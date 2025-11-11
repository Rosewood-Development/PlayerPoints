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

public class SetCommand extends BasePointsCommand {

    public SetCommand(PlayerPoints playerPoints) {
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

            if (amount < 0) {
                this.localeManager.sendCommandMessage(sender, "invalid-amount");
                return;
            }

            // Try to set the points for the player
            if (this.api.set(player.getFirst(), PointsUtils.getSenderUUID(sender), amount) && silentFlag == null) {
                this.localeManager.sendCommandMessage(sender, "command-set-success", StringPlaceholders.builder("player", player.getSecond())
                        .add("currency", this.localeManager.getCurrencyName(amount))
                        .add("amount", PointsUtils.formatPoints(amount))
                        .build());
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("set")
                .descriptionKey("command-set-description")
                .permission("playerpoints.set")
                .arguments(ArgumentsDefinition.builder()
                        .required("target", new StringSuggestingArgumentHandler(PointsUtils::getPlayerTabComplete))
                        .required("amount", ArgumentHandlers.INTEGER)
                        .optional("-s", ArgumentHandlers.forValues(String.class, "-s"))
                        .build())
                .build();
    }

}
