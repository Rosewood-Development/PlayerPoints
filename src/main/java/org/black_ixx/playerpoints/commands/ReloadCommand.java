package org.black_ixx.playerpoints.commands;

import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements PointsCommand {

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (!PermissionNode.RELOAD.check(sender)) {
            localeManager.sendMessage(sender, "no-permission");
            return;
        }

        plugin.reload();
        localeManager.sendMessage(sender, "command-reload-success");
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
