package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveAllCommand extends BasePointsCommand {

    public GiveAllCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context, Integer amount, String includeOffline, String silentFlag) {
        this.rosePlugin.getScheduler().runTaskAsync(() -> {
            CommandSender sender = context.getSender();
            if (includeOffline != null) {
                this.rosePlugin.getManager(DataManager.class).offsetAllPoints(amount);
            } else {
                List<UUID> playerIds = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());
                this.api.giveAll(playerIds, PointsUtils.getSenderUUID(sender), amount);
            }

            if (silentFlag == null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    this.localeManager.sendCommandMessage(player, "command-give-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .add("currency", this.localeManager.getCurrencyName(amount))
                            .build());
                }

                this.localeManager.sendCommandMessage(sender, "command-giveall-success", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                        .add("currency", this.localeManager.getCurrencyName(amount))
                        .build());
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("giveall")
                .descriptionKey("command-giveall-description")
                .permission("playerpoints.giveall")
                .arguments(ArgumentsDefinition.builder()
                        .required("amount", ArgumentHandlers.INTEGER)
                        .optional("*", ArgumentHandlers.forValues(String.class, "*"))
                        .optional("-s", ArgumentHandlers.forValues(String.class, "-s"))
                        .build())
                .build();
    }

}
