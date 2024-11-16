package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.commands.BaseCommand;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Function<RosePlugin, BaseRoseCommand>> getRootCommands() {
        return Collections.singletonList(plugin -> new BaseCommand(PlayerPoints.getInstance()));
    }

}
