package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;
import org.black_ixx.playerpoints.commands.PointsCommandWrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public List<Class<? extends RoseCommandWrapper>> getRootCommands() {
        return Collections.singletonList(PointsCommandWrapper.class);
    }

    @Override
    public List<String> getArgumentHandlerPackages() {
        return Collections.singletonList("org.black_ixx.playerpoints.commands.argument");
    }

}
