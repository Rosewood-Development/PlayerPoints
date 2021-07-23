package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveAllCommand extends PointsCommand {

    public GiveAllCommand() {
        super("giveall", CommandManager.CommandAliases.GIVEALL);
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
            boolean includeOffline = args.length > 1 && args[1].equals("*");

            Consumer<Boolean> finish = success -> {
                if (success) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        localeManager.sendMessage(player, "command-give-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                                .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                                .build());
                    }

                    localeManager.sendMessage(sender, "command-giveall-success", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .addPlaceholder("currency", localeManager.getCurrencyName(amount))
                            .build());
                }
            };

            if (includeOffline) {
                plugin.getManager(DataManager.class).offsetAllPoints(amount).thenAccept(finish);
            } else {
                List<UUID> playerIds = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());
                plugin.getAPI().giveAllAsync(playerIds, amount).thenAccept(finish);
            }

        } catch (NumberFormatException e) {
            localeManager.sendMessage(sender, "invalid-amount");
        }
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Collections.singletonList("<amount>");
            case 2:
                return Collections.singletonList("*");
            default:
                return Collections.emptyList();
        }
    }

}
