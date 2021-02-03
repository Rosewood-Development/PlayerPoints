package org.black_ixx.playerpoints.commands;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.models.Flag;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.CommandHandler;
import org.black_ixx.playerpoints.storage.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Handles the leader board commands.
 * 
 * @author Mitsugaru
 */
public class LeadCommand extends CommandHandler {

    /**
     * Entries per page limit.
     */
    private static final int LIMIT = 10;

    /**
     * Current page the player is viewing.
     */
    private final Map<String, Integer> page = new HashMap<String, Integer>();

    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public LeadCommand(PlayerPoints plugin) {
        super(plugin, "lead");
    }

    @Override
    public boolean noArgs(CommandSender sender, Command command, String label,
                          EnumMap<Flag, String> info) {
        // Check permissions
        if (!PermissionHandler.has(sender, PermissionNode.LEAD)) {
            info.put(Flag.EXTRA, PermissionNode.LEAD.getNode());
            final String permMessage = LocalizeConfig.parseString(
                    LocalizeNode.PERMISSION_DENY, info);
            if (!permMessage.isEmpty()) {
                sender.sendMessage(permMessage);
            }
            return true;
        }

        SortedSet<SortedPlayer> leaders = sortLeaders(plugin, plugin
                .getModuleForClass(StorageHandler.class).getPlayers());

        int current = 0;
        if (page.containsKey(sender.getName())) {
            current = page.get(sender.getName());
        }

        int num = leaders.size() / LIMIT;
        double rem = (double) leaders.size() % (double) LIMIT;
        if (rem != 0) {
            num++;
        }

        // Bounds check
        if (current < 0) {
            current = 0;
            page.put(sender.getName(), current);
        } else if (current >= num) {
            current = num - 1;
            page.put(sender.getName(), current);
        }

        SortedPlayer[] array = leaders.toArray(new SortedPlayer[0]);

        if (leaders.isEmpty()) {
            current = 0;
            num = 0;
        }

        // Header
        sender.sendMessage(ChatColor.BLUE + "=== " + ChatColor.GRAY
                + PlayerPoints.TAG + " Leader Board " + ChatColor.BLUE + "=== "
                + ChatColor.GRAY + (current + 1) + ":" + num);

        // Page through
        for (int i = current * LIMIT; i < (current * LIMIT + LIMIT); i++) {
            if (i >= array.length) {
                break;
            }
            SortedPlayer player = array[i];
            sender.sendMessage(ChatColor.AQUA + "" + (i + 1) + ". "
                    + ChatColor.GRAY + Bukkit.getOfflinePlayer(UUID.fromString(player.getName())).getName() + ChatColor.WHITE
                    + " - " + ChatColor.GOLD + player.getPoints());
        }

        return true;
    }

    @Override
    public boolean unknownCommand(CommandSender sender, Command command,
                                  String label, String[] args, EnumMap<Flag, String> info) {
        String com = args[0];

        int current = 0;
        if (page.containsKey(sender.getName())) {
            current = page.get(sender.getName());
        }

        boolean valid = false;

        if (com.equalsIgnoreCase("prev")) {
            page.put(sender.getName(), --current);
            noArgs(sender, command, label, info);
            valid = true;
        } else if (com.equals("next")) {
            page.put(sender.getName(), ++current);
            noArgs(sender, command, label, info);
            valid = true;
        } else {
            try {
                current = Integer.parseInt(com);
                page.put(sender.getName(), current - 1);
                noArgs(sender, command, label, info);
                valid = true;
            } catch (NumberFormatException e) {
                // Handle notification later
            }
        }

        // Handle invalid input
        if (!valid) {
            info.put(Flag.EXTRA, args[0]);
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.COMMAND_LEAD, info));
        }

        return true;
    }

    /**
     * Sorts the given players by their point value and name.
     *
     * @param plugin  - Plugin instance.
     * @param players - All player names in storage.
     * @return Set of sorted players.
     */
    private SortedSet<SortedPlayer> sortLeaders(PlayerPoints plugin,
                                                Collection<String> players) {
        SortedSet<SortedPlayer> sorted = new TreeSet<SortedPlayer>();

        for (String name : players) {
            int points = plugin.getAPI().look(UUID.fromString(name));
            sorted.add(new SortedPlayer(name, points));
        }

        return sorted;
    }
}