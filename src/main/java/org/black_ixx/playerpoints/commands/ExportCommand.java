package org.black_ixx.playerpoints.commands;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
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

public class ExportCommand extends PointsCommand {

    public ExportCommand() {
        super("export", CommandManager.CommandAliases.EXPORT);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        plugin.getScheduler().runTaskAsync(() -> {
            LocaleManager localeManager = plugin.getManager(LocaleManager.class);
            File file = new File(plugin.getDataFolder(), "storage.yml");
            if (file.exists() && (args.length < 1 || !args[0].equalsIgnoreCase("confirm"))) {
                localeManager.sendMessage(sender, "command-export-warning");
                return;
            }

            if (file.exists())
                file.delete();

            List<SortedPlayer> data = plugin.getManager(DataManager.class).getTopSortedPoints(null);
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection pointsSection = configuration.createSection("Points");
            ConfigurationSection uuidSection = configuration.createSection("UUIDs");

            for (SortedPlayer playerData : data) {
                pointsSection.set(playerData.getUniqueId().toString(), playerData.getPoints());
                if (!playerData.getUsername().equalsIgnoreCase("Unknown"))
                    uuidSection.set(playerData.getUniqueId().toString(), playerData.getUsername());
            }

            try {
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            localeManager.sendMessage(sender, "command-export-success");
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
