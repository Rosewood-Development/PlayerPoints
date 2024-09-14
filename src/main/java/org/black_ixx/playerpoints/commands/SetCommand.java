package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.command.CommandSender;

public class SetCommand extends PointsCommand {

    public SetCommand() {
        super("set", CommandManager.CommandAliases.SET);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        
        // Ensure there are at least two arguments
        if (args.length < 2) {
            localeManager.sendMessage(sender, "command-set-usage");
            return;
        }

        // Check if -s (silent) flag is present
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
                if (amount < 0) {
                    if (!silent) {
                        localeManager.sendMessage(sender, "invalid-amount");
                    }
                    return;
                }
            } catch (NumberFormatException e) {
                if (!silent) {
                    localeManager.sendMessage(sender, "invalid-amount");
                }
                return;
            }

            // Try to set the points for the player
            if (plugin.getAPI().set(player.getFirst(), amount)) {
                if (!silent) {
                    localeManager.sendMessage(sender, "command-set-success", StringPlaceholders.builder("player", player.getSecond())
                            .add("currency", localeManager.getCurrencyName(amount))
                            .add("amount", PointsUtils.formatPoints(amount))
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
