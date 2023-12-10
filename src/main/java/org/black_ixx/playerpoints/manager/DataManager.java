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
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.database.migrations._1_Create_Tables;
import org.black_ixx.playerpoints.database.migrations._2_Add_Table_Username_Cache;
import org.black_ixx.playerpoints.listeners.PointsMessageListener;
import org.black_ixx.playerpoints.manager.ConfigurationManager.Setting;
import org.black_ixx.playerpoints.models.PendingTransaction;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DataManager extends AbstractDataManager implements Listener {

    private final Set<UUID> locks = ConcurrentHashMap.newKeySet();
    private LoadingCache<UUID, Integer> pointsCache;
    private final Map<UUID, Deque<PendingTransaction>> pendingTransactions;
    private final Map<UUID, String> pendingUsernameUpdates;

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.pendingTransactions = new ConcurrentHashMap<>();
        this.pendingUsernameUpdates = new ConcurrentHashMap<>();

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
        Bukkit.getScheduler().runTaskTimerAsynchronously(rosePlugin, this::update, 10L, 10L);
    }

    @Override
    public void reload() {
        super.reload();

        this.pointsCache = CacheBuilder.newBuilder()
                .concurrencyLevel(2)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .refreshAfterWrite(Setting.CACHE_DURATION.getInt(), TimeUnit.SECONDS)
                .build(new CacheLoader<UUID, Integer>() {
                    @Override
                    public Integer load(UUID uuid) throws Exception {
                        return DataManager.this.getPoints(uuid);
                    }
                });
    }

    @Override
    public void disable() {
        this.update();

        this.pointsCache.invalidateAll();
        this.pendingTransactions.clear();
        this.pendingUsernameUpdates.clear();

        super.disable();
    }

    /**
     * Pushes any points changes to the database and removes stale cache entries
     */
    private void update() {
        // Push any points changes to the database
        Map<UUID, Integer> transactions = new HashMap<>();
        Map<UUID, Deque<PendingTransaction>> processingPendingTransactions;
        synchronized (this.pendingTransactions) {
            processingPendingTransactions = new HashMap<>(this.pendingTransactions);
            this.pendingTransactions.clear();
        }

        for (Map.Entry<UUID, Deque<PendingTransaction>> entry : processingPendingTransactions.entrySet()) {
            UUID uuid = entry.getKey();
            int points = this.getEffectivePoints(uuid, entry.getValue());
            this.pointsCache.put(uuid, points);
            transactions.put(uuid, points);
        }

        if (!transactions.isEmpty())
            this.updatePoints(transactions);

        if (!this.pendingUsernameUpdates.isEmpty()) {
            this.updateCachedUsernames(this.pendingUsernameUpdates);
            this.pendingUsernameUpdates.clear();
        }
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
    }

    /**
     * Gets the effective amount of points that a player has (includes pending transactions)
     *
     * @param playerId The player ID to use to get the points
     * @return the effective points value
     */
    public int getEffectivePoints(UUID playerId) {
        return this.getEffectivePoints(playerId, this.pendingTransactions.get(playerId));
    }

    private int getEffectivePoints(UUID playerId, Deque<PendingTransaction> transactions) {
        // Get the cached amount or fetch it fresh from the database
        int points;
        try {
            points = this.pointsCache.get(playerId);
        } catch (ExecutionException e) {
            e.printStackTrace();
            points = 0;
        }

        // Apply any pending transactions
        if (transactions != null) {
            for (PendingTransaction transaction : transactions) {
                switch (transaction.getType()) {
                    case SET:
                        points = transaction.getAmount();
                        break;

                    case OFFSET:
                        points += transaction.getAmount();
                        break;
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
        this.pointsCache.invalidate(uuid);
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
            int startingBalance = Setting.STARTING_BALANCE.getInt();
            this.setPoints(playerId, startingBalance);
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
     * @param playerId The Player to set the points of
     * @param amount The amount to set to
     * @return true if the transaction was successful, false otherwise
     */
    public boolean setPoints(UUID playerId, int amount) {
        if (amount < 0)
            return false;

        this.getPendingTransactions(playerId).add(new PendingTransaction(PendingTransaction.TransactionType.SET, amount));
        return true;
    }

    /**
     * Adds a pending transaction to offset the player's points by a specified amount
     *
     * @param playerId The Player to offset the points of
     * @param amount The amount to offset by
     * @return true if the transaction was successful, false otherwise
     */
    public boolean offsetPoints(UUID playerId, int amount) {
        if (this.locks.contains(playerId))
            return false;

        try {
            this.locks.add(playerId);

            int points = this.getEffectivePoints(playerId);
            if (points + amount < 0)
                return false;

            this.getPendingTransactions(playerId).add(new PendingTransaction(PendingTransaction.TransactionType.OFFSET, amount));
            return true;
        } finally {
            this.locks.remove(playerId);
        }
    }

    private void updatePoints(Map<UUID, Integer> transactions) {
        this.databaseConnector.connect(connection -> {
            String query = "REPLACE INTO " + this.getPointsTableName() + " (" + this.getUuidColumnName() + ", points) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (Map.Entry<UUID, Integer> entry : transactions.entrySet()) {
                    statement.setString(1, entry.getKey().toString());
                    statement.setInt(2, Math.max(0, entry.getValue()));
                    statement.addBatch();

                    // Update cached value
                    this.pointsCache.put(entry.getKey(), entry.getValue());

                    // Send update to BungeeCord if enabled
                    if (Setting.BUNGEECORD_SEND_UPDATES.getBoolean() && this.rosePlugin.isEnabled()) {
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
                statement.executeBatch();
            }
        });
    }

    public boolean offsetAllPoints(int amount) {
        if (amount == 0)
            return true;

        this.databaseConnector.connect(connection -> {
            String function = this.databaseConnector instanceof SQLiteConnector ? "MAX" : "GREATEST";
            String query = "UPDATE " + this.getPointsTableName() + " SET points = " + function + "(0, points + ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, amount);
                statement.executeUpdate();
            }
        });

        for (Player player : Bukkit.getOnlinePlayers())
            this.offsetPoints(player.getUniqueId(), amount);

        return true;
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
                           "LEFT JOIN " + this.getTablePrefix() + "username_cache c ON t.uuid = c.uuid " +
                           "ORDER BY points DESC" + (limit != null ? " LIMIT " + limit : "");
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    UUID uuid = UUID.fromString(result.getString(1));
                    String username = result.getString(2);
                    Integer pointsValue = this.pointsCache.getIfPresent(uuid);
                    if (pointsValue == null)
                        pointsValue = result.getInt(3);

                    if (username != null) {
                        players.add(new SortedPlayer(uuid, username, pointsValue));
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
                           "WHERE t.uuid IN (" + uuidList + ")";
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

    public void importData(SortedSet<SortedPlayer> data, Map<UUID, String> cachedUsernames) {
        this.databaseConnector.connect(connection -> {
            String purgeQuery = "DELETE FROM " + this.getPointsTableName();
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(purgeQuery);
            }

            String batchInsert = "INSERT INTO " + this.getPointsTableName() + " (" + this.getUuidColumnName() + ", points) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(batchInsert)) {
                for (SortedPlayer playerData : data) {
                    statement.setString(1, playerData.getUniqueId().toString());
                    statement.setInt(2, playerData.getPoints());
                    statement.addBatch();
                }
                statement.executeBatch();
            }

            if (!cachedUsernames.isEmpty())
                this.updateCachedUsernames(cachedUsernames);
        });
    }

    public boolean importLegacyTable(String tableName) {
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
            String query = "SELECT uuid FROM " + this.getTablePrefix() + "username_cache WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                ResultSet result = statement.executeQuery();
                if (result.next())
                    value.set(UUID.fromString(result.getString(1)));
            }
        });

        return value.get();
    }

    private String getPointsTableName() {
        if (ConfigurationManager.Setting.LEGACY_DATABASE_MODE.getBoolean()) {
            return ConfigurationManager.Setting.LEGACY_DATABASE_NAME.getString();
        } else {
            return super.getTablePrefix() + "points";
        }
    }

    private String getUuidColumnName() {
        if (ConfigurationManager.Setting.LEGACY_DATABASE_MODE.getBoolean()) {
            return "playername";
        } else {
            return "uuid";
        }
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return Arrays.asList(
                _1_Create_Tables.class,
                _2_Add_Table_Username_Cache.class
        );
    }

}
