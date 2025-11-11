package org.black_ixx.playerpoints.database.migrations;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import dev.rosewood.rosegarden.database.SQLiteConnector;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _3_Add_Table_Transaction_Log extends DataMigration {

    public _3_Add_Table_Transaction_Log() {
        super(3);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE " + tablePrefix + "transaction_log (" +
                    "transaction_type VARCHAR(20) NOT NULL, " +
                    "description VARCHAR(100) NOT NULL, " +
                    "source VARCHAR(36) NULL, " +
                    "receiver VARCHAR(36) NOT NULL, " +
                    "amount INT NOT NULL, " +
                    "timestamp " + (connector instanceof SQLiteConnector ? "TEXT" : "TIMESTAMP") + " DEFAULT CURRENT_TIMESTAMP" +
                    ")");
        }
    }

}
