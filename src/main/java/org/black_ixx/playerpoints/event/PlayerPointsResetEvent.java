package org.black_ixx.playerpoints.event;

import org.bukkit.event.HandlerList;

import java.util.UUID;

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
     * @param id - UUID of the player.
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
