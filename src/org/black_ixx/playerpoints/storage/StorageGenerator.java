package org.black_ixx.playerpoints.storage;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.models.MySQLStorage;
import org.black_ixx.playerpoints.storage.models.SQLiteStorage;
import org.black_ixx.playerpoints.storage.models.YAMLStorage;

/**
 * Generates the storage handler for any given type.
 * 
 * @author Mitsugaru.
 */
public class StorageGenerator {

    /**
     * Plugin instance.
     */
    private final PlayerPoints plugin;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public StorageGenerator(PlayerPoints plugin) {
        this.plugin = plugin;
    }

    /**
     * Generate a storage handler for the given type.
     * 
     * @param type
     *            - Storage type.
     * @return Storage handler. Returns null for unhandled storage types.
     */
    public IStorage createStorageHandlerForType(StorageType type) {
        IStorage storage = null;
        switch(type) {
        case YAML:
            storage = new YAMLStorage(plugin);
            break;
        case SQLITE:
            storage = new SQLiteStorage(plugin);
            break;
        case MYSQL:
            storage = new MySQLStorage(plugin);
            break;
        default:
            break;
        }
        return storage;
    }
}