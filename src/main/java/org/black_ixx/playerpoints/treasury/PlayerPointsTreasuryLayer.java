package org.black_ixx.playerpoints.treasury;

import me.lokka30.treasury.api.economy.EconomyProvider;
import me.lokka30.treasury.api.economy.account.Account;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.misc.OptionalEconomyApiFeature;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomyFailureReason;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.models.SortedPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerPointsTreasuryLayer implements EconomyProvider {

    private final PlayerPoints plugin;
    private final Currency currency;

    public PlayerPointsTreasuryLayer(PlayerPoints plugin) {
        this.plugin = plugin;
        this.currency = new PointsCurrency(plugin);
    }

    @Override
    public Set<OptionalEconomyApiFeature> getSupportedOptionalEconomyApiFeatures() {
        return Collections.emptySet();
    }

    @Override
    public void hasPlayerAccount(UUID accountId, EconomySubscriber<Boolean> subscription) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(subscription);

        subscription.succeed(true);
    }

    @Override
    public void retrievePlayerAccount(UUID accountId, EconomySubscriber<PlayerAccount> subscription) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(subscription);

        PlayerPointsAccount account = new PlayerPointsAccount(this.plugin, accountId);
        subscription.succeed(account);
    }

    @Override
    public void createPlayerAccount(UUID accountId, EconomySubscriber<PlayerAccount> subscription) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(subscription);

        PlayerPointsAccount account = new PlayerPointsAccount(this.plugin, accountId);
        subscription.succeed(account);
    }

    @Override
    public void retrievePlayerAccountIds(EconomySubscriber<Collection<UUID>> subscription) {
        Objects.requireNonNull(subscription);

        List<UUID> accountIds = this.plugin.getAPI().getTopSortedPoints().stream()
                .map(SortedPlayer::getUniqueId)
                .collect(Collectors.toList());
        subscription.succeed(accountIds);
    }

    @Override
    public void hasAccount(String identifier, EconomySubscriber<Boolean> subscription) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(subscription);

        try {
            UUID.fromString(identifier);
            subscription.succeed(true);
        } catch (IllegalArgumentException e) {
            subscription.succeed(false);
        }
    }

    @Override
    public void retrieveAccount(String identifier, EconomySubscriber<Account> subscription) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(subscription);

        subscription.fail(new EconomyException(EconomyFailureReason.ACCOUNT_NOT_FOUND, "Non-player accounts are not supported"));
    }

    @Override
    public void createAccount(String name, String identifier, EconomySubscriber<Account> subscription) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(subscription);

        subscription.fail(new EconomyException(EconomyFailureReason.ACCOUNT_NOT_FOUND, "Non-player accounts are not supported"));
    }

    @Override
    public void retrieveAccountIds(EconomySubscriber<Collection<String>> subscription) {
        Objects.requireNonNull(subscription);

        subscription.fail(new EconomyException(EconomyFailureReason.ACCOUNT_NOT_FOUND, "Non-player accounts are not supported"));
    }

    @Override
    public void retrieveNonPlayerAccountIds(EconomySubscriber<Collection<String>> subscription) {
        Objects.requireNonNull(subscription);

        subscription.fail(new EconomyException(EconomyFailureReason.FEATURE_NOT_SUPPORTED, "Non-player accounts are not supported"));
    }

    @Override
    public Currency getPrimaryCurrency() {
        return this.currency;
    }

    @Override
    public Optional<Currency> findCurrency(String identifier) {
        Objects.requireNonNull(identifier);

        if (identifier.equalsIgnoreCase(this.currency.getIdentifier()))
            return Optional.of(this.currency);
        return Optional.empty();
    }

    @Override
    public Set<Currency> getCurrencies() {
        return Collections.singleton(this.currency);
    }

    @Override
    public void registerCurrency(Currency currency, EconomySubscriber<Boolean> subscription) {
        Objects.requireNonNull(currency);
        Objects.requireNonNull(subscription);

        subscription.fail(new EconomyException(EconomyFailureReason.FEATURE_NOT_SUPPORTED, "Currency registration is not supported"));
    }

}
