package org.black_ixx.playerpoints.config;

import java.io.File;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map.Entry;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.models.Flag;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Handles user-based messages.
 * 
 * @author Mitsugaru
 */
public class LocalizeConfig {

    /**
     * Plugin instance.
     */
    private static PlayerPoints plugin;

    /**
     * File reference.
     */
    private static File file;

    /**
     * YAML config.
     */
    private static YamlConfiguration config;

    /**
     * Map of config keys to values.
     */
    private static final EnumMap<LocalizeNode, String> MESSAGES = new EnumMap<>(
            LocalizeNode.class);

    /**
     * Initialize.
     * 
     * @param pp
     *            - Plugin instance.
     */
    public static void init(PlayerPoints pp) {
        plugin = pp;
        file = new File(plugin.getDataFolder().getAbsolutePath()
                + "/localization.yml");
        config = YamlConfiguration.loadConfiguration(file);
        loadDefaults();
        loadMessages();
    }

    public static void save() {
        // Set config
        try {
            // Save the file
            config.save(file);
        } catch(IOException e1) {
            plugin.getLogger().warning(
                    "File I/O Exception on saving localization config");
            e1.printStackTrace();
        }
    }

    public static void reload() {
        try {
            config.load(file);
        } catch(InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
        MESSAGES.clear();
        loadDefaults();
        loadMessages();
    }

    public static void set(String path, Object o) {
        config.set(path, o);
        save();
    }

    public static String getString(String path, String def) {
        return config.getString(path, def);
    }

    private static void loadDefaults() {
        // Add to config if missing
        for(LocalizeNode node : LocalizeNode.values()) {
            if(!config.contains(node.getPath())) {
                config.set(node.getPath(), node.getDefaultValue());
            }
        }
        save();
    }

    private static void loadMessages() {
        for(LocalizeNode node : LocalizeNode.values()) {
            MESSAGES.put(node,
                    config.getString(node.getPath(), node.getDefaultValue()));
        }
    }

    public static String parseString(LocalizeNode node,
            EnumMap<Flag, String> replace) {
        /*
         * Thanks to @Njol for the following
         * http://forums.bukkit.org/threads/multiple
         * -classes-config-colours.79719/#post-1154761
         */
        String out = ChatColor.translateAlternateColorCodes('&',
                MESSAGES.get(node));
        if(replace != null) {
            for(Entry<Flag, String> entry : replace.entrySet()) {
                out = out
                        .replaceAll(entry.getKey().getFlag(), entry.getValue());
            }
        }
        return out;
    }

}
