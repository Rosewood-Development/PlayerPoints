package org.black_ixx.playerpoints.database.migrations;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _2_Add_Table_Username_Cache extends DataMigration {

    public _2_Add_Table_Username_Cache() {
        super(2);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        // Create username cache table
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE " + tablePrefix + "username_cache (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "username VARCHAR(30) NOT NULL, " +
                    "UNIQUE (uuid)" +
                    ")");
        }

        // Create indexes on each column
        try (Statement statement = connection.createStatement()) {
            statement.addBatch("CREATE INDEX " + tablePrefix + "username_cache_uuid_index ON " + tablePrefix + "username_cache (uuid)");
            statement.addBatch("CREATE INDEX " + tablePrefix + "username_cache_username_index ON " + tablePrefix + "username_cache (username)");
            statement.executeBatch();
        }
    }

}
