package org.black_ixx.playerpoints;

import com.evilmidget38.UUIDFetcher;
import org.black_ixx.playerpoints.commands.Commander;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.listeners.RestrictionListener;
import org.black_ixx.playerpoints.listeners.VotifierListener;
import org.black_ixx.playerpoints.services.ExecutorModule;
import org.black_ixx.playerpoints.services.IModule;
import org.black_ixx.playerpoints.storage.StorageHandler;
import org.black_ixx.playerpoints.storage.exports.Exporter;
import org.black_ixx.playerpoints.storage.imports.Importer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

/**
 * Main plugin class for PlayerPoints.
 */
public class PlayerPoints extends JavaPlugin {

    /**
     * Plugin tag.
     */
    public static final String TAG = "[PlayerPoints]";
    /**
     * Modules.
     */
    private final Map<Class<? extends IModule>, IModule> modules = new HashMap<>();
    /**
     * API instance.
     */
    private PlayerPointsAPI api;

    @Override
    public void onEnable() {
        // Initialize localization
        LocalizeConfig.init(this);
        // Initialize config
        RootConfig rootConfig = new RootConfig(this);
        registerModule(RootConfig.class, rootConfig);
        // Initialize ExecutorService
        registerModule(ExecutorModule.class, new ExecutorModule());
        // Do imports
        Importer importer = new Importer(this);
        importer.checkImport();
        // Do exports
        Exporter exporter = new Exporter(this);
        exporter.checkExport();
        // Intialize storage handler
        registerModule(StorageHandler.class, new StorageHandler(this));
        // Initialize API
        api = new PlayerPointsAPI(this);
        // Register commands
        final Commander commander = new Commander(this);
        if (getDescription().getCommands().containsKey("points")) {
            getCommand("points").setExecutor(commander);
        }
        if (getDescription().getCommands().containsKey("p")) {
            getCommand("p").setExecutor(commander);
        }
        final PluginManager pm = getServer().getPluginManager();
        // Register votifier listener, if applicable
        if (rootConfig.voteEnabled) {
            final Plugin votifier = pm.getPlugin("Votifier");
            if (votifier != null) {
                pm.registerEvents(new VotifierListener(this), this);
            } else {
                getLogger().warning("Could not hook into Votifier!");
            }
        }
        // Vault module
        if (rootConfig.vault) {
            registerModule(PlayerPointsVaultLayer.class,
                    new PlayerPointsVaultLayer(this));
        }
        // Register listeners
        pm.registerEvents(new RestrictionListener(this), this);
    }

    @Override
    public void onDisable() {
        // Deregister all modules.
        List<Class<? extends IModule>> clazzez = new ArrayList<>(modules.keySet());
        for (Class<? extends IModule> clazz : clazzez) {
            this.deregisterModuleForClass(clazz);
        }
    }

    /**
     * Get the plugin's API.
     *
     * @return API instance.
     */
    public PlayerPointsAPI getAPI() {
        return api;
    }

    /**
     * Register a module to the API.
     *
     * @param clazz  - Class of the instance.
     * @param module - Module instance.
     * @throws IllegalArgumentException - Thrown if an argument is null.
     */
    public <T extends IModule> void registerModule(Class<T> clazz, T module) {
        // Check arguments.
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        } else if (module == null) {
            throw new IllegalArgumentException("Module cannot be null");
        } else if (modules.containsKey(clazz)) {
            this.getLogger().warning(
                    "Overwriting module for class: " + clazz.getName());
        }
        // Add module.
        modules.put(clazz, module);
        // Tell module to start.
        module.starting();
    }

    /**
     * Unregister a module from the API.
     *
     * @param clazz - Class of the instance.
     */
    public <T extends IModule> void deregisterModuleForClass(Class<T> clazz) {
        // Check arguments.
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        // Grab module and tell it its closing.
        T module = clazz.cast(modules.get(clazz));
        if (module != null) {
            module.closing();
        }
    }

    /**
     * Retrieve a registered CCModule.
     *
     * @param clazz - Class identifier.
     * @return Module instance. Returns null is an instance of the given class
     * has not been registered with the API.
     */
    public <T extends IModule> T getModuleForClass(Class<T> clazz) {
        return clazz.cast(modules.get(clazz));
    }

    /**
     * Attempts to look up full name based on who's on the server Given a
     * partial name
     *
     * @author Frigid, edited by Raphfrk and petteyg359
     */
    public String expandName(String name) {
        int m = 0;
        String Result = "";
        final Collection<? extends Player> online = getServer().getOnlinePlayers();
        for (Player player : online) {
            String str = player.getName();
            if (str.matches("(?i).*" + name + ".*")) {
                m++;
                Result = str;
                if (m == 2) {
                    return null;
                }
            }
            if (str.equalsIgnoreCase(name)) {
                return str;
            }
        }
        if (m == 1)
            return Result;
        if (m > 1) {
            return null;
        }
        return name;
    }

    /**
     * Attempt to translate a player name into a UUID.
     *
     * @param name - Player name.
     * @return Player UUID. Null if no match found.
     */
    public UUID translateNameToUUID(String name) {
        UUID id = null;
        RootConfig config = getModuleForClass(RootConfig.class);
        if (config.debugUUID) {
            getLogger().info("translateNameToUUID(" + name + ")");
        }

        if (name == null) {
            if (config.debugUUID) {
                getLogger().info("translateNameToUUID() - bad ID");
            }
            return null;
        }

        // Look through online players first
        if (config.debugUUID) {
            getLogger().info("translateNameToUUID() - Looking through online players: " + Bukkit.getServer().getOnlinePlayers().size());
        }
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                id = p.getUniqueId();
                if (config.debugUUID) {
                    getLogger().info("translateNameToUUID() online player UUID found: " + id.toString());
                }
                break;
            }
        }

        // Last resort, attempt bukkit api lookup
        if (id == null && Bukkit.getServer().getOnlineMode()) {
            if (config.debugUUID) {
                getLogger().info("translateNameToUUID() - Attempting online lookup");
            }
            UUIDFetcher fetcher = new UUIDFetcher(Collections.singletonList(name));
            try {
                Map<String, UUID> map = fetcher.call();
                for (Map.Entry<String, UUID> entry : map.entrySet()) {
                    if (name.equalsIgnoreCase(entry.getKey())) {
                        id = entry.getValue();
                        if (config.debugUUID) {
                            getLogger().info("translateNameToUUID() web player UUID found: " + ((id == null) ? null : id.toString()));
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Exception on online UUID fetch", e);
            }
        } else if (id == null && !Bukkit.getServer().getOnlineMode()) {
            //There's nothing we can do but attempt to get the UUID from old method.
            id = Bukkit.getServer().getOfflinePlayer(name).getUniqueId();
            if (config.debugUUID) {
                getLogger().info("translateNameToUUID() offline player UUID found: " + ((id == null) ? null : id.toString()));
            }
        }
        return id;
    }
}