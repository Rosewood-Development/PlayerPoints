package org.black_ixx.playerpoints.commands;

import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
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
     * @param plugin  - PlayerPoints instance.
     * @param sender  - Sender of the command.
     * @param args    - Command arguments.
     */
    void execute(PlayerPoints plugin, CommandSender sender, String[] args);

    /**
     * Tab completion method for the command.
     *
     * @param plugin  - PlayerPoints instance.
     * @param sender  - Sender of the command.
     * @param args    - Command arguments.
     */
    List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args);

}
