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
 * Handles the set command.
 *
 * @author Mitsugaru
 */
public class SetCommand extends PointsCommand {

    public SetCommand() {
        super("set");
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (args.length < 2) {
            localeManager.sendMessage(sender, "command-set-usage");
            return;
        }

        OfflinePlayer player = PointsUtils.getPlayerByName(args[0]);
        if (!player.hasPlayedBefore() && !player.isOnline()) {
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

        plugin.getAPI().setAsync(player.getUniqueId(), amount).thenAccept(success -> {
            localeManager.sendMessage(sender, "command-set-success", StringPlaceholders.builder("player", player.getName())
                    .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                    .addPlaceholder("amount", PointsUtils.formatPoints(amount))
                    .build());
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
