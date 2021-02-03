package org.black_ixx.playerpoints.storage;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.services.IModule;

import java.util.Collection;

/**
 * Storage handler for getting / setting info between YAML, SQLite, and MYSQL.
 */
public class StorageHandler implements IStorage, IModule {
    /**
     * Plugin instance.
     */
    private final PlayerPoints plugin;
    /**
     * Current storage of player points information.
     */
    IStorage storage;

    /**
     * Constructor.
     *
     * @param plugin - PlayerPoints plugin instance.
     */
    public StorageHandler(PlayerPoints plugin) {
        this.plugin = plugin;

    }

    @Override
    public int getPoints(String name) {
        return storage.getPoints(name);
    }

    @Override
    public boolean setPoints(String name, int points) {
        return storage.setPoints(name, points);
    }

    @Override
    public boolean playerEntryExists(String id) {
        return storage.playerEntryExists(id);
    }

    @Override
    public boolean removePlayer(String id) {
        return storage.removePlayer(id);
    }

    @Override
    public Collection<String> getPlayers() {
        return storage.getPlayers();
    }

    @Override
    public void starting() {
        /*
         * Generator for storage objects.
         */
        StorageGenerator generator = new StorageGenerator(plugin);
        storage = generator.createStorageHandlerForType(plugin.getModuleForClass(RootConfig.class).getStorageType());
    }

    @Override
    public void closing() {
    }

    @Override
    public boolean destroy() {
        return storage.destroy();
    }

    @Override
    public boolean build() {
        return storage.build();
    }

}