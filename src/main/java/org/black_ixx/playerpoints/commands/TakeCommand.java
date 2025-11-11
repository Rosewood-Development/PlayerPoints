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
import org.bukkit.command.CommandSender;

public class TakeCommand extends BasePointsCommand {

    public TakeCommand(PlayerPoints playerPoints) {
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

            // Try to take points from the player
            if (this.api.take(player.getFirst(), PointsUtils.getSenderUUID(sender), amount) && silentFlag == null) {
                this.localeManager.sendCommandMessage(sender, "command-take-success", StringPlaceholders.builder("player", player.getSecond())
                        .add("currency", this.localeManager.getCurrencyName(amount))
                        .add("amount", PointsUtils.formatPoints(amount))
                        .build());
            } else if (silentFlag == null) {
                this.localeManager.sendCommandMessage(sender, "command-take-not-enough", StringPlaceholders.builder("player", player.getSecond())
                        .add("currency", this.localeManager.getCurrencyName(amount))
                        .build());
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("take")
                .descriptionKey("command-take-description")
                .permission("playerpoints.take")
                .arguments(ArgumentsDefinition.builder()
                        .required("target", new StringSuggestingArgumentHandler(PointsUtils::getPlayerTabComplete))
                        .required("amount", ArgumentHandlers.INTEGER)
                        .optional("-s", ArgumentHandlers.forValues(String.class, "-s"))
                        .build())
                .build();
    }

}
