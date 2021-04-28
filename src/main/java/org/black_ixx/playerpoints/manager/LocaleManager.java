package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.locale.Locale;
import dev.rosewood.rosegarden.manager.AbstractLocaleManager;
import java.util.Arrays;
import java.util.List;
import org.black_ixx.playerpoints.locale.EnglishLocale;
import org.black_ixx.playerpoints.locale.FrenchLocale;

public class LocaleManager extends AbstractLocaleManager {

    public LocaleManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Locale> getLocales() {
        return Arrays.asList(
                new EnglishLocale(),
                new FrenchLocale()
        );
    }

    public String getCurrencyName(int value) {
        if (value == 1 || value == -1) {
            return this.getLocaleMessage("currency-singular");
        } else {
            return this.getLocaleMessage("currency-plural");
        }
    }

}
