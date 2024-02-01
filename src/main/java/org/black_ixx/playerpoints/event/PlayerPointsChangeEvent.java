package org.black_ixx.playerpoints.event;

import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Called when a player's points is to be changed.
 */
public class PlayerPointsChangeEvent extends PlayerPointsEvent {

    /**
     * Handler list.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * Constructor.
     *
     * @param playerId Player UUID
     * @param change   Amount of points to be changed.
     */
    public PlayerPointsChangeEvent(UUID playerId, int change) {
        super(playerId, change);
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
