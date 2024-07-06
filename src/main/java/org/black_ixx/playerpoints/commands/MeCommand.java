package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MeCommand extends PointsCommand {

    public MeCommand() {
        super("me", CommandManager.CommandAliases.ME);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (!(sender instanceof Player)) {
            localeManager.sendMessage(sender, "no-console");
            return;
        }

        plugin.getScheduler().runTaskAsync(() -> {
            int amount = plugin.getAPI().look(((Player) sender).getUniqueId());
            localeManager.sendMessage(sender, "command-me-success", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                    .add("currency", localeManager.getCurrencyName(amount))
                    .build());
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
