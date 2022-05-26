package org.black_ixx.playerpoints.database.migrations;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import dev.rosewood.rosegarden.database.MySQLConnector;
import dev.rosewood.rosegarden.database.SQLiteConnector;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Creates the database tables
 */
public class _1_Create_Tables extends DataMigration {

    public _1_Create_Tables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        String autoIncrement = connector instanceof MySQLConnector ? " AUTO_INCREMENT" : "";

        String query;
        if (connector instanceof SQLiteConnector) {
            query = "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?";
        } else {
            query = "SHOW TABLES LIKE ?";
        }

        // Check if the old table already exists, if it does then try renaming the table to playerpoints_points and the 'playername' column to 'uuid'
        boolean exists;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "playerpoints");
            exists = statement.executeQuery().next();
        }

        if (exists) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("ALTER TABLE playerpoints RENAME TO " + tablePrefix + "points");
            } catch (Exception ignored) { }

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("ALTER TABLE " + tablePrefix + "points RENAME COLUMN playername TO uuid");
            } catch (Exception ignored) { }
        } else {
            // Create points table
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE " + tablePrefix + "points (" +
                        "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                        "uuid VARCHAR(36) NOT NULL, " +
                        "points INTEGER NOT NULL, " +
                        "UNIQUE (uuid)" +
                        ")");
            }
        }

        // Attempt to import legacy data if it exists and we are using SQLite
        // First make sure there isn't already any data in the database for some reason
        PlayerPoints plugin = PlayerPoints.getInstance();
        DataManager dataManager = plugin.getManager(DataManager.class);
        File file = new File(plugin.getDataFolder(), "storage.yml");
        if (!dataManager.doesDataExist() && file.exists() && connector instanceof SQLiteConnector) {
            try {
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection section = configuration.getConfigurationSection("Points");
                if (section == null)
                    section = configuration.getConfigurationSection("Players");

                if (section == null) {
                    plugin.getLogger().warning("Malformed storage.yml file.");
                    return;
                }

                SortedSet<SortedPlayer> data = new TreeSet<>();
                for (String uuid : section.getKeys(false))
                    data.add(new SortedPlayer(UUID.fromString(uuid), section.getInt(uuid)));

                plugin.getManager(DataManager.class).importData(data, Collections.emptyMap());
                plugin.getLogger().warning("Imported legacy data from storage.yml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
