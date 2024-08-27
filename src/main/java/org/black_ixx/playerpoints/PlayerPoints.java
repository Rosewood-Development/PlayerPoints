package org.black_ixx.playerpoints;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.Arrays;
import java.util.List;
import me.lokka30.treasury.api.common.service.ServiceRegistry;
import me.lokka30.treasury.api.economy.EconomyProvider;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.hook.PointsPlaceholderExpansion;
import org.black_ixx.playerpoints.listeners.PointsMessageListener;
import org.black_ixx.playerpoints.listeners.VotifierListener;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LeaderboardManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.setting.SettingKey;
import org.black_ixx.playerpoints.treasury.PlayerPointsTreasuryLayer;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

/**
 * Main plugin class for PlayerPoints.
 */
public class PlayerPoints extends RosePlugin {

    private static PlayerPoints instance;
    private PlayerPointsAPI api;
    private PlayerPointsVaultLayer vaultLayer;
    private PlayerPointsTreasuryLayer treasuryLayer;

    public PlayerPoints() {
        super(80745, 10234, DataManager.class, LocaleManager.class, null);
        instance = this;
    }

    @Override
    public void enable() {
        this.api = new PlayerPointsAPI(this);

        if (SettingKey.VAULT.get() && Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            this.vaultLayer = new PlayerPointsVaultLayer(this);

            // Check valid values for the priorities
            ServicePriority priority = null;
            String desiredPriority = SettingKey.VAULT_PRIORITY.get();
            for (ServicePriority value : ServicePriority.values()) {
                if (value.name().equalsIgnoreCase(desiredPriority)) {
                    priority = value;
                    break;
                }
            }

            if (priority == null) {
                this.getLogger().warning("vault-priority value in the config.yml is invalid, defaulting to Low.");
                priority = ServicePriority.Low;
            }

            Bukkit.getServicesManager().register(Economy.class, this.vaultLayer, this, priority);
        }

        if (SettingKey.TREASURY.get() && Bukkit.getPluginManager().isPluginEnabled("Treasury")) {
            this.treasuryLayer = new PlayerPointsTreasuryLayer(this);

            // Check valid values for the priorities
            me.lokka30.treasury.api.common.service.ServicePriority priority = null;
            String desiredPriority = SettingKey.TREASURY_PRIORITY.get();
            for (me.lokka30.treasury.api.common.service.ServicePriority value : me.lokka30.treasury.api.common.service.ServicePriority.values()) {
                if (value.name().equalsIgnoreCase(desiredPriority)) {
                    priority = value;
                    break;
                }
            }

            if (priority == null) {
                this.getLogger().warning("treasury-priority value in the config.yml is invalid, defaulting to LOW.");
                priority = me.lokka30.treasury.api.common.service.ServicePriority.LOW;
            }

            ServiceRegistry.INSTANCE.registerService(EconomyProvider.class, new PlayerPointsTreasuryLayer(this), this.getName(), priority);
        }

        if (SettingKey.BUNGEECORD_SEND_UPDATES.get()) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, PointsMessageListener.CHANNEL);
            Bukkit.getMessenger().registerIncomingPluginChannel(this, PointsMessageListener.CHANNEL, new PointsMessageListener(this));
        }

        this.getScheduler().runTask(() -> {
            // Register placeholders, if applicable
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
                new PointsPlaceholderExpansion(this).register();

            // Register votifier listener, if applicable
            if (SettingKey.VOTE_ENABLED.get()) {
                Plugin votifier = Bukkit.getPluginManager().getPlugin("Votifier");
                if (votifier != null) {
                    Bukkit.getPluginManager().registerEvents(new VotifierListener(this), this);
                } else {
                    this.getLogger().warning("The hook for Votifier was enabled, but it does not appear to be installed.");
                }
            }
        });
    }

    @Override
    public void disable() {
        if (this.vaultLayer != null)
            Bukkit.getServicesManager().unregister(Economy.class, this.vaultLayer);

        if (this.treasuryLayer != null)
            ServiceRegistry.INSTANCE.unregister(EconomyProvider.class, this.treasuryLayer);

        if (SettingKey.BUNGEECORD_SEND_UPDATES.get()) {
            Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
            Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        }
    }

    @Override
    public void reload() {
        super.reload();
        PointsUtils.setCachedValues(this);
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Arrays.asList(
                CommandManager.class,
                LeaderboardManager.class
        );
    }

    @Override
    protected List<RoseSetting<?>> getRoseConfigSettings() {
        return SettingKey.getKeys();
    }

    @Override
    protected String[] getRoseConfigHeader() {
        return new String[] {
                "__________ __                           __________       __        __",
                "\\______   \\  | _____  ___ __  __________\\______   \\____ |__| _____/  |_  ______",
                " |     ___/  | \\__  \\<   |  |/ __ \\_  __ \\     ___/  _ \\|  |/    \\   __\\/  ___/",
                " |    |   |  |__/ __ \\\\___  \\  ___/|  | \\/    |  (  <_> )  |   |  \\  |  \\___ \\",
                " |____|   |____(____  / ____|\\___  >__|  |____|   \\____/|__|___|  /__| /____  >",
                "                    \\/\\/         \\/                             \\/          \\/"
        };
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
