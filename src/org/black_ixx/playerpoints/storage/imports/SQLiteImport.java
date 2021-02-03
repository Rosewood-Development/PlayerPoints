package org.black_ixx.playerpoints.storage.imports;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import lib.PatPeter.SQLibrary.SQLite;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.IStorage;
import org.black_ixx.playerpoints.storage.StorageType;

/**
 * Imports from SQLite to MySQL.
 * 
 * @author Mitsugaru
 */
public class SQLiteImport extends DatabaseImport {

    /**
     * SQLite reference.
     */
    private final SQLite sqlite;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public SQLiteImport(PlayerPoints plugin) {
        super(plugin);
        sqlite = new SQLite(plugin.getLogger(), " ", plugin.getDataFolder()
                .getAbsolutePath(), "storage");
        sqlite.open();
    }

    @Override
    void doImport() {
        plugin.getLogger().info("Importing SQLite to MySQL");
        IStorage mysql = generator
                .createStorageHandlerForType(StorageType.MYSQL);
        ResultSet query;
        try {
            query = sqlite.query("SELECT * FROM playerpoints");
            if(query.next()) {
                do {
                    mysql.setPoints(query.getString("playername"),
                            query.getInt("points"));
                } while(query.next());
            }
            query.close();
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "SQLException on SQLite import", e);
        } finally {
            sqlite.close();
        }
    }
}