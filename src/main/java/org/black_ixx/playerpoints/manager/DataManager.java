package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.SQLiteConnector;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.black_ixx.playerpoints.models.SortedPlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DataManager extends AbstractDataManager {

    private final ExecutorManager executorManager;
    private final PointsCacheManager pointsCacheManager;

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.executorManager = rosePlugin.getManager(ExecutorManager.class);
        this.pointsCacheManager = rosePlugin.getManager(PointsCacheManager.class);
    }

    public CompletableFuture<Integer> getPoints(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            AtomicInteger value = new AtomicInteger();
            this.databaseConnector.connect(connection -> {
                String query = "SELECT points FROM " + this.getPointsTableName() + " WHERE " + this.getUuidColumnName() + " = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, playerId.toString());
                    ResultSet result = statement.executeQuery();
                    if (result.next()) {
                        value.set(result.getInt(1));
                    } else {
                        this.createEntry(playerId, 0);
                    }
                }
            });
            this.pointsCacheManager.updatePoints(playerId, value.get());
            return value.get();
        }, executorManager.getExecutor());
    }

    public CompletableFuture<Boolean> setPoints(UUID playerId, int amount) {
        if (amount < 0)
            return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            this.databaseConnector.connect(connection -> {
                if (this.playerEntryExists(playerId).join()) {
                    String query = "UPDATE " + this.getPointsTableName() + " SET points = ? WHERE " + this.getUuidColumnName() + " = ?";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setInt(1, amount);
                        statement.setString(2, playerId.toString());
                        statement.executeUpdate();
                        this.pointsCacheManager.updatePoints(playerId, amount);
                    }
                } else {
                    this.createEntry(playerId, amount);
                }
            });
            return true;
        }, executorManager.getExecutor());
    }

    public CompletableFuture<Boolean> offsetPoints(List<UUID> playerIds, int amount) {
        if (amount == 0)
            return CompletableFuture.completedFuture(true);

        return CompletableFuture.supplyAsync(() -> {
            this.databaseConnector.connect(connection -> {
                String function = this.databaseConnector instanceof SQLiteConnector ? "MAX" : "GREATEST";
                String batchQuery = "UPDATE " + this.getPointsTableName() + " SET points = " + function + "(0, points + ?) WHERE " + this.getUuidColumnName() + " = ?";
                try (PreparedStatement statement = connection.prepareStatement(batchQuery)) {
                    for (UUID uuid : playerIds) {
                        statement.setInt(1, amount);
                        statement.setString(2, uuid.toString());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }
                this.pointsCacheManager.reset();
            });
            return true;
        }, executorManager.getExecutor());
    }

    public CompletableFuture<Boolean> offsetAllPoints(int amount) {
        if (amount == 0)
            return CompletableFuture.completedFuture(true);

        return CompletableFuture.supplyAsync(() -> {
            this.databaseConnector.connect(connection -> {
                String function = this.databaseConnector instanceof SQLiteConnector ? "MAX" : "GREATEST";
                String query = "UPDATE " + this.getPointsTableName() + " SET points = " + function + "(0, points + ?)";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, amount);
                    statement.executeUpdate();
                }
                this.pointsCacheManager.reset();
            });
            return true;
        }, executorManager.getExecutor());
    }

    public CompletableFuture<Boolean> playerEntryExists(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            AtomicBoolean value = new AtomicBoolean();
            this.databaseConnector.connect(connection -> {
                String query = "SELECT 1 FROM " + this.getPointsTableName() + " WHERE " + this.getUuidColumnName() + " = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, playerId.toString());
                    value.set(statement.executeQuery().next());
                }
            });
            return value.get();
        });
    }

    public CompletableFuture<SortedSet<SortedPlayer>> getAllPoints() {
        return CompletableFuture.supplyAsync(() -> {
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
        }, executorManager.getExecutor());
    }

    public CompletableFuture<Void> importData(SortedSet<SortedPlayer> data) {
        return CompletableFuture.supplyAsync(() -> {
            this.databaseConnector.connect(connection -> {
                String purgeQuery = "DELETE FROM " + this.getPointsTableName();
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(purgeQuery);
                    this.pointsCacheManager.reset();
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
            return null;
        }, executorManager.getExecutor());
    }

    public CompletableFuture<Boolean> importLegacyTable(String tableName) {
        return CompletableFuture.supplyAsync(() -> {
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

                    this.pointsCacheManager.reset();
                    value.set(true);
                } catch (Exception e) {
                    value.set(false);
                    e.printStackTrace();
                }
            });
            return value.get();
        }, executorManager.getExecutor());
    }

    private void createEntry(UUID playerId, int value) {
        this.databaseConnector.connect(connection -> {
            String insert = "INSERT INTO " + this.getPointsTableName() + " (" + this.getUuidColumnName() + ", points) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                statement.setString(1, playerId.toString());
                statement.setInt(2, value);
                statement.executeUpdate();
                this.pointsCacheManager.updatePoints(playerId, 0);
            }
        });
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
