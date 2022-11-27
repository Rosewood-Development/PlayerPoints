package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;

/**
 * Abstract class to handle the majority of the logic dealing with commands.
 * Allows for a nested structure of commands.
 */
public abstract class CommandHandler implements TabExecutor, NamedExecutor {
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
     * Command aliases.
     */
    protected CommandManager.CommandAliases aliases;

    /**
     * Constructor.
     *
     * @param plugin - Root plugin.
     */
    public CommandHandler(PlayerPoints plugin, String cmd, CommandManager.CommandAliases aliases) {
        this.plugin = plugin;
        this.cmd = cmd;
        this.aliases = aliases;
    }

    /**
     * Register a command with an execution handler.
     *
     * @param command - Execution handler that will handle the logic behind the command.
     */
    public void registerCommand(PointsCommand command) {
        for (String alias : command.getAliases()) {
            if (this.registeredCommands.containsKey(alias))
                this.plugin.getLogger().warning("Conflicting command aliases for '" + alias + "', overwriting.");
            this.registeredCommands.put(alias, command);
        }
    }

    /**
     * Register a subcommand with a command handler.
     *
     * @param handler - Command handler.
     */
    public void registerHandler(CommandHandler handler) {
        for (String alias : handler.getAliases()) {
            if (this.registeredCommands.containsKey(alias))
                this.plugin.getLogger().warning("Conflicting handler aliases for '" + alias + "', overwriting.");
            this.registeredHandlers.put(alias, handler);
        }
    }

    /**
     * Command loop that will go through the linked handlers until it finds the
     * appropriate handler or command execution handler to do the logic for.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.noArgs(sender);
            return true;
        }

        String subcmd = args[0].toLowerCase();

        // Parse placeholders for the console
        if (sender instanceof ConsoleCommandSender)
            for (int i = 1; i < args.length; i++)
                if (args[i].startsWith("%"))
                    args[i] = PlaceholderAPIHook.applyPlaceholders(null, args[i]);

        // Parse selectors in command blocks
        if (sender instanceof BlockCommandSender || sender instanceof CommandMinecart) {
            for (int i = 1; i < args.length; i++) {
                String selector = args[i];
                if (!selector.startsWith("@"))
                    continue;

                try {
                    List<Entity> selectedEntities = Bukkit.selectEntities(sender, selector);
                    if (selectedEntities.isEmpty()) {
                        sender.sendMessage("Error: No entities found for selector '" + selector + "'");
                        return true;
                    }

                    if (selectedEntities.size() > 1) {
                        sender.sendMessage("Error: More than one entity found for selector '" + selector + "'");
                        return true;
                    }

                    Entity entity = selectedEntities.get(0);
                    if (!(entity instanceof Player)) {
                        sender.sendMessage("Error: Entity '" + entity.getName() + "' is not a player");
                        return true;
                    }

                    args[i] = entity.getName();
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("Error: Invalid player selector '" + selector + "'");
                    return true;
                }
            }
        }

        // Check known handlers first and pass to them
        CommandHandler handler = this.registeredHandlers.get(subcmd);
        if (handler != null) {
            // Make sure they have permission
            if (!handler.hasPermission(sender)) {
                this.plugin.getManager(LocaleManager.class).sendMessage(sender, "no-permission");
                return true;
            }
            handler.onCommand(sender, command, label, this.shortenArgs(args));
            return true;
        }

        // Its our command, so handle it if its registered.
        PointsCommand subCommand = this.registeredCommands.get(subcmd);
        if (subCommand == null) {
            this.unknownCommand(sender, args);
            return true;
        }

        // Make sure they have permission
        if (!subCommand.hasPermission(sender)) {
            this.plugin.getManager(LocaleManager.class).sendMessage(sender, "no-permission");
            return true;
        }

        // Execute command
        try {
            subCommand.execute(this.plugin, sender, this.shortenArgs(args));
        } catch (ArrayIndexOutOfBoundsException e) {
            sender.sendMessage(ChatColor.RED + "A PlayerPoints error occurred while executing that command. Did you enter an invalid parameter?");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0)
            return Collections.emptyList();

        String subcmd = args[0].toLowerCase();
        if (args.length == 1) {
            // Complete against command names the sender has permission for
            List<String> commandNames = new ArrayList<>();

            commandNames.addAll(this.registeredHandlers.entrySet().stream()
                    .filter(x -> x.getValue().hasPermission(sender))
                    .map(Map.Entry::getKey).collect(Collectors.toList()));

            commandNames.addAll(this.registeredCommands.entrySet().stream()
                    .filter(x -> x.getValue().hasPermission(sender))
                    .map(Map.Entry::getKey).collect(Collectors.toList()));

            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(subcmd, commandNames, completions);
            return completions;
        }

        // Try to find a handler to pass to
        CommandHandler handler = this.registeredHandlers.get(subcmd);
        if (handler != null && handler.hasPermission(sender))
            return handler.onTabComplete(sender, command, alias, this.shortenArgs(args));

        // Look for a command to pass to
        PointsCommand subCommand = this.registeredCommands.get(subcmd);
        if (subCommand != null && subCommand.hasPermission(sender))
            return subCommand.tabComplete(this.plugin, sender, this.shortenArgs(args));

        // No matching commands, return an empty list
        return Collections.emptyList();
    }

    /**
     * Method that is called on a CommandHandler if there is no additional
     * arguments given that specify a specific command.
     *
     * @param sender  - Sender of the command.
     */
    public abstract void noArgs(CommandSender sender);

    /**
     * Allow for the command handler to have special logic for unknown commands.
     * Useful for when expecting a player name parameter on a root command
     * handler command.
     *
     * @param sender  - Sender of the command.
     * @param args    - Arguments.
     */
    public abstract void unknownCommand(CommandSender sender, String[] args);

    /**
     * @return a combination of all executable commands and handlers sorted by name
     */
    public List<NamedExecutor> getExecutables() {
        Set<NamedExecutor> executors = new HashSet<>();
        executors.addAll(this.registeredHandlers.values());
        executors.addAll(this.registeredCommands.values());
        List<NamedExecutor> sortedExecutors = new ArrayList<>(executors);
        sortedExecutors.sort(Comparator.comparing(NamedExecutor::getName));
        return sortedExecutors;
    }

    @Override
    public String getName() {
        return this.cmd;
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = this.aliases.get();
        if (aliases.isEmpty()) {
            return Collections.singletonList(this.cmd);
        } else {
            return aliases;
        }
    }

    @Override
    public boolean hasPermission(Permissible permissible) {
        return true;
    }

    /**
     * Shortens the given string array by removing the first entry.
     *
     * @param args - Array to shorten.
     * @return Shortened array.
     */
    protected String[] shortenArgs(String[] args) {
        if (args.length == 0) {
            return args;
        }
        final List<String> argList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        return argList.toArray(new String[0]);
    }

}
