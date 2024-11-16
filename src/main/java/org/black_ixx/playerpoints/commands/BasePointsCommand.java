package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.black_ixx.playerpoints.manager.LocaleManager;

public abstract class BasePointsCommand extends BaseRoseCommand {

    protected final PlayerPointsAPI api;
    protected final LocaleManager localeManager;

    public BasePointsCommand(PlayerPoints playerPoints) {
        super(playerPoints);
        this.api = playerPoints.getAPI();
        this.localeManager = playerPoints.getManager(LocaleManager.class);
    }

}
