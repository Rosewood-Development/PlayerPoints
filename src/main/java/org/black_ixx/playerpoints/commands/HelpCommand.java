package org.black_ixx.playerpoints.commands;

import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.bukkit.command.CommandSender;

public class HelpCommand implements PointsCommand {

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);

        // Send header
        localeManager.sendMessage(sender, "command-help-title");

        // Send message for each command the sender can use
        if (PermissionNode.BROADCAST.check(sender)) localeManager.sendSimpleMessage(sender, "command-broadcast-description");
        if (PermissionNode.EXPORT.check(sender)) localeManager.sendSimpleMessage(sender, "command-export-description");
        if (PermissionNode.GIVE.check(sender)) localeManager.sendSimpleMessage(sender, "command-give-description");
        if (PermissionNode.GIVEALL.check(sender)) localeManager.sendSimpleMessage(sender, "command-giveall-description");
        localeManager.sendSimpleMessage(sender, "command-help-description");
        if (PermissionNode.IMPORT.check(sender)) localeManager.sendSimpleMessage(sender, "command-import-description");
        if (PermissionNode.LEAD.check(sender)) localeManager.sendSimpleMessage(sender, "command-lead-description");
        if (PermissionNode.LOOK.check(sender)) localeManager.sendSimpleMessage(sender, "command-look-description");
        if (PermissionNode.ME.check(sender)) localeManager.sendSimpleMessage(sender, "command-me-description");
        if (PermissionNode.PAY.check(sender)) localeManager.sendSimpleMessage(sender, "command-pay-description");
        if (PermissionNode.RELOAD.check(sender)) localeManager.sendSimpleMessage(sender, "command-reload-description");
        if (PermissionNode.RESET.check(sender)) localeManager.sendSimpleMessage(sender, "command-reset-description");
        if (PermissionNode.SET.check(sender)) localeManager.sendSimpleMessage(sender, "command-set-description");
        if (PermissionNode.TAKE.check(sender)) localeManager.sendSimpleMessage(sender, "command-take-description");
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
