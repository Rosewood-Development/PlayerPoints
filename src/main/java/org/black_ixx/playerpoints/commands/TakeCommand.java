package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.Tuple;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class TakeCommand extends PointsCommand {

    public TakeCommand() {
        super("take", CommandManager.CommandAliases.TAKE);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (args.length < 2) {
            localeManager.sendMessage(sender, "command-take-usage");
            return;
        }

        PointsUtils.getPlayerByName(args[0]).thenAccept(player -> {
            if (player == null) {
                localeManager.sendMessage(sender, "unknown-player", StringPlaceholders.single("player", args[0]));
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[1]);
                if (amount <= 0) {
                    localeManager.sendMessage(sender, "invalid-amount");
                    return;
                }
            } catch (NumberFormatException e) {
                localeManager.sendMessage(sender, "invalid-amount");
                return;
            }

            if (plugin.getAPI().take(player.getFirst(), amount)) {
                localeManager.sendMessage(sender, "command-take-success", StringPlaceholders.builder("player", player.getSecond())
                        .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                        .addPlaceholder("amount", PointsUtils.formatPoints(amount))
                        .build());
            } else {
                localeManager.sendMessage(sender, "command-take-lacking-funds", StringPlaceholders.builder("player", player.getSecond())
                        .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                        .build());
                plugin.getAPI().reset(player.getFirst());
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
