package org.black_ixx.playerpoints.commands.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExportCommand extends RoseCommand {
    public ExportCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, boolean confirm) {
        Bukkit.getScheduler().runTaskAsynchronously(this.rosePlugin, () -> {
            LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            File file = new File(this.rosePlugin.getDataFolder(), "storage.yml");

            if (file.exists() || !confirm) {
                locale.sendMessage(context.getSender(), "command-export-warning");
                return;
            }

            if (file.exists())
                file.delete();

            List<SortedPlayer> data = this.rosePlugin.getManager(DataManager.class).getTopSortedPoints(null);
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

            locale.sendMessage(context.getSender(), "command-export-success");
        });
    }

    @Override
    protected String getDefaultName() {
        return "export";
    }

    @Override
    public String getDescriptionKey() {
        return "command-export-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.export";
    }
}
