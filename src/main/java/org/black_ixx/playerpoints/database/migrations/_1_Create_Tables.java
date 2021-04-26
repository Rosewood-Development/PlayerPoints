package org.black_ixx.playerpoints.database.migrations;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import dev.rosewood.rosegarden.database.MySQLConnector;
import dev.rosewood.rosegarden.database.SQLiteConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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

        // Check if table already exists, if it does then try renaming the old 'playername' column to 'uuid'
        boolean exists;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tablePrefix + "points");
            exists = statement.executeQuery().next();
        }

        if (exists) {
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
    }

}
