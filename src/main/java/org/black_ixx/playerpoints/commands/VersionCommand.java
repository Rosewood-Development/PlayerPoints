package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.bukkit.command.CommandSender;

public class VersionCommand extends BasePointsCommand {

    public VersionCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        CommandSender sender = context.getSender();
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);
        String baseColor = localeManager.getLocaleMessage("base-command-color");
        localeManager.sendCustomMessage(sender, baseColor + "Running <g:#E8A230:#ECD32D>PlayerPoints" + baseColor +
                " v" + this.rosePlugin.getDescription().getVersion());
        localeManager.sendCustomMessage(sender, baseColor + "Plugin created by: <g:#41E0F0:#FF8DCE>"
                + this.rosePlugin.getDescription().getAuthors().get(0) + baseColor + " & <g:#969696:#5C5C5C>"
                + this.rosePlugin.getDescription().getAuthors().get(1));
        localeManager.sendSimpleMessage(sender, "base-command-help");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("version")
                .descriptionKey("command-version-description")
                .permission("playerpoints.version")
                .build();
    }

}
