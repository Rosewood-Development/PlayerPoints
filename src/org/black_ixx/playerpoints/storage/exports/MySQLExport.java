package org.black_ixx.playerpoints.storage.exports;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import lib.PatPeter.SQLibrary.MySQL;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.storage.IStorage;
import org.black_ixx.playerpoints.storage.StorageType;

/**
 * Handles MySQL to YAML export.
 * 
 * @author Mitsugaru
 */
public class MySQLExport extends DatabaseExport {
    
    /**
     * MYSQL reference.
     */
    private final MySQL mysql;

    public MySQLExport(PlayerPoints plugin) {
        super(plugin);
        RootConfig config = plugin.getModuleForClass(RootConfig.class);
        mysql = new MySQL(plugin.getLogger(), " ", config.host,
                Integer.parseInt(config.port),
                config.database, config.user,
                config.password);
        mysql.open();
    }

    @Override
    void doExport() {
    	RootConfig config = plugin.getModuleForClass(RootConfig.class);
        IStorage yaml = generator.createStorageHandlerForType(StorageType.YAML);
        ResultSet query;
        try {
            query = mysql.query(String.format("SELECT * FROM %s", config.table));
            if(query.next()) {
                do {
                    yaml.setPoints(query.getString("playername"),
                            query.getInt("points"));
                } while(query.next());
            }
            query.close();
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "SQLException on MySQL export", e);
        } finally {
            mysql.close();
        }
    }
}