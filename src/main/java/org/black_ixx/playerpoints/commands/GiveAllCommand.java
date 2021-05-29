package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveAllCommand extends PointsCommand {

    public GiveAllCommand() {
        super("giveall");
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (args.length < 1) {
            localeManager.sendMessage(sender, "command-giveall-usage");
            return;
        }

        try {
            int amount = Integer.parseInt(args[0]);
            if (amount <= 0) {
                localeManager.sendMessage(sender, "invalid-amount");
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    plugin.getAPI().giveAsync(player.getUniqueId(), amount).thenAccept(success -> {
                        if (success) {
                            localeManager.sendMessage(player, "command-give-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                                    .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                                    .build());
                        }
                    });
                }

                localeManager.sendMessage(sender, "command-giveall-success", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                        .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                        .build());
            });
        } catch (NumberFormatException e) {
            localeManager.sendMessage(sender, "invalid-amount");
        }
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? Collections.singletonList("<amount>") : Collections.emptyList();
    }

}
