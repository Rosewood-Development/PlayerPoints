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

public class LookCommand extends RoseCommand {

    public LookCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, String player) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        final PlayerPoints plugin = (PlayerPoints) this.rosePlugin;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Tuple<UUID, String> target = PointsUtils.getPlayerByName(player);

            if (target == null) {
                locale.sendMessage(context.getSender(), "argument-handler-offline-player", StringPlaceholders.of("player", player));
                return;
            }

            int amount = plugin.getAPI().look(target.getFirst());

            locale.sendMessage(context.getSender(), "command-look-success", StringPlaceholders.builder("player", target.getSecond())
                    .add("amount", PointsUtils.formatPoints(amount))
                    .add("currency", locale.getCurrencyName(amount))
                    .build());
        });
    }

    @Override
    protected String getDefaultName() {
        return "look";
    }

    @Override
    public String getDescriptionKey() {
        return "command-look-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.look";
    }

}
