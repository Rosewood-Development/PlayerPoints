package org.black_ixx.playerpoints.storage.imports;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.StorageGenerator;

/**
 * Handles database importing.
 * 
 * @author Mitsugaru
 */
public abstract class DatabaseImport {

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
    public DatabaseImport(PlayerPoints plugin) {
        this.plugin = plugin;
        generator = new StorageGenerator(plugin);
    }

    /**
     * Handle the import process into a database.
     */
    abstract void doImport();
}