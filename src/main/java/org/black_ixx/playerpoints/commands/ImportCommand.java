package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.database.MySQLConnector;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ImportCommand extends PointsCommand {

    public ImportCommand() {
        super("import", CommandManager.CommandAliases.IMPORT);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        File file = new File(plugin.getDataFolder(), "storage.yml");
        if (!file.exists()) {
            localeManager.sendMessage(sender, "command-import-no-backup");
            return;
        }

        if (args.length < 1 || !args[0].equalsIgnoreCase("confirm")) {
            String databaseType = plugin.getManager(DataManager.class).getDatabaseConnector() instanceof MySQLConnector ? "MySQL" : "SQLite";
            localeManager.sendMessage(sender, "command-import-warning", StringPlaceholders.single("type", databaseType));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = configuration.getConfigurationSection("Points");
            if (section == null)
                section = configuration.getConfigurationSection("Players");

            if (section == null) {
                plugin.getLogger().warning("Malformed storage.yml file.");
                return;
            }

            SortedSet<SortedPlayer> data = new TreeSet<>();
            for (String uuid : section.getKeys(false))
                data.add(new SortedPlayer(UUID.fromString(uuid), section.getInt(uuid)));

            plugin.getManager(DataManager.class).importData(data);
            localeManager.sendMessage(sender, "command-import-success");
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
