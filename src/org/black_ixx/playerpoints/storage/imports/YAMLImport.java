package org.black_ixx.playerpoints.storage.imports;

import java.io.File;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.IStorage;
import org.black_ixx.playerpoints.storage.StorageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Import from YAML to MySQL.
 * 
 * @author Mitsugaru
 */
public class YAMLImport extends DatabaseImport {

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public YAMLImport(PlayerPoints plugin) {
        super(plugin);
    }

    @Override
    void doImport() {
        plugin.getLogger().info("Importing YAML to MySQL");
        IStorage mysql = generator
                .createStorageHandlerForType(StorageType.MYSQL);
        final ConfigurationSection config = YamlConfiguration
                .loadConfiguration(new File(plugin.getDataFolder()
                        .getAbsolutePath() + "/storage.yml"));
        final ConfigurationSection points = config
                .getConfigurationSection("Points");
        for(String key : points.getKeys(false)) {
            mysql.setPoints(key, points.getInt(key));
        }
    }
}