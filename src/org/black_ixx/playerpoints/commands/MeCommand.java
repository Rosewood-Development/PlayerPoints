package org.black_ixx.playerpoints.commands;

import java.util.EnumMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.models.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.PointsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the me command.
 * 
 * @author Mitsugaru
 */
public class MeCommand implements PointsCommand {

    @Override
    public boolean execute(PlayerPoints plugin, CommandSender sender,
            Command command, String label, String[] args,
            EnumMap<Flag, String> info) {
        if(!(sender instanceof Player)) {
            final String consoleMessage = LocalizeConfig.parseString(
                    LocalizeNode.CONSOLE_DENY, info);
            if(!consoleMessage.isEmpty()) {
                sender.sendMessage(consoleMessage);
            }
            return true;
        }
        if(!PermissionHandler.has(sender, PermissionNode.ME)) {
            info.put(Flag.EXTRA, PermissionNode.ME.getNode());
            final String permMessage = LocalizeConfig.parseString(
                    LocalizeNode.PERMISSION_DENY, info);
            if(!permMessage.isEmpty()) {
                sender.sendMessage(permMessage);
            }
            return true;
        }
        info.put(Flag.AMOUNT, "" + plugin.getAPI().look(((Player)sender).getUniqueId()));
        final String meMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_ME,
                info);
        if(!meMessage.isEmpty()) {
            sender.sendMessage(meMessage);
        }
        return true;
    }
}