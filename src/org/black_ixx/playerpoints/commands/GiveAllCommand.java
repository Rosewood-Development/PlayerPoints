package org.black_ixx.playerpoints.commands;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.models.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.PointsCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveAllCommand implements PointsCommand {

    @Override
    public boolean execute(PlayerPoints plugin, CommandSender sender,
            Command command, String label, String[] args,
            EnumMap<Flag, String> info) {
        if(!PermissionHandler.has(sender, PermissionNode.GIVEALL)) {
            info.put(Flag.EXTRA, PermissionNode.GIVEALL.getNode());
            final String permMessage = LocalizeConfig.parseString(
                    LocalizeNode.PERMISSION_DENY, info);
            if(!permMessage.isEmpty()) {
                sender.sendMessage(permMessage);
            }
            return true;
        }
        if(args.length < 1) {
            final String argMessage = LocalizeConfig.parseString(
                    LocalizeNode.COMMAND_GIVEALL, info);
            if(!argMessage.isEmpty()) {
                sender.sendMessage(argMessage);
            }
            return true;
        }
        try {
            final int anzahl = Integer.parseInt(args[0]);
            info.put(Flag.AMOUNT, String.valueOf(anzahl));
            List<String> unsuccessful = new ArrayList<>();
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player != null) {
                    if(plugin.getAPI().give(player.getUniqueId(), anzahl)) {
                        info.put(Flag.PLAYER, sender.getName());
                        final String receiveMessage = LocalizeConfig.parseString(
                                LocalizeNode.POINTS_PAY_RECEIVE, info);
                        if(!receiveMessage.isEmpty()) {
                            player.sendMessage(receiveMessage);
                        }
                    } else {
                        unsuccessful.add(player.getName());
                    }
                }
            }
            info.put(
                    Flag.PLAYER,
                    String.valueOf(Bukkit.getOnlinePlayers().size()
                            - unsuccessful.size()));
            final String successMessage = LocalizeConfig.parseString(
                    LocalizeNode.POINTS_SUCCESS_ALL, info);
            if(!successMessage.isEmpty()) {
                sender.sendMessage(successMessage);
            }
            if(!unsuccessful.isEmpty()) {
                // TODO maybe tell them which players failed...
                info.put(Flag.PLAYER, String.valueOf(unsuccessful.size()));
                final String failMessage = LocalizeConfig.parseString(
                        LocalizeNode.POINTS_FAIL_ALL, info);
                if(!failMessage.isEmpty()) {
                    sender.sendMessage(failMessage);
                }
            }
        } catch(NumberFormatException notnumber) {
            info.put(Flag.EXTRA, args[1]);
            final String errorMessage = LocalizeConfig.parseString(
                    LocalizeNode.NOT_INTEGER, info);
            if(!errorMessage.isEmpty()) {
                sender.sendMessage(errorMessage);
            }
        }
        return true;
    }

}
