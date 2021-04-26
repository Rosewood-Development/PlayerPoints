package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.black_ixx.playerpoints.models.SortedPlayer;

public class DataManager extends AbstractDataManager {

    private final PointsCacheManager pointsCacheManager;

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.pointsCacheManager = rosePlugin.getManager(PointsCacheManager.class);
    }

    public CompletableFuture<Integer> getPoints(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            AtomicInteger value = new AtomicInteger();
            this.databaseConnector.connect(connection -> {
                String query = "SELECT points FROM " + this.getTablePrefix() + "points WHERE uuid = ?";
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
        });
    }

    public CompletableFuture<Boolean> setPoints(UUID playerId, int amount) {
        if (amount < 0)
            return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            this.databaseConnector.connect(connection -> {
                if (this.playerEntryExists(playerId).join()) {
                    String query = "UPDATE " + this.getTablePrefix() + "points SET points = ? WHERE uuid = ?";
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
        });
    }

    public CompletableFuture<Boolean> playerEntryExists(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            AtomicBoolean value = new AtomicBoolean();
            this.databaseConnector.connect(connection -> {
                String query = "SELECT 1 FROM " + this.getTablePrefix() + "points WHERE uuid = ?";
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
                String query = "SELECT uuid, points FROM " + this.getTablePrefix() + "points";
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
        });
    }

    public CompletableFuture<Void> importData(SortedSet<SortedPlayer> data) {
        return CompletableFuture.supplyAsync(() -> {
            this.databaseConnector.connect(connection -> {
                String purgeQuery = "DELETE FROM " + this.getTablePrefix() + "points";
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(purgeQuery);
                    this.pointsCacheManager.reset();
                }

                String batchInsert = "INSERT INTO " + this.getTablePrefix() + "points (uuid, points) VALUES (?, ?)";
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
        });
    }

    private void createEntry(UUID playerId, int value) {
        this.databaseConnector.connect(connection -> {
            String insert = "INSERT INTO " + this.getTablePrefix() + "points (uuid, points) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                statement.setString(1, playerId.toString());
                statement.setInt(2, value);
                statement.executeUpdate();
                this.pointsCacheManager.updatePoints(playerId, 0);
            }
        });
    }

}
