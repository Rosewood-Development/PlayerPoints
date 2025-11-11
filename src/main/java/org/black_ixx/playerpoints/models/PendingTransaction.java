package org.black_ixx.playerpoints.models;

import java.util.UUID;

public class PendingTransaction {

    private final UpdateType updateType;
    private final TransactionType transactionType;
    private final String sourceDescription;
    private final UUID source;
    private final int amount;

    public PendingTransaction(UpdateType updateType, TransactionType transactionType, String sourceDescription, UUID source, int amount) {
        this.updateType = updateType;
        this.transactionType = transactionType;
        this.sourceDescription = sourceDescription;
        this.source = source;
        this.amount = amount;
    }

    public UpdateType getUpdateType() {
        return this.updateType;
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public String getSourceDescription() {
        return this.sourceDescription;
    }

    public UUID getSource() {
        return this.source;
    }

    public int getAmount() {
        return this.amount;
    }

}
