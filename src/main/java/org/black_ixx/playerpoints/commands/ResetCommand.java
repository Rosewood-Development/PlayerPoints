package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.commands.arguments.StringSuggestingArgumentHandler;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.command.CommandSender;

public class ResetCommand extends BasePointsCommand {

    public ResetCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context, String target) {
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

            if (this.api.reset(player.getFirst(), PointsUtils.getSenderUUID(sender))) {
                this.localeManager.sendCommandMessage(sender, "command-reset-success", StringPlaceholders.builder("player", player.getSecond())
                        .add("currency", this.localeManager.getCurrencyName(0))
                        .build());
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("reset")
                .descriptionKey("command-reset-description")
                .permission("playerpoints.reset")
                .arguments(ArgumentsDefinition.builder()
                        .required("target", new StringSuggestingArgumentHandler(PointsUtils::getPlayerTabComplete))
                        .build())
                .build();
    }

}
