package org.black_ixx.playerpoints.models;

public enum TransactionType {

    /**
     * Used for /points give, giveall, and take
     */
    OFFSET,
    /**
     * Used for the /points pay sender
     */
    PAY_SENDER,
    /**
     * Used for the /points pay receiver
     */
    PAY_RECEIVER,
    /**
     * Used for /points set
     */
    SET

}
