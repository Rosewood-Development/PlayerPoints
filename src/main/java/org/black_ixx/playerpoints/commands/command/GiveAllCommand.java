package org.black_ixx.playerpoints.commands.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GiveAllCommand extends RoseCommand {

    public GiveAllCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Integer amount, @Optional Boolean includeOffline) { // Not sure if you want to make this use * instead of boolean
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        final PlayerPoints plugin = (PlayerPoints) this.rosePlugin;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean success;
            if (includeOffline != null && includeOffline) {
                success = plugin.getManager(DataManager.class).offsetAllPoints(amount);
            } else {
                List<UUID> playerIds = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());
                success = plugin.getAPI().giveAll(playerIds, amount);
            }

            if (success) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    locale.sendMessage(player, "command-give-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .addPlaceholder("currency", locale.getCurrencyName(amount))
                            .build());
                }

                locale.sendMessage(context.getSender(), "command-giveall-success", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                        .addPlaceholder("currency", locale.getCurrencyName(amount))
                        .build());
            }
        });

    }

    @Override
    protected String getDefaultName() {
        return "giveall";
    }

    @Override
    public String getDescriptionKey() {
        return "command-giveall-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.giveall";
    }
}
