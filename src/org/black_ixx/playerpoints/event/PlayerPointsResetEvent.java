package org.black_ixx.playerpoints.event;

import java.util.UUID;

import org.bukkit.event.HandlerList;

/**
 * Called when a player's points is to be reset.
 */
public class PlayerPointsResetEvent extends PlayerPointsEvent {

    /**
     * Handler list.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * Constructor.
     * 
     * @param name
     *            - Name of player.
     */
    public PlayerPointsResetEvent(UUID id) {
        super(id, 0);
    }

    /**
     * Static method to get HandlerList.
     * 
     * @return HandlerList.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
