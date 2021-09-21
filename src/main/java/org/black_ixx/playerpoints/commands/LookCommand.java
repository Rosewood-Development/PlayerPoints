package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class LookCommand extends PointsCommand {

    public LookCommand() {
        super("look", CommandManager.CommandAliases.LOOK);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (args.length < 1) {
            localeManager.sendMessage(sender, "command-look-usage");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer player = PointsUtils.getPlayerByName(args[0]);
            if (!player.hasPlayedBefore() && !player.isOnline()) {
                localeManager.sendMessage(sender, "unknown-player", StringPlaceholders.single("player", args[0]));
                return;
            }

            int amount = plugin.getAPI().look(player.getUniqueId());
            localeManager.sendMessage(sender, "command-look-success", StringPlaceholders.builder("player", player.getName())
                    .addPlaceholder("amount", PointsUtils.formatPoints(amount))
                    .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                    .build());
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? PointsUtils.getPlayerTabComplete(args[0]) : Collections.emptyList();
    }

}
