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
import org.bukkit.entity.Player;

public class GiveCommand extends PointsCommand {

    public GiveCommand() {
        super("give", CommandManager.CommandAliases.GIVE);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (args.length < 2) {
            localeManager.sendMessage(sender, "command-give-usage");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer player = PointsUtils.getPlayerByName(args[0]);
            if (!player.hasPlayedBefore() && !player.isOnline()) {
                localeManager.sendMessage(sender, "unknown-player", StringPlaceholders.single("player", args[0]));
                return;
            }

            try {
                int amount = Integer.parseInt(args[1]);
                if (amount <= 0) {
                    localeManager.sendMessage(sender, "invalid-amount");
                    return;
                }

                if (plugin.getAPI().give(player.getUniqueId(), amount)) {
                    // Send message to receiver
                    if (player.isOnline()) {
                        localeManager.sendMessage((Player) player, "command-give-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                                .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                                .build());
                    }

                    // Send message to sender
                    localeManager.sendMessage(sender, "command-give-success", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                            .addPlaceholder("player", player.getName())
                            .build());
                }
            } catch (NumberFormatException e) {
                localeManager.sendMessage(sender, "invalid-amount");
            }
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        if (args.length == 1) {
            return PointsUtils.getPlayerTabComplete(args[0]);
        } else if (args.length == 2) {
            return Collections.singletonList("<amount>");
        } else {
            return Collections.emptyList();
        }
    }

}
