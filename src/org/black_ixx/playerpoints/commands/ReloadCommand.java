package org.black_ixx.playerpoints.commands;

import java.util.EnumMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.models.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.PointsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements PointsCommand {

    @Override
    public boolean execute(PlayerPoints plugin, CommandSender sender,
            Command command, String label, String[] args,
            EnumMap<Flag, String> info) {
        if(!PermissionHandler.has(sender, PermissionNode.RELOAD)) {
            info.put(Flag.EXTRA, PermissionNode.RELOAD.getNode());
            final String permMessage = LocalizeConfig.parseString(
                    LocalizeNode.PERMISSION_DENY, info);
            if(!permMessage.isEmpty()) {
                sender.sendMessage(permMessage);
            }
            return true;
        }
        plugin.getModuleForClass(RootConfig.class).reloadConfig();
        LocalizeConfig.reload();
        final String reloadMessage = LocalizeConfig.parseString(LocalizeNode.RELOAD, info);
        if(!reloadMessage.isEmpty()) {
            sender.sendMessage(reloadMessage);
        }
        return true;
    }
}