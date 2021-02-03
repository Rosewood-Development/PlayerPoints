package org.black_ixx.playerpoints.storage.exports;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import lib.PatPeter.SQLibrary.SQLite;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.IStorage;
import org.black_ixx.playerpoints.storage.StorageType;

/**
 * Handles SQLite to YAML export.
 * 
 * @author Mitsugaru
 */
public class SQLiteExport extends DatabaseExport {
    
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
    public SQLiteExport(PlayerPoints plugin) {
        super(plugin);
        sqlite = new SQLite(plugin.getLogger(), " ", plugin.getDataFolder()
                .getAbsolutePath(), "storage");
        sqlite.open();
    }

    @Override
    void doExport() {
        IStorage yaml = generator.createStorageHandlerForType(StorageType.YAML);
        ResultSet query;
        try {
            query = sqlite.query("SELECT * FROM playerpoints");
            if(query.next()) {
                do {
                    yaml.setPoints(query.getString("playername"),
                            query.getInt("points"));
                } while(query.next());
            }
            query.close();
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "SQLException on SQLite export", e);
        } finally {
            sqlite.close();
        }
    }
}