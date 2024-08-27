package org.black_ixx.playerpoints.treasury;

import dev.rosewood.rosegarden.RosePlugin;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomyFailureReason;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.setting.SettingKey;
import org.black_ixx.playerpoints.util.PointsUtils;

public class PointsCurrency implements Currency {

    private final LocaleManager localeManager;

    public PointsCurrency(RosePlugin rosePlugin) {
        this.localeManager = rosePlugin.getManager(LocaleManager.class);
    }

    @Override
    public String getIdentifier() {
        return "Points";
    }

    @Override
    public String getSymbol() {
        return "";
    }

    @Override
    public char getDecimal() {
        return PointsUtils.getDecimalSeparator();
    }

    @Override
    public String getDisplayNameSingular() {
        return this.localeManager.getLocaleMessage("currency-singular");
    }

    @Override
    public String getDisplayNamePlural() {
        return this.localeManager.getLocaleMessage("currency-plural");
    }

    @Override
    public int getPrecision() {
        return 0;
    }

    @Override
    public boolean isPrimary() {
        return true;
    }

    @Override
    public void to(Currency currency, BigDecimal amount, EconomySubscriber<BigDecimal> subscription) {
        Objects.requireNonNull(currency);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(subscription);

        subscription.fail(new EconomyException(EconomyFailureReason.FEATURE_NOT_SUPPORTED, "Currency conversion not supported"));
    }

    @Override
    public void parse(String formatted, EconomySubscriber<BigDecimal> subscription) {
        Objects.requireNonNull(formatted);
        Objects.requireNonNull(subscription);

        try {
            subscription.succeed(new BigDecimal(Integer.parseInt(formatted)));
        } catch (NumberFormatException e) {
            subscription.fail(new EconomyException(EconomyFailureReason.NUMBER_PARSING_ERROR));
        }
    }

    @Override
    public BigDecimal getStartingBalance(UUID playerID) {
        return new BigDecimal(SettingKey.STARTING_BALANCE.get());
    }

    @Override
    public String format(BigDecimal amount, Locale locale) {
        Objects.requireNonNull(amount);

        return PointsUtils.formatPoints(amount.longValue());
    }

    @Override
    public String format(BigDecimal amount, Locale locale, int precision) {
        return this.format(amount, locale);
    }

}
