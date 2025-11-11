package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.commands.arguments.StringSuggestingArgumentHandler;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand extends BasePointsCommand {

    public GiveCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context, String target, Integer amount, String silentFlag) {
        PointsUtils.getPlayerByName(target, player -> {
            CommandSender sender = context.getSender();
            if (player == null) {
                if (target.startsWith("*")) {
                    this.localeManager.sendCommandMessage(sender, "unknown-account", StringPlaceholders.of("account", target));
                } else {
                    this.localeManager.sendCommandMessage(sender, "unknown-player", StringPlaceholders.of("player", target));
                }
                return;
            }

            if (amount <= 0) {
                this.localeManager.sendCommandMessage(sender, "invalid-amount");
                return;
            }

            if (this.api.give(player.getFirst(), PointsUtils.getSenderUUID(sender), amount) && silentFlag == null) {
                // Send message to receiver
                Player onlinePlayer = Bukkit.getPlayer(player.getFirst());
                if (onlinePlayer != null) {
                    this.localeManager.sendCommandMessage(onlinePlayer, "command-give-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .add("currency", this.localeManager.getCurrencyName(amount))
                            .build());
                }

                // Send message to sender
                this.localeManager.sendCommandMessage(sender, "command-give-success", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                        .add("currency", this.localeManager.getCurrencyName(amount))
                        .add("player", player.getSecond())
                        .build());
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("give")
                .descriptionKey("command-give-description")
                .permission("playerpoints.give")
                .arguments(ArgumentsDefinition.builder()
                        .required("target", new StringSuggestingArgumentHandler(PointsUtils::getPlayerTabComplete))
                        .required("amount", ArgumentHandlers.INTEGER)
                        .optional("-s", ArgumentHandlers.forValues(String.class, "-s"))
                        .build())
                .build();
    }

}
