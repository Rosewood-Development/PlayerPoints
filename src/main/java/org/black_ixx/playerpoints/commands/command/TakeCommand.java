package org.black_ixx.playerpoints.commands.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.Tuple;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;

import java.util.UUID;

public class TakeCommand extends RoseCommand {

    public TakeCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, String player, Integer amount) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        final PlayerPoints plugin = (PlayerPoints) this.rosePlugin;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Tuple<UUID, String> target = PointsUtils.getPlayerByName(player);

            if (target == null) {
                locale.sendMessage(context.getSender(), "argument-handler-offline-player", StringPlaceholders.single("player", player));
                return;
            }

            if (amount < 0) {
                locale.sendMessage(context.getSender(), "invalid-amount");
                return;
            }

            if (plugin.getAPI().take(target.getFirst(), amount)) {
                locale.sendMessage(context.getSender(), "command-take-success", StringPlaceholders.builder("player", target.getSecond())
                        .addPlaceholder("currency", locale.getCurrencyName(amount))
                        .addPlaceholder("amount", PointsUtils.formatPoints(amount))
                        .build());

                return;
            }

            locale.sendMessage(context.getSender(), "command-take-lacking-funds", StringPlaceholders.builder("player", target.getSecond())
                    .addPlaceholder("currency", locale.getCurrencyName(amount))
                    .build());

            plugin.getAPI().reset(target.getFirst());
        });
    }

    @Override
    protected String getDefaultName() {
        return "take";
    }

    @Override
    public String getDescriptionKey() {
        return "command-take-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.take";
    }

}
