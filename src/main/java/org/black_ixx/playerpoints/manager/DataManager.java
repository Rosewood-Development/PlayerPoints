package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.SQLiteConnector;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.black_ixx.playerpoints.models.PendingTransaction;
import org.black_ixx.playerpoints.models.PointsValue;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class DataManager extends AbstractDataManager implements Listener {

    private final Map<UUID, PointsValue> pointsCache;
    private final Map<UUID, Deque<PendingTransaction>> pendingTransactions;

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.pointsCache = new ConcurrentHashMap<>();
        this.pendingTransactions = Collections.synchronizedMap(new HashMap<>());

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
        Bukkit.getScheduler().runTaskTimerAsynchronously(rosePlugin, this::update, 10L, 10L);
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
            this.pointsCache.put(uuid, new PointsValue(points));
            transactions.put(uuid, points);
        }

        if (!transactions.isEmpty())
            this.updatePoints(transactions);

        // Remove stale cache entries
        synchronized (this.pointsCache) {
            this.pointsCache.values().removeIf(PointsValue::isStale);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            this.pointsCache.remove(event.getUniqueId());
            this.getPoints(event.getUniqueId());
        }
    }

    public void resetCache() {
        this.update();
        this.pointsCache.clear();
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
        if (this.pointsCache.containsKey(playerId)) {
            points = this.pointsCache.get(playerId).getValue();
        } else {
            points = this.getPoints(playerId);
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
     * Performs a database query and hangs the current thread, also caches the points entry
     *
     * @param playerId The UUID of the Player
     * @return the amount of points the Player has
     */
    private int getPoints(UUID playerId) {
        AtomicInteger value = new AtomicInteger();
        this.databaseConnector.connect(connection -> {
            String query = "SELECT points FROM " + this.getPointsTableName() + " WHERE " + this.getUuidColumnName() + " = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerId.toString());
                ResultSet result = statement.executeQuery();
                if (result.next())
                    value.set(result.getInt(1));
            }
        });
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
        int points = this.getEffectivePoints(playerId);
        if (points + amount < 0)
            return false;

        this.getPendingTransactions(playerId).add(new PendingTransaction(PendingTransaction.TransactionType.OFFSET, amount));
        return true;
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
                    this.pointsCache.computeIfAbsent(entry.getKey(), x -> new PointsValue(entry.getValue())).setValue(entry.getValue());
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
            if (this.pointsCache.containsKey(player.getUniqueId()))
                this.offsetPoints(player.getUniqueId(), amount);

        return true;
    }

    public SortedSet<SortedPlayer> getAllPoints() {
        SortedSet<SortedPlayer> players = new TreeSet<>();
        this.databaseConnector.connect(connection -> {
            String query = "SELECT " + this.getUuidColumnName() + ", points FROM " + this.getPointsTableName();
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    UUID uuid = UUID.fromString(result.getString(1));
                    int points = result.getInt(2);
                    players.add(new SortedPlayer(uuid, points));
                }
            }
        });
        return players;
    }

    public void importData(SortedSet<SortedPlayer> data) {
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

                String insertQuery = "INSERT INTO " + this.getPointsTableName() + " (" + this.getUuidColumnName() + ", points) VALUES (?, ?) ON DUPLICATE KEY UPDATE points = ?";

                try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                    for (Map.Entry<UUID, Integer> entry : points.entrySet()) {
                        statement.setString(1, entry.getKey().toString());
                        statement.setInt(2, entry.getValue());
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

}
