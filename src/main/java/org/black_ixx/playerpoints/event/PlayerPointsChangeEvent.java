package org.black_ixx.playerpoints.event;

import java.util.UUID;
import org.black_ixx.playerpoints.models.PendingTransaction;
import org.black_ixx.playerpoints.models.TransactionType;
import org.bukkit.event.HandlerList;

/**
 * Called when a player's points is to be changed.
 */
public class PlayerPointsChangeEvent extends PlayerPointsEvent {

    /**
     * Handler list.
     */
    private static final HandlerList handlers = new HandlerList();

    private final TransactionType transactionType;

    /**
     * Constructor.
     *
     * @param playerId Player UUID
     * @param change Amount of points to be changed.
     * @param transactionType The type of transaction
     */
    public PlayerPointsChangeEvent(UUID playerId, int change, TransactionType transactionType) {
        super(playerId, change);
        this.transactionType = transactionType;
    }

    /**
     * @return the transaction type for this event
     */
    public TransactionType getTransactionType() {
        return this.transactionType;
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
