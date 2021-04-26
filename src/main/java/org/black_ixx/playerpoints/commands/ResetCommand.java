package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class ResetCommand implements PointsCommand {

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (!PermissionNode.RESET.check(sender)) {
            localeManager.sendMessage(sender, "no-permission");
            return;
        }

        if (args.length < 1) {
            localeManager.sendMessage(sender, "command-reset-usage");
            return;
        }

        OfflinePlayer player = PointsUtils.getPlayerByName(args[0]);
        if (!player.hasPlayedBefore()) {
            localeManager.sendMessage(sender, "unknown-player", StringPlaceholders.single("player", args[0]));
            return;
        }

        plugin.getAPI().resetAsync(player.getUniqueId()).thenAccept(success -> {
            localeManager.sendMessage(sender, "command-reset-success", StringPlaceholders.builder("player", player.getName())
                    .addPlaceholder("currency", localeManager.getCurrencyName(0))
                    .build());
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? PointsUtils.getPlayerTabComplete(args[0]) : Collections.emptyList();
    }

}
