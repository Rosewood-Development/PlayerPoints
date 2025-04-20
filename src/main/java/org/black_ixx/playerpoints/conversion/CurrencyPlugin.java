package org.black_ixx.playerpoints.conversion;

import java.util.function.Function;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.conversion.converter.EcoBitsConverter;
import org.black_ixx.playerpoints.conversion.converter.GamePointsConverter;
import org.black_ixx.playerpoints.conversion.converter.TokenManagerConverter;
import org.bukkit.Bukkit;

public enum CurrencyPlugin {

    TOKENMANAGER("TokenManager", TokenManagerConverter::new, false),
    GAMEPOINTS("GamePoints", GamePointsConverter::new, false),
    ECOBITS("EcoBits", EcoBitsConverter::new, true);

    private final String plugin;
    private final Function<PlayerPoints, ? extends CurrencyConverter> converterConstructor;
    private final boolean multipleCurrencies;

    CurrencyPlugin(String plugin, Function<PlayerPoints, ? extends CurrencyConverter> converterConstructor, boolean multipleCurrencies) {
        this.plugin = plugin;
        this.converterConstructor = converterConstructor;
        this.multipleCurrencies = multipleCurrencies;
    }

    public boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled(this.plugin);
    }

    public CurrencyConverter getConverter() {
        try {
            return this.converterConstructor.apply(PlayerPoints.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasMultipleCurrencies() {
        return this.multipleCurrencies;
    }

    public static CurrencyPlugin get(String name) {
        for (CurrencyPlugin currencyPlugin : values())
            if (currencyPlugin.name().equalsIgnoreCase(name))
                return currencyPlugin;
        return null;
    }

}
