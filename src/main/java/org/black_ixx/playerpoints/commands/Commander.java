package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
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
     * @param plugin - Plugin instance.
     */
    public Commander(PlayerPoints plugin) {
        super(plugin, "points");

        // Register commands.
        this.registerCommand("help", new HelpCommand(this));
        this.registerCommand("give", new GiveCommand());
        this.registerCommand("giveall", new GiveAllCommand());
        this.registerCommand("take", new TakeCommand());
        this.registerCommand("look", new LookCommand());
        this.registerCommand("pay", new PayCommand());
        this.registerCommand("set", new SetCommand());
        this.registerCommand("broadcast", new BroadcastCommand());
        this.registerCommand("reset", new ResetCommand());
        this.registerCommand("me", new MeCommand());
        this.registerCommand("reload", new ReloadCommand());
        this.registerCommand("export", new ExportCommand());
        this.registerCommand("import", new ImportCommand());

        // Register handlers
        this.registerHandler(new LeadCommand(plugin));
    }

    @Override
    public void noArgs(CommandSender sender) {
        LocaleManager localeManager = this.plugin.getManager(LocaleManager.class);

        String baseColor = localeManager.getLocaleMessage("base-command-color");
        localeManager.sendCustomMessage(sender, baseColor + "Running <g:#E8A230:#ECD32D>PlayerPoints" + baseColor + " v" + this.plugin.getDescription().getVersion());
        localeManager.sendCustomMessage(sender, baseColor + "Plugin created by: <g:#41E0F0:#FF8DCE>" + this.plugin.getDescription().getAuthors().get(0) + baseColor + " & <g:#969696:#5C5C5C>" + this.plugin.getDescription().getAuthors().get(1));
        localeManager.sendSimpleMessage(sender, "base-command-help");
    }

    @Override
    public void unknownCommand(CommandSender sender, String[] args) {
        this.plugin.getManager(LocaleManager.class).sendMessage(sender, "unknown-command", StringPlaceholders.single("input", args[0]));
    }

}
