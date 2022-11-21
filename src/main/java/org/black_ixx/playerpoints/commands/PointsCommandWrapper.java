package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PointsCommandWrapper extends RoseCommandWrapper {

    public PointsCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "points";
    }

    @Override
    public List<String> getDefaultAliases() {
        return Collections.singletonList("p");
    }

    @Override
    public List<String> getCommandPackages() {
        return Collections.singletonList("org.black_ixx.playerpoints.commands.command");
    }

    @Override
    public boolean includeBaseCommand() {
        return true;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }

}
