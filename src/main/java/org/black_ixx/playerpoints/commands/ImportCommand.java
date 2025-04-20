package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.database.MySQLConnector;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ImportCommand extends BasePointsCommand {

    public ImportCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context, String confirm) {
        CommandSender sender = context.getSender();
        File file = new File(this.rosePlugin.getDataFolder(), "storage.yml");
        if (!file.exists()) {
            this.localeManager.sendMessage(sender, "command-import-no-backup");
            return;
        }

        DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
        if (confirm == null) {
            String databaseType = dataManager.getDatabaseConnector() instanceof MySQLConnector ? "MySQL" : "SQLite";
            this.localeManager.sendMessage(sender, "command-import-warning", StringPlaceholders.of("type", databaseType));
            return;
        }

        this.rosePlugin.getScheduler().runTaskAsync(() -> {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection pointsSection = configuration.getConfigurationSection("Points");
            if (pointsSection == null)
                pointsSection = configuration.getConfigurationSection("Players");

            if (pointsSection == null) {
                this.rosePlugin.getLogger().warning("Malformed storage.yml file.");
                return;
            }

            ConfigurationSection uuidSection = configuration.getConfigurationSection("UUIDs");
            Map<UUID, String> usernameMap = new HashMap<>();
            if (uuidSection != null) {
                for (String uuidString : uuidSection.getKeys(false)) {
                    String name = uuidSection.getString(uuidString);
                    UUID uuidObj = UUID.fromString(uuidString);
                    usernameMap.put(uuidObj, name);
                }
            }

            Map<UUID, Integer> data = new HashMap<>();
            for (String uuidString : pointsSection.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                int points = pointsSection.getInt(uuidString);
                data.put(uuid, points);
            }

            dataManager.importData(data, usernameMap);
            this.localeManager.sendCommandMessage(sender, "command-import-success");
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("import")
                .descriptionKey("command-import-description")
                .permission("playerpoints.import")
                .arguments(ArgumentsDefinition.builder()
                        .optional("confirm", ArgumentHandlers.forValues(String.class, "confirm"))
                        .build())
                .build();
    }

}
