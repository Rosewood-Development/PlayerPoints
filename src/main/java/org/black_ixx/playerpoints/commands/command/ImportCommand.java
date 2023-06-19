package org.black_ixx.playerpoints.commands.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.database.MySQLConnector;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

public class ImportCommand extends RoseCommand {

    public ImportCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Boolean confirm) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        File file = new File(this.rosePlugin.getDataFolder(), "storage.yml");
        if (!file.exists()) {
            locale.sendMessage(context.getSender(), "command-import-no-backup");
            return;
        }

        if (!confirm) {
            String databaseType = this.rosePlugin.getManager(DataManager.class).getDatabaseConnector() instanceof MySQLConnector ? "MySQL" : "SQLite";
            locale.sendMessage(context.getSender(), "command-import-warning", StringPlaceholders.of("type", databaseType));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this.rosePlugin, () -> {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection pointsSection = configuration.getConfigurationSection("Points");
            if (pointsSection == null)
                pointsSection = configuration.getConfigurationSection("Players");

            if (pointsSection == null) {
                this.rosePlugin.getLogger().warning("Malformed storage.yml file.");
                return;
            }

            ConfigurationSection uuidSection = configuration.getConfigurationSection("UUIDs");
            Map<UUID, String> uuidMap = new HashMap<>();
            if (uuidSection != null) {
                for (String uuidString : uuidSection.getKeys(false)) {
                    String name = uuidSection.getString(uuidString);
                    UUID uuidObj = UUID.fromString(uuidString);
                    uuidMap.put(uuidObj, name);
                }
            }

            SortedSet<SortedPlayer> data = new TreeSet<>();
            for (String uuidString : pointsSection.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                int points = pointsSection.getInt(uuidString);

                String username = uuidMap.get(uuid);
                if (username != null) {
                    data.add(new SortedPlayer(uuid, username, points));
                } else {
                    data.add(new SortedPlayer(uuid, points));
                }
            }

            this.rosePlugin.getManager(DataManager.class).importData(data, uuidMap);
            locale.sendMessage(context.getSender(), "command-import-success");
        });
    }

    @Override
    protected String getDefaultName() {
        return "import";
    }

    @Override
    public String getDescriptionKey() {
        return "command-import-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.import";
    }
}
