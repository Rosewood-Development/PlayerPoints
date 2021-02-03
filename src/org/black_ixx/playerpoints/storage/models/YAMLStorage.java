package org.black_ixx.playerpoints.storage.models;

import java.io.File;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.services.ExecutorModule;
import org.black_ixx.playerpoints.storage.IStorage;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Object that handles points storage from a file config source.
 * 
 * @author Mitsugaru
 */
public class YAMLStorage implements IStorage {

    /**
     * Plugin reference.
     */
    private final PlayerPoints plugin;

    /**
     * File reference.
     */
    private final File file;

    /**
     * Yaml config.
     */
    private final YamlConfiguration config;

    /**
     * Task that saves to disk.
     */
    private final SaveTask saveTask;

    /**
     * Points section string.
     */
    private static final String POINTS_SECTION = "Points.";

    /**
     * Constructor.
     * 
     * @param pp
     *            - Player points plugin instance.
     */
    public YAMLStorage(PlayerPoints pp) {
        plugin = pp;
        file = new File(plugin.getDataFolder().getAbsolutePath() + "/storage.yml");
        config = YamlConfiguration.loadConfiguration(file);
        saveTask = new SaveTask();
        save();
    }

    /**
     * Save the config data.
     *
     * The save action is done in a separate thread to save the server
     * performance.
     */
    public void save() {
        plugin.getModuleForClass(ExecutorModule.class).submit(saveTask);
    }

    /**
     * Reload the config file.
     */
    public void reload() {
        try {
            config.load(file);
        } catch(InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setPoints(String id, int points) {
        config.set(POINTS_SECTION + id, points);
        save();
        return true;
    }

    @Override
    public int getPoints(String id) {
        return config.getInt(POINTS_SECTION + id, 0);
    }

    @Override
    public boolean playerEntryExists(String id) {
        return config.contains(POINTS_SECTION + id);
    }

    @Override
    public boolean removePlayer(String id) {
        config.set(POINTS_SECTION + id, null);
        save();
        return true;
    }

    @Override
    public Collection<String> getPlayers() {
    	Collection<String> players = Collections.emptySet();
    	
    	if(config.isConfigurationSection("Points")) {
    		players = config.getConfigurationSection("Points").getKeys(false);
    	}
        return players;
    }

    @Override
    public boolean destroy() {
        Collection<String> sections = config.getKeys(false);
        for(String section: sections) {
            config.set(section, null);
        }
        save();
        return true;
    }

    @Override
    public boolean build() {
        boolean success = false;
        try {
            success = file.createNewFile();
        } catch(IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create storage file!", e);
        }
        return success;
    }

    private final class SaveTask implements Runnable {
        @Override
        public void run() {
            // Set config
            try {
                // Save the file
                config.save(file);
            } catch(IOException e1) {
                plugin.getLogger().warning(
                        "File I/O Exception on saving storage.yml");
                e1.printStackTrace();
            }
        }
    }
}