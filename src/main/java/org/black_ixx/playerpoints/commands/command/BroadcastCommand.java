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
import org.bukkit.entity.Player;

import java.util.UUID;

public class BroadcastCommand extends RoseCommand {

    public BroadcastCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, String player) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        PlayerPoints plugin = (PlayerPoints) this.rosePlugin; // Could be replaced with getInstance();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Tuple<UUID, String> target = PointsUtils.getPlayerByName(player);

            if (target == null) {
                locale.sendMessage(context.getSender(), "argument-handler-offline-player", StringPlaceholders.single("player", player));
                return;
            }

            int points = plugin.getAPI().look(target.getFirst());

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                locale.sendMessage(onlinePlayer, "command-broadcast-message", StringPlaceholders.builder("player", target.getSecond())
                        .addPlaceholder("amount", PointsUtils.formatPoints(points))
                        .addPlaceholder("currency", locale.getCurrencyName(points))
                        .build());
            }
        });
    }

    @Override
    protected String getDefaultName() {
        return "broadcast";
    }

    @Override
    public String getDescriptionKey() {
        return "command-broadcast-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.broadcast";
    }

}
