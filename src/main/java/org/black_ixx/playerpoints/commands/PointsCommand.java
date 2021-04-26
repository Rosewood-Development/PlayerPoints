package org.black_ixx.playerpoints.commands;

import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

/**
 * Represents a command.
 *
 * @author Mitsugaru
 */
public abstract class PointsCommand implements NamedExecutor {

    private final String name;

    public PointsCommand(String name) {
        this.name = name;
    }

    /**
     * Execution method for the command.
     *
     * @param plugin  - PlayerPoints instance.
     * @param sender  - Sender of the command.
     * @param args    - Command arguments.
     */
    public abstract void execute(PlayerPoints plugin, CommandSender sender, String[] args);

    /**
     * Tab completion method for the command.
     *
     * @param plugin  - PlayerPoints instance.
     * @param sender  - Sender of the command.
     * @param args    - Command arguments.
     */
    public abstract List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args);

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasPermission(Permissible permissible) {
        return permissible.hasPermission("playerpoints." + this.name);
    }

}
