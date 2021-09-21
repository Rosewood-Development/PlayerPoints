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

public class PayCommand extends PointsCommand {

    public PayCommand() {
        super("pay", CommandManager.CommandAliases.PAY);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (!(sender instanceof Player)) {
            localeManager.sendMessage(sender, "no-console");
            return;
        }

        if (args.length < 2) {
            localeManager.sendMessage(sender, "command-pay-usage");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer target = PointsUtils.getPlayerByName(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
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

            Player player = (Player) sender;
            if (plugin.getAPI().pay(player.getUniqueId(), target.getUniqueId(), amount)) {
                // Send success message to sender
                localeManager.sendMessage(sender, "command-pay-sent", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                        .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                        .addPlaceholder("player", target.getName())
                        .build());

                // Send success message to target
                if (target.isOnline()) {
                    localeManager.sendMessage((Player) target, "command-pay-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                            .addPlaceholder("player", player.getName())
                            .build());
                }
            } else {
                localeManager.sendMessage(sender, "command-pay-lacking-funds", StringPlaceholders.single("currency", localeManager.getCurrencyName(0)));
            }
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return Collections.emptyList();

        if (args.length == 1) {
            return PointsUtils.getPlayerTabComplete(args[0]);
        } else if (args.length == 2) {
            return Collections.singletonList("<amount>");
        } else {
            return Collections.emptyList();
        }
    }

}
