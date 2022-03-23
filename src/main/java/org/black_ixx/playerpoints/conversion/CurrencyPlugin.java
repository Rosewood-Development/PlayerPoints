package org.black_ixx.playerpoints.conversion;

import dev.rosewood.rosegarden.RosePlugin;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.conversion.converter.GamePointsConverter;
import org.black_ixx.playerpoints.conversion.converter.TokenManagerConverter;

public enum CurrencyPlugin {

    TokenManager(TokenManagerConverter.class),
    GamePoints(GamePointsConverter.class);

    private final Class<? extends CurrencyConverter> converterClass;

    CurrencyPlugin(Class<? extends CurrencyConverter> converterClass) {
        this.converterClass = converterClass;
    }

    public CurrencyConverter getConverter() {
        try {
            return this.converterClass.getConstructor(RosePlugin.class).newInstance(PlayerPoints.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CurrencyPlugin get(String name) {
        for (CurrencyPlugin currencyPlugin : values())
            if (currencyPlugin.name().equalsIgnoreCase(name))
                return currencyPlugin;
        return null;
    }

}
