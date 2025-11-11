package org.black_ixx.playerpoints.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.SQLiteConnector;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.config.SettingKey;
import org.black_ixx.playerpoints.database.migrations._1_Create_Tables;
import org.black_ixx.playerpoints.database.migrations._2_Add_Table_Username_Cache;
import org.black_ixx.playerpoints.database.migrations._3_Add_Table_Transaction_Log;
import org.black_ixx.playerpoints.listeners.PointsMessageListener;
import org.black_ixx.playerpoints.models.PendingTransaction;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.black_ixx.playerpoints.models.TransactionType;
import org.black_ixx.playerpoints.models.UpdateType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DataManager extends AbstractDataManager implements Listener {

    private ScheduledTask updateTask;
    private ScheduledTask accountUpdateTask;
    private LoadingCache<UUID, Integer> pointsCache;
    private final Map<UUID, Deque<PendingTransaction>> pendingTransactions;
    private final Map<UUID, String> pendingUsernameUpdates;
    private final Set<String> accountToNameMap;
    private boolean isModernSqlite;

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.pendingTransactions = new ConcurrentHashMap<>();
        this.pendingUsernameUpdates = new ConcurrentHashMap<>();
        this.accountToNameMap = Collections.newSetFromMap(new ConcurrentHashMap<>());

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        super.reload();

        this.pointsCache = CacheBuilder.newBuilder()
                .concurrencyLevel(2)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .refreshAfterWrite(org.black_ixx.playerpoints.config.SettingKey.CACHE_DURATION.get(), TimeUnit.SECONDS)
                .build(new CacheLoader<UUID, Integer>() {
                    @Override
                    public Integer load(UUID uuid) {
                        return DataManager.this.getPoints(uuid);
                    }
                });

        this.updateTask = this.rosePlugin.getScheduler().runTaskTimerAsync(this::update, 10L, 10L);
        if (SettingKey.TAB_COMPLETE_SHOW_ALL_PLAYERS.get())
            this.accountUpdateTask = this.rosePlugin.getScheduler().runTaskTimerAsync(this::updateAccountUUIDMaps, 10L, org.black_ixx.playerpoints.config.SettingKey.CACHED_ACCOUNT_LIST_REFRESH_INTERVAL.get() * 20);

        if (this.databaseConnector instanceof SQLiteConnector) {
            this.databaseConnector.connect(connection -> {
                // Get SQLite version
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT sqlite_version()")) {
                    if (rs.next()) {
                        String version = rs.getString(1);
                        // Parse version to check if it's >= 3.24.0
                        String[] parts = version.split("\\.");
                        int major = Integer.parseInt(parts[0]);
                        int minor = Integer.parseInt(parts[1]);
                        this.isModernSqlite = (major > 3) || (major == 3 && minor >= 24);
                    }
                }
            });
        } else {
            this.isModernSqlite = false;
        }
    }

    @Override
    public void disable() {
        if (this.updateTask != null) {
            this.updateTask.cancel();
            this.updateTask = null;
        }

        if (this.accountUpdateTask != null) {
            this.accountUpdateTask.cancel();
            this.accountUpdateTask = null;
        }

        this.update();

        this.pointsCache.invalidateAll();
        this.pendingTransactions.clear();
        this.pendingUsernameUpdates.clear();
        this.accountToNameMap.clear();

        super.disable();
    }

    /**
     * Pushes any pending points changes to the database
     */
    private void update() {
        // Push any points changes to the database
        Map<UUID, Deque<PendingTransaction>> processingPendingTransactions;
        synchronized (this.pendingTransactions) {
            if (this.pendingTransactions.isEmpty())
                return;

            processingPendingTransactions = new HashMap<>(this.pendingTransactions);
            this.pendingTransactions.clear();
        }

        this.updatePoints(processingPendingTransactions);

        if (!this.pendingUsernameUpdates.isEmpty()) {
            this.updateCachedUsernames(this.pendingUsernameUpdates);
            this.pendingUsernameUpdates.clear();
        }
    }

    private void updateAccountUUIDMaps() {
        Set<String> accountToNameMap = new HashSet<>();
        this.databaseConnector.connect(connection -> {
            String accountUUIDMapQuery = "SELECT username FROM " + this.getTablePrefix() + "username_cache";
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(accountUUIDMapQuery);
                while (result.next())
                    accountToNameMap.add(result.getString(1));
            }
        });

        this.accountToNameMap.clear();
        this.accountToNameMap.addAll(accountToNameMap);
    }

    /**
     * @return a set of all account names registered by PlayerPoints, will be empty if tab-complete-show-all-players is false
     */
    public Set<String> getAllAccountNames() {
        return this.accountToNameMap;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED)
            this.pointsCache.put(event.getUniqueId(), this.getPoints(event.getUniqueId()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.pendingUsernameUpdates.put(player.getUniqueId(), player.getName());
        this.accountToNameMap.add(player.getName());
    }

    /**
     * Gets the effective amount of points that a player has (includes pending transactions)
     *
     * @param playerId The player ID to use to get the points
     * @return the effective points value
     */
    public int getEffectivePoints(UUID playerId) {
        return this.getEffectivePoints(playerId, this.pendingTransactions.get(playerId), null);
    }

    public int getEffectivePoints(UUID playerId, int points) {
        return this.getEffectivePoints(playerId, this.pendingTransactions.get(playerId), points);
    }

    private int getEffectivePoints(UUID playerId, Deque<PendingTransaction> transactions, Integer points) {
        // Get the cached amount or fetch it fresh from the database
        if (points == null) {
            try {
                points = this.pointsCache.get(playerId);
            } catch (ExecutionException e) {
                e.printStackTrace();
                points = 0;
            }
        }

        // Apply any pending transactions
        if (transactions != null) {
            for (PendingTransaction transaction : transactions) {
                switch (transaction.getUpdateType()) {
                    case SET:
                        points = transaction.getAmount();
                        break;

                    case OFFSET:
                        points += transaction.getAmount();
                        break;

                    default:
                        throw new IllegalStateException("Invalid transaction type");
                }
            }
        }

        return points;
    }

    /**
     * Refreshes a player's points to the value in the database if they are online
     *
     * @param uuid The player's UUID
     */
    public void refreshPoints(UUID uuid) {
        this.rosePlugin.getScheduler().runTaskAsync(() -> this.pointsCache.put(uuid, this.getPoints(uuid)));
    }

    /**
     * Performs a database query and hangs the current thread, also caches the points entry
     *
     * @param playerId The UUID of the Player
     * @return the amount of points the Player has
     */
    private int getPoints(UUID playerId) {
        AtomicInteger value = new AtomicInteger();
        AtomicBoolean generate = new AtomicBoolean(false);
        this.databaseConnector.connect(connection -> {
            String query = "SELECT points FROM " + this.getPointsTableName() + " WHERE " + this.getUuidColumnName() + " = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerId.toString());
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    value.set(result.getInt(1));
                } else {
                    generate.set(true);
                }
            }
        });

        if (generate.get()) {
            int startingBalance = org.black_ixx.playerpoints.config.SettingKey.STARTING_BALANCE.get();
            this.setPoints(TransactionType.SET, playerId, "Starting balance", null, startingBalance);
            value.set(startingBalance);
        }

        return value.get();
    }

    private Deque<PendingTransaction> getPendingTransactions(UUID playerId) {
        return this.pendingTransactions.computeIfAbsent(playerId, x -> new ConcurrentLinkedDeque<>());
    }

    /**
     * Adds a pending transaction to set the player's points to a specified amount
     *
     * @param transactionType The type of transaction
     * @param playerId The Player to set the points of
     * @param sourceDescription The description of how the points were given
     * @param source The UUID source of the points, nullable
     * @param amount The amount to set to
     * @return true if the transaction was successful, false otherwise
     */
    public boolean setPoints(TransactionType transactionType, UUID playerId, String sourceDescription, UUID source, int amount) {
        if (amount < 0)
            return false;

        this.getPendingTransactions(playerId).add(new PendingTransaction(UpdateType.SET, transactionType, sourceDescription, source, amount));
        return true;
    }

    /**
     * Adds a pending transaction to offset the player's points by a specified amount
     *
     * @param transactionType The type of transaction
     * @param playerId The Player to offset the points of
     * @param amount The amount to offset by
     * @return true if the transaction was successful, false otherwise
     */
    public boolean offsetPoints(TransactionType transactionType, UUID playerId, String sourceDescription, UUID source, int amount) {
        int points = this.getEffectivePoints(playerId);
        if (points + amount < 0)
            return false;

        this.getPendingTransactions(playerId).add(new PendingTransaction(UpdateType.OFFSET, transactionType, sourceDescription, source, amount));
        return true;
    }

    private void updatePoints(Map<UUID, Deque<PendingTransaction>> transactionsMap) {
        this.databaseConnector.connect(connection -> {
            String offsetQuery = "INSERT INTO " + this.getPointsTableName() + " (" + this.getUuidColumnName() + ", points) VALUES (?, ?) ";
            String setQuery = offsetQuery;
            String getQuery = "SELECT points FROM " + this.getPointsTableName() + " WHERE " + this.getUuidColumnName() + " = ?";
            boolean mysql = false;
            if (this.databaseConnector instanceof SQLiteConnector) {
                if (this.isModernSqlite) {
                    offsetQuery += "ON CONFLICT(" + this.getUuidColumnName() + ") DO UPDATE SET points = MAX(0, points + ?)";
                    setQuery += "ON CONFLICT(" + this.getUuidColumnName() + ") DO UPDATE SET points = ?";
                } else {
                    offsetQuery = "INSERT OR REPLACE INTO " + this.getPointsTableName() + " (" + this.getUuidColumnName() + ", points) " +
                            "VALUES (?, COALESCE((SELECT MAX(0, points + ?) FROM " + this.getPointsTableName() + " WHERE " + this.getUuidColumnName() + " = ?), ?))";
                    setQuery = "INSERT OR REPLACE INTO " + this.getPointsTableName() + " (" + this.getUuidColumnName() + ", points) VALUES (?, ?)";
                }
            } else {
                offsetQuery += "ON DUPLICATE KEY UPDATE points = GREATEST(0, points + ?)";
                setQuery += "ON DUPLICATE KEY UPDATE points = ?";
                mysql = true;
            }

            for (Map.Entry<UUID, Deque<PendingTransaction>> entry : transactionsMap.entrySet()) {
                UUID uuid = entry.getKey();
                for (PendingTransaction transaction : entry.getValue()) {
                    switch (transaction.getUpdateType()) {
                        case OFFSET:
                            try (PreparedStatement statement = connection.prepareStatement(offsetQuery)) {
                                statement.setString(1, uuid.toString());
                                statement.setInt(2, transaction.getAmount());
                                if (mysql || this.isModernSqlite) {
                                    statement.setInt(3, transaction.getAmount());
                                } else {
                                    statement.setString(3, uuid.toString());
                                    statement.setInt(4, transaction.getAmount());
                                }
                                statement.executeUpdate();
                            }
                            break;

                        case SET:
                            try (PreparedStatement statement = connection.prepareStatement(setQuery)) {
                                statement.setString(1, uuid.toString());
                                statement.setInt(2, transaction.getAmount());
                                if (mysql || this.isModernSqlite) {
                                    statement.setInt(3, transaction.getAmount());
                                }
                                statement.executeUpdate();
                            }
                            break;

                        default:
                            throw new IllegalStateException("Invalid transaction type");
                    }

                    this.logTransaction(connection, uuid, transaction);
                }

                try (PreparedStatement statement = connection.prepareStatement(getQuery)) {
                    statement.setString(1, uuid.toString());
                    ResultSet result = statement.executeQuery();
                    if (result.next()) {
                        this.pointsCache.put(uuid, result.getInt(1));
                    } else {
                        this.pointsCache.invalidate(uuid);
                    }
                }

                // Send update to BungeeCord if enabled
                if (org.black_ixx.playerpoints.config.SettingKey.BUNGEECORD_SEND_UPDATES.get() && this.rosePlugin.isEnabled()) {
                    ByteArrayDataOutput output = ByteStreams.newDataOutput();
                    output.writeUTF("Forward");
                    output.writeUTF("ONLINE");
                    output.writeUTF(PointsMessageListener.REFRESH_SUBCHANNEL);

                    byte[] bytes = entry.getKey().toString().getBytes(StandardCharsets.UTF_8);
                    output.writeShort(bytes.length);
                    output.write(bytes);

                    Player attachedPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
                    if (attachedPlayer != null)
                        attachedPlayer.sendPluginMessage(this.rosePlugin, PointsMessageListener.CHANNEL, output.toByteArray());
                }
            }
        });
    }

    public void offsetAllPoints(int amount) {
        if (amount == 0)
            return;

        this.databaseConnector.connect(connection -> {
            String function = this.databaseConnector instanceof SQLiteConnector ? "MAX" : "GREATEST";
            String query = "UPDATE " + this.getPointsTableName() + " SET points = " + function + "(0, points + ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, amount);
                statement.executeUpdate();
            }
        });

        for (Player player : Bukkit.getOnlinePlayers())
            this.pointsCache.put(player.getUniqueId(), this.getPoints(player.getUniqueId()));
    }

    public boolean doesDataExist() {
        AtomicInteger count = new AtomicInteger();
        this.databaseConnector.connect(connection -> {
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM " + this.getPointsTableName());
                result.next();
                count.set(result.getInt(1));
            }
        });
        return count.get() > 0;
    }

    public List<SortedPlayer> getTopSortedPoints(Integer limit) {
        List<SortedPlayer> players = new ArrayList<>();
        this.databaseConnector.connect(connection -> {
            String query = "SELECT t." + this.getUuidColumnName() + ", username, points FROM " + this.getPointsTableName() + " t " +
                           "LEFT JOIN " + this.getTablePrefix() + "username_cache c ON t." + this.getUuidColumnName() + " = c.uuid " +
                           "ORDER BY points DESC" + (limit != null ? " LIMIT " + limit : "");
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    UUID uuid = UUID.fromString(result.getString(1));
                    String username = result.getString(2);
                    int pointsValue = this.getEffectivePoints(uuid, result.getInt(3));

                    if (username != null) {
                        if (org.black_ixx.playerpoints.config.SettingKey.SHOW_NON_PLAYER_ACCOUNTS_ON_LEADERBOARDS.get()) {
                            players.add(new SortedPlayer(uuid, username, pointsValue));
                        } else {
                            if (!username.startsWith("*")) {
                                players.add(new SortedPlayer(uuid, username, pointsValue));
                            }
                        }
                    } else {
                        players.add(new SortedPlayer(uuid, pointsValue));
                    }
                }
            }
        });
        return players;
    }

    public Map<UUID, Long> getOnlineTopSortedPointPositions() {
        Map<UUID, Long> players = new HashMap<>();
        if (Bukkit.getOnlinePlayers().isEmpty())
            return players;

        String uuidList = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).map(x -> "'" + x + "'").collect(Collectors.joining(", "));
        this.databaseConnector.connect(connection -> {
            String tableName = this.getPointsTableName();
            String query = "SELECT t." + this.getUuidColumnName() + ", (SELECT COUNT(*) FROM " + tableName + " x WHERE x.points >= t.points) AS position " +
                           "FROM " + tableName + " t " +
                           "WHERE t." + this.getUuidColumnName() + " IN (" + uuidList + ")";
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    UUID uuid = UUID.fromString(result.getString(1));
                    players.put(uuid, result.getLong(2));
                }
            }
        });
        return players;
    }

    public UUID createNonPlayerAccount(String accountName) {
        UUID existing = this.lookupCachedUUID(accountName);
        if (existing != null)
            return existing;

        UUID uuid = UUID.randomUUID();
        this.pendingUsernameUpdates.put(uuid, accountName);
        int startingBalance = org.black_ixx.playerpoints.config.SettingKey.STARTING_BALANCE.get();
        this.setPoints(TransactionType.SET, uuid, "Starting balance", null, startingBalance);
        this.accountToNameMap.add(accountName);
        return uuid;
    }

    public void deleteAccount(UUID accountID) {
        this.pointsCache.invalidate(accountID);
        this.pendingTransactions.remove(accountID);

        this.databaseConnector.connect(connection -> {
            String usernameDeleteQuery = "DELETE FROM " + this.getPointsTableName() + " WHERE " + this.getUuidColumnName() + " = ?";
            try (PreparedStatement statement = connection.prepareStatement(usernameDeleteQuery)) {
                statement.setString(1, accountID.toString());
                statement.executeUpdate();
            }
            String pointsDeleteQuery = "DELETE FROM " + this.getTablePrefix() + "username_cache WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(pointsDeleteQuery)) {
                statement.setString(1, accountID.toString());
                statement.executeUpdate();
            }
        });

        this.accountToNameMap.remove(accountID);
    }

    public void importData(Map<UUID, Integer> data, Map<UUID, String> cachedUsernames) {
        this.pointsCache.invalidateAll();
        this.pendingTransactions.clear();

        this.databaseConnector.connect(connection -> {
            String purgeQuery = "DELETE FROM " + this.getPointsTableName();
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(purgeQuery);
            }

            String batchInsert = "INSERT INTO " + this.getPointsTableName() + " (" + this.getUuidColumnName() + ", points) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(batchInsert)) {
                for (Map.Entry<UUID, Integer> entry : data.entrySet()) {
                    statement.setString(1, entry.getKey().toString());
                    statement.setInt(2, entry.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
            }

            if (!cachedUsernames.isEmpty())
                this.updateCachedUsernames(cachedUsernames);
        });
    }

    public boolean importLegacyTable(String tableName) {
        this.pointsCache.invalidateAll();
        this.pendingTransactions.clear();

        AtomicBoolean value = new AtomicBoolean();
        this.databaseConnector.connect(connection -> {
            try {
                String selectQuery = "SELECT playername, points FROM " + tableName;
                Map<UUID, Integer> points = new HashMap<>();
                try (Statement statement = connection.createStatement()) {
                    ResultSet result = statement.executeQuery(selectQuery);
                    while (result.next()) {
                        UUID uuid = UUID.fromString(result.getString(1));
                        int pointValue = result.getInt(2);
                        points.put(uuid, pointValue);
                    }
                }

                boolean isSqlite = this.databaseConnector instanceof SQLiteConnector;
                String insertQuery;
                if (isSqlite) {
                    insertQuery = "REPLACE INTO " + this.getPointsTableName() + " (" + this.getUuidColumnName() + ", points) VALUES (?, ?)";
                } else {
                    insertQuery = "INSERT INTO " + this.getPointsTableName() + " (" + this.getUuidColumnName() + ", points) VALUES (?, ?) ON DUPLICATE KEY UPDATE points = ?";
                }

                try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                    for (Map.Entry<UUID, Integer> entry : points.entrySet()) {
                        statement.setString(1, entry.getKey().toString());
                        statement.setInt(2, entry.getValue());
                        if (!isSqlite)
                            statement.setInt(3, entry.getValue());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }

                value.set(true);
            } catch (Exception e) {
                value.set(false);
                e.printStackTrace();
            }
        });
        return value.get();
    }

    public void updateCachedUsernames(Map<UUID, String> cachedUsernames) {
        this.databaseConnector.connect(connection -> {
            String query;
            boolean isSqlite = this.databaseConnector instanceof SQLiteConnector;
            if (isSqlite) {
                query = "REPLACE INTO " + this.getTablePrefix() + "username_cache (uuid, username) VALUES (?, ?)";
            } else {
                query = "INSERT INTO " + this.getTablePrefix() + "username_cache (uuid, username) VALUES (?, ?) ON DUPLICATE KEY UPDATE username = ?";
            }

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (Map.Entry<UUID, String> entry : cachedUsernames.entrySet()) {
                    statement.setString(1, entry.getKey().toString());
                    statement.setString(2, entry.getValue());
                    if (!isSqlite)
                        statement.setString(3, entry.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        });
    }

    public String lookupCachedUsername(UUID uuid) {
        AtomicReference<String> value = new AtomicReference<>();
        this.databaseConnector.connect(connection -> {
            String query = "SELECT username FROM " + this.getTablePrefix() + "username_cache WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                if (result.next())
                    value.set(result.getString(1));
            }
        });

        String name = value.get();
        if (name == null) {
            return "Unknown";
        } else {
            return name;
        }
    }

    public UUID lookupCachedUUID(String username) {
        AtomicReference<UUID> value = new AtomicReference<>();
        this.databaseConnector.connect(connection -> {
            String query = "SELECT uuid FROM " + this.getTablePrefix() + "username_cache WHERE LOWER(username) = LOWER(?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                ResultSet result = statement.executeQuery();
                if (result.next())
                    value.set(UUID.fromString(result.getString(1)));
            }
        });

        return value.get();
    }

    private void logTransaction(Connection connection, UUID receiver, PendingTransaction transaction) throws SQLException {
        String query = "INSERT INTO " + this.getTablePrefix() + "transaction_log (transaction_type, description, source, receiver, amount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, transaction.getTransactionType().name());
            statement.setString(2, transaction.getSourceDescription());
            if (transaction.getSource() == null) {
                statement.setNull(3, Types.VARCHAR);
            } else {
                statement.setString(3, transaction.getSource().toString());
            }
            statement.setString(4, receiver.toString());
            statement.setInt(5, transaction.getAmount());
            statement.executeUpdate();
        }
    }

    private String getPointsTableName() {
        if (org.black_ixx.playerpoints.config.SettingKey.LEGACY_DATABASE_MODE.get()) {
            return org.black_ixx.playerpoints.config.SettingKey.LEGACY_DATABASE_NAME.get();
        } else {
            return super.getTablePrefix() + "points";
        }
    }

    private String getUuidColumnName() {
        if (org.black_ixx.playerpoints.config.SettingKey.LEGACY_DATABASE_MODE.get()) {
            return "playername";
        } else {
            return "uuid";
        }
    }

    @Override
    public List<Supplier<? extends DataMigration>> getDataMigrations() {
        return Arrays.asList(
                _1_Create_Tables::new,
                _2_Add_Table_Username_Cache::new,
                _3_Add_Table_Transaction_Log::new
        );
    }

}
