package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * Handles the take command.
 *
 * @author Mitsugaru
 */
public class TakeCommand extends PointsCommand {

    public TakeCommand() {
        super("take");
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (args.length < 2) {
            localeManager.sendMessage(sender, "command-take-usage");
            return;
        }

        OfflinePlayer player = PointsUtils.getPlayerByName(args[0]);
        if (!player.hasPlayedBefore()) {
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
        } catch (NumberFormatException notnumber) {
            localeManager.sendMessage(sender, "invalid-amount");
            return;
        }

        plugin.getAPI().takeAsync(player.getUniqueId(), amount).thenAccept(success -> {
            if (success) {
                // Send message to to taked player
                if (player.isOnline()) {
                    localeManager.sendMessage((Player) player, "command-take-taken", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                            .build());
                }
                // Send message to receiver
                localeManager.sendMessage(sender, "command-take-success", StringPlaceholders.builder("player", player.getName())
                        .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                        .addPlaceholder("amount", PointsUtils.formatPoints(amount))
                        .build());
            } else {
                localeManager.sendMessage(sender, "command-take-lacking-funds", StringPlaceholders.builder("player", player.getName())
                        .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                        .build());
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
