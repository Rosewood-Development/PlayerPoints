package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.commands.arguments.StringSuggestingArgumentHandler;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastCommand extends BasePointsCommand {

    public BroadcastCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context, String target) {
        PointsUtils.getPlayerByName(target, player -> {
            if (player == null) {
                if (target.startsWith("*")) {
                    this.localeManager.sendCommandMessage(context.getSender(), "unknown-account", StringPlaceholders.of("account", target));
                } else {
                    this.localeManager.sendCommandMessage(context.getSender(), "unknown-player", StringPlaceholders.of("player", target));
                }
                return;
            }

            int points = this.api.look(player.getFirst());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                this.localeManager.sendCommandMessage(onlinePlayer, "command-broadcast-message", StringPlaceholders.builder("player", player.getSecond())
                        .add("amount", PointsUtils.formatPoints(points))
                        .add("currency", this.localeManager.getCurrencyName(points)).build());
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("broadcast")
                .descriptionKey("command-broadcast-description")
                .permission("playerpoints.broadcast")
                .arguments(ArgumentsDefinition.builder()
                        .required("target", new StringSuggestingArgumentHandler(PointsUtils::getPlayerTabComplete))
                        .build())
                .build();
    }

}
