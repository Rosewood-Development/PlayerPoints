package org.black_ixx.playerpoints.commands.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MeCommand extends RoseCommand {

    public MeCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        PlayerPoints plugin = (PlayerPoints) this.rosePlugin;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int points = plugin.getAPI().look(player.getUniqueId());
            locale.sendMessage(player, "command-me-success", StringPlaceholders.builder("amount", PointsUtils.formatPoints(points))
                    .addPlaceholder("currency", locale.getCurrencyName(points))
                    .build());
        });
    }


    @Override
    protected String getDefaultName() {
        return "me";
    }

    @Override
    public String getDescriptionKey() {
        return "command-me-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.me";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
