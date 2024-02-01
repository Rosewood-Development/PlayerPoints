package org.black_ixx.playerpoints.treasury;

import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomyFailureReason;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import me.lokka30.treasury.api.economy.transaction.EconomyTransaction;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.util.NameFetcher;

import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PlayerPointsAccount implements PlayerAccount {

    private final PlayerPoints plugin;
    private final UUID uuid;

    public PlayerPointsAccount(PlayerPoints plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public Optional<String> getName() {
        return Optional.of(NameFetcher.getName(this.uuid));
    }

    @Override
    public void retrieveBalance(Currency currency, EconomySubscriber<BigDecimal> subscription) {
        Objects.requireNonNull(currency);
        Objects.requireNonNull(subscription);

        if (!currency.isPrimary()) {
            subscription.fail(new EconomyException(EconomyFailureReason.CURRENCY_NOT_FOUND, "Currency is not supported"));
            return;
        }

        subscription.succeed(new BigDecimal(this.plugin.getAPI().look(this.uuid)));
    }

    @Override
    public void setBalance(BigDecimal amount, EconomyTransactionInitiator<?> initiator, Currency currency, EconomySubscriber<BigDecimal> subscription) {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(initiator);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(subscription);

        if (!currency.isPrimary()) {
            subscription.fail(new EconomyException(EconomyFailureReason.CURRENCY_NOT_FOUND, "Currency is not supported"));
            return;
        }

        this.plugin.getAPI().set(this.uuid, amount.intValue());
        subscription.succeed(new BigDecimal(this.plugin.getAPI().look(this.uuid)));
    }

    @Override
    public void doTransaction(EconomyTransaction economyTransaction, EconomySubscriber<BigDecimal> subscription) {
        Objects.requireNonNull(economyTransaction);
        Objects.requireNonNull(subscription);

        switch (economyTransaction.getTransactionType()) {
            case DEPOSIT:
                this.plugin.getAPI().give(this.uuid, economyTransaction.getTransactionAmount().intValue());
                subscription.succeed(new BigDecimal(this.plugin.getAPI().look(this.uuid)));
                break;

            case WITHDRAWAL:
                this.plugin.getAPI().take(this.uuid, economyTransaction.getTransactionAmount().intValue());
                subscription.succeed(new BigDecimal(this.plugin.getAPI().look(this.uuid)));
                break;

            default:
                subscription.fail(new EconomyException(EconomyFailureReason.FEATURE_NOT_SUPPORTED, "Transaction type not supported"));
                break;
        }
    }

    @Override
    public void deleteAccount(EconomySubscriber<Boolean> subscription) {
        Objects.requireNonNull(subscription);

        subscription.succeed(this.plugin.getAPI().reset(this.uuid));
    }

    @Override
    public void retrieveHeldCurrencies(EconomySubscriber<Collection<String>> subscription) {
        Objects.requireNonNull(subscription);

        subscription.fail(new EconomyException(EconomyFailureReason.FEATURE_NOT_SUPPORTED, "Only a primary currency is supported"));
    }

    @Override
    public void retrieveTransactionHistory(int transactionCount, Temporal from, Temporal to, EconomySubscriber<Collection<EconomyTransaction>> subscription) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(subscription);

        subscription.fail(new EconomyException(EconomyFailureReason.FEATURE_NOT_SUPPORTED, "Transaction history is not supported"));
    }

}
