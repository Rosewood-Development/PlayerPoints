package org.black_ixx.playerpoints.models;

public class PendingTransaction {

    private final TransactionType type;
    private final int amount;

    public PendingTransaction(TransactionType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public TransactionType getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    public enum TransactionType {
        OFFSET,
        SET
    }

}
