package org.black_ixx.playerpoints.services;

import java.util.EnumMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.models.Flag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Represents a command.
 * 
 * @author Mitsugaru
 */
public interface PointsCommand {

    /**
     * Execution method for the command.
     * 
     * @param sender
     *            - Sender of the command.
     * @param command
     *            - Command used.
     * @param label
     *            - Label.
     * @param args
     *            - Command arguments.
     * @return True if valid command and executed. Else false.
     */
    boolean execute(final PlayerPoints plugin, final CommandSender sender,
            final Command command, final String label, String[] args,
            EnumMap<Flag, String> info);

}