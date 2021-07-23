package org.black_ixx.playerpoints.commands;

import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.bukkit.command.CommandSender;

public class VersionCommand extends PointsCommand {

    public VersionCommand() {
        super("version", CommandManager.CommandAliases.VERSION);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        sendInfo(plugin, sender);
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    public static void sendInfo(PlayerPoints plugin, CommandSender sender) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);

        String baseColor = localeManager.getLocaleMessage("base-command-color");
        localeManager.sendCustomMessage(sender, baseColor + "Running <g:#E8A230:#ECD32D>PlayerPoints" + baseColor + " v" + plugin.getDescription().getVersion());
        localeManager.sendCustomMessage(sender, baseColor + "Plugin created by: <g:#41E0F0:#FF8DCE>" + plugin.getDescription().getAuthors().get(0) + baseColor + " & <g:#969696:#5C5C5C>" + plugin.getDescription().getAuthors().get(1));
        localeManager.sendSimpleMessage(sender, "base-command-help");
    }

}
