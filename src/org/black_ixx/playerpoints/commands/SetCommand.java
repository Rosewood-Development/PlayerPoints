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

/**
 * Handles the set command.
 * 
 * @author Mitsugaru
 */
public class SetCommand implements PointsCommand {

    @Override
    public boolean execute(PlayerPoints plugin, CommandSender sender,
            Command command, String label, String[] args,
            EnumMap<Flag, String> info) {
        if(!PermissionHandler.has(sender, PermissionNode.SET)) {
            info.put(Flag.EXTRA, PermissionNode.SET.getNode());
            final String permMessage = LocalizeConfig.parseString(
                    LocalizeNode.PERMISSION_DENY, info);
            if(!permMessage.isEmpty()) {
                sender.sendMessage(permMessage);
            }
            return true;
        }
        if(args.length < 2) {
            final String argMessage = LocalizeConfig.parseString(
                    LocalizeNode.COMMAND_SET, info);
            if(!argMessage.isEmpty()) {
                sender.sendMessage(argMessage);
            }
            return true;
        }
        try {
            int intanzahl = Integer.parseInt(args[1]);
            String playerName = null;
            if(plugin.getModuleForClass(RootConfig.class).autocompleteOnline) {
                playerName = plugin.expandName(args[0]);
            }
            if(playerName == null) {
                playerName = args[0];
            }
            if(plugin.getAPI().set(plugin.translateNameToUUID(playerName), intanzahl)) {
                info.put(Flag.PLAYER, playerName);
                info.put(Flag.AMOUNT, "" + intanzahl);
                final String successMessage = LocalizeConfig.parseString(
                        LocalizeNode.POINTS_SUCCESS, info);
                if(!successMessage.isEmpty()) {
                    sender.sendMessage(successMessage);
                }
            } else {
                final String failMessage = LocalizeConfig.parseString(
                        LocalizeNode.POINTS_FAIL, info);
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