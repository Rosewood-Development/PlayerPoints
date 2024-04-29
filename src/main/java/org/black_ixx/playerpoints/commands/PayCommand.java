package org.black_ixx.playerpoints.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand extends PointsCommand {

    private static final Cache<UUID, Long> PAY_COOLDOWN = CacheBuilder.newBuilder()
            .expireAfterWrite(500, TimeUnit.MILLISECONDS)
            .build();

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

        Player player = (Player) sender;
        if (args.length < 2) {
            localeManager.sendMessage(player, "command-pay-usage");
            return;
        }

        if (PAY_COOLDOWN.getIfPresent(player.getUniqueId()) != null) {
            localeManager.sendMessage(player, "command-cooldown");
            return;
        }

        PAY_COOLDOWN.put(player.getUniqueId(), System.currentTimeMillis());

        PointsUtils.getPlayerByName(args[0], target -> {
            if (target == null) {
                localeManager.sendMessage(player, "unknown-player", StringPlaceholders.of("player", args[0]));
                return;
            }

            if (player.getUniqueId().equals(target.getFirst())) {
                localeManager.sendMessage(player, "command-pay-self");
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                localeManager.sendMessage(player, "invalid-amount");
                return;
            }

            if (amount <= 0) {
                localeManager.sendMessage(player, "invalid-amount");
                return;
            }

            if (plugin.getAPI().pay(player.getUniqueId(), target.getFirst(), amount)) {
                // Send success message to sender
                localeManager.sendMessage(player, "command-pay-sent", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                        .add("currency", localeManager.getCurrencyName(amount))
                        .add("player", target.getSecond())
                        .build());

                // Send success message to target
                Player onlinePlayer = Bukkit.getPlayer(target.getFirst());
                if (onlinePlayer != null) {
                    localeManager.sendMessage(onlinePlayer, "command-pay-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .add("currency", localeManager.getCurrencyName(amount))
                            .add("player", player.getName())
                            .build());
                }
            } else {
                localeManager.sendMessage(player, "command-pay-lacking-funds", StringPlaceholders.of("currency", localeManager.getCurrencyName(0)));
            }
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return Collections.emptyList();

        if (args.length == 1) {
            List<String> completions = PointsUtils.getPlayerTabComplete(args[0]);
            completions.remove(sender.getName());
            return completions;
        } else if (args.length == 2) {
            return Collections.singletonList("<amount>");
        } else {
            return Collections.emptyList();
        }
    }

}
