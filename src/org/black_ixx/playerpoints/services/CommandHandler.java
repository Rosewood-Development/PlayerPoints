package org.black_ixx.playerpoints.services;

import java.util.*;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.models.Flag;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Abstract class to handle the majority of the logic dealing with commands.
 * Allows for a nested structure of commands.
 */
public abstract class CommandHandler implements CommandExecutor {
    /**
     * Registered commands for this handler.
     */
    protected final Map<String, PointsCommand> registeredCommands = new HashMap<>();
    /**
     * Registered subcommands and the handler associated with it.
     */
    protected final Map<String, CommandHandler> registeredHandlers = new HashMap<>();
    /**
     * Root plugin so that commands and handlers have access to the information.
     */
    protected PlayerPoints plugin;

    /**
     * Command name.
     */
    protected String cmd;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Root plugin.
     */
    public CommandHandler(PlayerPoints plugin, String cmd) {
        this.plugin = plugin;
        this.cmd = cmd;
    }

    /**
     * Register a command with an execution handler.
     * 
     * @param label
     *            - Command to listen for.
     * @param command
     *            - Execution handler that will handle the logic behind the
     *            command.
     */
    public void registerCommand(String label, PointsCommand command) {
        if(registeredCommands.containsKey(label)) {
            plugin.getLogger().warning(
                    "Replacing existing command for: " + label);
        }
        registeredCommands.put(label, command);
    }

    /**
     * Unregister a command for this handler.
     * 
     * @param label
     *            - Command to stop handling.
     */
    public void unregisterCommand(String label) {
        registeredCommands.remove(label);
    }

    /**
     * Register a subcommand with a command handler.
     * 
     * @param handler
     *            - Command handler.
     */
    public void registerHandler(CommandHandler handler) {
        if(registeredHandlers.containsKey(handler.getCommand())) {
            plugin.getLogger().warning(
                    "Replacing existing handler for: " + handler.getCommand());
        }
        registeredHandlers.put(handler.getCommand(), handler);
    }

    /**
     * Unregister a subcommand.
     * 
     * @param label
     *            - Subcommand to remove.
     */
    public void unregisterHandler(String label) {
        registeredHandlers.remove(label);
    }

    /**
     * Command loop that will go through the linked handlers until it finds the
     * appropriate handler or command execution handler to do the logic for.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        EnumMap<Flag, String> info = new EnumMap<>(Flag.class);
        info.put(Flag.TAG, PlayerPoints.TAG);
        if(args.length == 0) {
            return noArgs(sender, command, label, info);
        } else {
            final String subcmd = args[0].toLowerCase();
            // Check known handlers first and pass to them
            final CommandHandler handler = registeredHandlers.get(subcmd);
            if(handler != null) {
                return handler.onCommand(sender, command, label,
                        shortenArgs(args));
            }
            // Its our command, so handle it if its registered.
            final PointsCommand subCommand = registeredCommands.get(subcmd);
            if(subCommand == null) {
                return unknownCommand(sender, command, label, args, info);
            }
            // Execute command
            boolean value = true;
            try {
                value = subCommand.execute(plugin, sender, command, label,
                        shortenArgs(args), info);
            } catch(ArrayIndexOutOfBoundsException e) {
                sender.sendMessage(ChatColor.GRAY + PlayerPoints.TAG
                        + ChatColor.RED + " Missing parameters.");
            }
            return value;
        }
    }

    /**
     * Method that is called on a CommandHandler if there is no additional
     * arguments given that specify a specific command.
     * 
     * @param sender
     *            - Sender of the command.
     * @param command
     *            - Command used.
     * @param label
     *            - Command label.
     * @return True if handled. Should not need to return false...
     */
    public abstract boolean noArgs(CommandSender sender, Command command,
            String label, EnumMap<Flag, String> info);

    /**
     * Allow for the command handler to have special logic for unknown commands.
     * Useful for when expecting a player name parameter on a root command
     * handler command.
     * 
     * @param sender
     *            - Sender of the command.
     * @param command
     *            - Command used.
     * @param label
     *            - Command label.
     * @param args
     *            - Arguments.
     * @return True if handled. Should not need to return false...
     */
    public abstract boolean unknownCommand(CommandSender sender,
            Command command, String label, String[] args,
            EnumMap<Flag, String> info);

    /**
     * Shortens the given string array by removing the first entry.
     * 
     * @param args
     *            - Array to shorten.
     * @return Shortened array.
     */
    protected String[] shortenArgs(String[] args) {
        if(args.length == 0) {
            return args;
        }
        final List<String> argList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        return argList.toArray(new String[0]);
    }

    /**
     * Get the command for this handler.
     * 
     * @return Command
     */
    public String getCommand() {
        return cmd;
    }
}