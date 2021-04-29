package org.black_ixx.playerpoints;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.Collections;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.commands.Commander;
import org.black_ixx.playerpoints.database.migrations._1_Create_Tables;
import org.black_ixx.playerpoints.hook.PointsPlaceholderExpansion;
import org.black_ixx.playerpoints.listeners.VotifierListener;
import org.black_ixx.playerpoints.manager.ConfigurationManager;
import org.black_ixx.playerpoints.manager.ConfigurationManager.Setting;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.manager.PointsCacheManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

/**
 * Main plugin class for PlayerPoints.
 */
public class PlayerPoints extends RosePlugin {

    private static PlayerPoints instance;
    private PlayerPointsAPI api;
    private PlayerPointsVaultLayer vaultLayer;

    public PlayerPoints() {
        super(80745, 10234, ConfigurationManager.class, DataManager.class, LocaleManager.class);
        instance = this;
    }

    @Override
    public void enable() {
        this.api = new PlayerPointsAPI(this);

        if (Setting.VAULT.getBoolean()) {
            this.vaultLayer = new PlayerPointsVaultLayer(this);
            Bukkit.getServicesManager().register(Economy.class, this.vaultLayer, this, ServicePriority.Low);
        }

        // Register commands
        Commander commander = new Commander(this);
        PluginCommand command = this.getCommand("points");
        if (command != null)
            command.setExecutor(commander);

        // Register votifier listener, if applicable
        if (Setting.VOTE_ENABLED.getBoolean()) {
            Plugin votifier = Bukkit.getPluginManager().getPlugin("Votifier");
            if (votifier != null) {
                Bukkit.getPluginManager().registerEvents(new VotifierListener(this), this);
            } else {
                this.getLogger().warning("The hook for Votifier was enabled, but it does not appear to be installed.");
            }
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new PointsPlaceholderExpansion(this).register();
    }

    @Override
    public void disable() {
        if (this.vaultLayer != null)
            Bukkit.getServicesManager().unregister(Economy.class, this.vaultLayer);
    }

    @Override
    public void reload() {
        super.reload();
        PointsUtils.setFormatter(this.getManager(LocaleManager.class).getLocaleMessage("currency-separator"));
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Collections.singletonList(
                PointsCacheManager.class
        );
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return Collections.singletonList(
                _1_Create_Tables.class
        );
    }

    public static PlayerPoints getInstance() {
        return instance;
    }

    /**
     * Get the plugin's API.
     *
     * @return API instance.
     */
    public PlayerPointsAPI getAPI() {
        return this.api;
    }

}
