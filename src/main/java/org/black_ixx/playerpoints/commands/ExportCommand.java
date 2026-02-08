package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ExportCommand extends BasePointsCommand {

    public ExportCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context, String confirm) {
        this.rosePlugin.getScheduler().runTaskAsync(() -> {
            CommandSender sender = context.getSender();
            File file = new File(this.rosePlugin.getDataFolder(), "storage.yml");
            if (file.exists() && confirm == null) {
                this.localeManager.sendCommandMessage(sender, "command-export-warning");
                return;
            }

            if (file.exists())
                file.delete();

            List<SortedPlayer> data = this.rosePlugin.getManager(DataManager.class).getTopSortedPoints();
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

            this.localeManager.sendCommandMessage(sender, "command-export-success");
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("export")
                .descriptionKey("command-export-description")
                .permission("playerpoints.export")
                .arguments(ArgumentsDefinition.builder()
                        .optional("confirm", ArgumentHandlers.forValues(String.class, "confirm"))
                        .build())
                .build();
    }

}
