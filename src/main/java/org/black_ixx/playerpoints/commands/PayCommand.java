package org.black_ixx.playerpoints.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.commands.arguments.StringSuggestingArgumentHandler;
import org.black_ixx.playerpoints.config.SettingKey;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PayCommand extends BasePointsCommand {

    private static final Cache<UUID, Long> PAY_COOLDOWN = CacheBuilder.newBuilder()
            .expireAfterWrite(500, TimeUnit.MILLISECONDS)
            .build();

    public PayCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context, String targetName, Integer amount) {
        Player player = (Player) context.getSender();
        if (PAY_COOLDOWN.getIfPresent(player.getUniqueId()) != null) {
            this.localeManager.sendCommandMessage(player, "command-cooldown");
            return;
        }

        PAY_COOLDOWN.put(player.getUniqueId(), System.currentTimeMillis());

        PointsUtils.getPlayerByName(targetName, target -> {
            if (target == null) {
                if (targetName.startsWith("*")) {
                    this.localeManager.sendCommandMessage(player, "unknown-account", StringPlaceholders.of("account", targetName));
                } else {
                    this.localeManager.sendCommandMessage(player, "unknown-player", StringPlaceholders.of("player", targetName));
                }
                return;
            }

            if (player.getUniqueId().equals(target.getFirst())) {
                this.localeManager.sendCommandMessage(player, "command-pay-self");
                return;
            }

            if (amount <= 0) {
                this.localeManager.sendCommandMessage(player, "invalid-amount");
                return;
            }

            int minAmount = SettingKey.MINIMUM_PAY_AMOUNT.get();
            if (amount < minAmount) {
                this.localeManager.sendCommandMessage(player, "command-pay-minimum-amount", StringPlaceholders.of("amount", PointsUtils.formatPoints(minAmount), "currency", this.localeManager.getCurrencyName(minAmount)));
                return;
            }

            if (this.api.pay(player.getUniqueId(), target.getFirst(), amount)) {
                // Send success message to sender
                this.localeManager.sendCommandMessage(player, "command-pay-sent", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                        .add("currency", this.localeManager.getCurrencyName(amount))
                        .add("player", target.getSecond())
                        .build());

                // Send success message to target
                Player onlinePlayer = Bukkit.getPlayer(target.getFirst());
                if (onlinePlayer != null) {
                    this.localeManager.sendCommandMessage(onlinePlayer, "command-pay-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .add("currency", this.localeManager.getCurrencyName(amount))
                            .add("player", player.getName())
                            .build());
                }
            } else {
                this.localeManager.sendCommandMessage(player, "command-pay-lacking-funds", StringPlaceholders.of("currency", this.localeManager.getCurrencyName(0)));
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("pay")
                .descriptionKey("command-pay-description")
                .permission("playerpoints.pay")
                .arguments(ArgumentsDefinition.builder()
                        .required("target", new StringSuggestingArgumentHandler(PointsUtils::getPlayerTabCompleteWithoutSelf))
                        .required("amount", ArgumentHandlers.INTEGER)
                        .build())
                .playerOnly()
                .build();
    }

}
