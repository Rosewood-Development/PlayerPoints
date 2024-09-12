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

        // Check if "-s" flag is present
        boolean silent = false;
        if (args.length > 2 && args[2].equalsIgnoreCase("-s")) {
            silent = true;
        }

        PointsUtils.getPlayerByName(args[0], player -> {
            if (player == null) {
                if (!silent) {
                    localeManager.sendMessage(sender, "unknown-player", StringPlaceholders.of("player", args[0]));
                }
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                if (!silent) {
                    localeManager.sendMessage(sender, "invalid-amount");
                }
                return;
            }

            if (amount <= 0) {
                if (!silent) {
                    localeManager.sendMessage(sender, "invalid-amount");
                }
                return;
            }

            if (plugin.getAPI().give(player.getFirst(), amount)) {
                // Send message to receiver if not in silent mode
                Player onlinePlayer = Bukkit.getPlayer(player.getFirst());
                if (onlinePlayer != null && !silent) {
                    localeManager.sendMessage(onlinePlayer, "command-give-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .add("currency", localeManager.getCurrencyName(amount))
                            .build());
                }

                // Send message to sender if not in silent mode
                if (!silent) {
                    localeManager.sendMessage(sender, "command-give-success", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .add("currency", localeManager.getCurrencyName(amount))
                            .add("player", player.getSecond())
                            .build());
                }
            }
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        if (args.length == 1) {
            return PointsUtils.getPlayerTabComplete(args[0]);
        } else if (args.length == 2) {
            return Collections.singletonList("<amount>");
        } else if (args.length == 3) {
            return Collections.singletonList("-s");
        } else {
            return Collections.emptyList();
        }
    }
}
