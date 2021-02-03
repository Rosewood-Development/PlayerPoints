package org.black_ixx.playerpoints.storage.exports;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.StorageGenerator;

/**
 * Handles database exporting.
 * 
 * @author Mitsugaru
 */
public abstract class DatabaseExport {
    /**
     * Plugin instance.
     */
    protected PlayerPoints plugin;

    /**
     * Storage generator.
     */
    protected StorageGenerator generator;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public DatabaseExport(PlayerPoints plugin) {
        this.plugin = plugin;
        generator = new StorageGenerator(plugin);
    }

    /**
     * Handle the import process into a database.
     */
    abstract void doExport();
}