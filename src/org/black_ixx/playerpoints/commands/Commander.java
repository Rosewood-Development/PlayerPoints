package org.black_ixx.playerpoints.commands;

import java.util.EnumMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.models.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.CommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Handles the commands for the root command.
 * 
 * @author Mitsugaru
 */
public class Commander extends CommandHandler {

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public Commander(PlayerPoints plugin) {
        super(plugin, "points");

        // Register commands.
        registerCommand("give", new GiveCommand());
        registerCommand("giveall", new GiveAllCommand());
        registerCommand("take", new TakeCommand());
        registerCommand("look", new LookCommand());
        registerCommand("pay", new PayCommand());
        registerCommand("set", new SetCommand());
        registerCommand("broadcast", new BroadcastCommand());
        registerCommand("reset", new ResetCommand());
        registerCommand("me", new MeCommand());
        registerCommand("reload", new ReloadCommand());

        // Register handlers
        registerHandler(new LeadCommand(plugin));
    }

    @Override
    public boolean noArgs(CommandSender sender, Command command, String label,
            EnumMap<Flag, String> info) {
        sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_HEADER,
                info));
        if(PermissionHandler.has(sender, PermissionNode.ME)) {
            sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_ME,
                    info));
        }
        if(PermissionHandler.has(sender, PermissionNode.GIVE)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.HELP_GIVE, info));
        }
        if(PermissionHandler.has(sender, PermissionNode.GIVEALL)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.HELP_GIVEALL, info));
        }
        if(PermissionHandler.has(sender, PermissionNode.TAKE)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.HELP_TAKE, info));
        }
        if(PermissionHandler.has(sender, PermissionNode.PAY)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.HELP_PAY, info));
        }
        if(PermissionHandler.has(sender, PermissionNode.LOOK)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.HELP_LOOK, info));
        }
        if(PermissionHandler.has(sender, PermissionNode.LEAD)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.HELP_LEAD, info));
        }
        if(PermissionHandler.has(sender, PermissionNode.BROADCAST)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.HELP_BROADCAST, info));
        }
        if(PermissionHandler.has(sender, PermissionNode.SET)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.HELP_SET, info));
        }
        if(PermissionHandler.has(sender, PermissionNode.RESET)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.HELP_RESET, info));
        }
        if(PermissionHandler.has(sender, PermissionNode.RELOAD)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.HELP_RELOAD, info));
        }
        return true;
    }

    @Override
    public boolean unknownCommand(CommandSender sender, Command command,
            String label, String[] args, EnumMap<Flag, String> info) {
        info.put(Flag.EXTRA, args[0]);
        sender.sendMessage(LocalizeConfig.parseString(
                LocalizeNode.COMMAND_UNKNOWN, info));
        return true;
    }
}