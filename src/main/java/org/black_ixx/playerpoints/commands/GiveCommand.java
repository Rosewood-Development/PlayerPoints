package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the give command.
 *
 * @author Mitsugaru
 */
public class GiveCommand implements PointsCommand {

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (!PermissionNode.GIVE.check(sender)) {
            localeManager.sendMessage(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            localeManager.sendMessage(sender, "command-give-usage");
            return;
        }

        OfflinePlayer player = PointsUtils.getPlayerByName(args[0]);
        if (!player.hasPlayedBefore()) {
            localeManager.sendMessage(sender, "unknown-player", StringPlaceholders.single("player", args[0]));
            return;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                localeManager.sendMessage(sender, "invalid-amount");
                return;
            }

            plugin.getAPI().giveAsync(player.getUniqueId(), amount).thenAccept(success -> {
                if (success) {
                    // Send message to receiver
                    if (player.isOnline()) {
                        localeManager.sendMessage((Player) player, "command-give-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                                .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                                .build());
                    }

                    // Send message to sender
                    plugin.getAPI().lookAsync(player.getUniqueId()).thenAccept(total -> {
                        localeManager.sendMessage(sender, "command-give-success", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                                .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                                .addPlaceholder("player", player.getName())
                                .build());
                    });
                }
            });
        } catch (NumberFormatException notnumber) {
            localeManager.sendMessage(sender, "invalid-amount");
        }
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
