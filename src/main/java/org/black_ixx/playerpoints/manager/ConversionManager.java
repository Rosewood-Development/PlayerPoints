package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.black_ixx.playerpoints.conversion.CurrencyConverter;
import org.black_ixx.playerpoints.conversion.CurrencyPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConversionManager extends Manager {

    private final Map<CurrencyPlugin, CurrencyConverter> converters;

    public ConversionManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.converters = new HashMap<>();
    }

    @Override
    public void reload() {
        for (CurrencyPlugin currencyPlugin : CurrencyPlugin.values())
            this.converters.put(currencyPlugin, currencyPlugin.getConverter());
    }

    @Override
    public void disable() {
        this.converters.clear();
    }

    public boolean convert(CurrencyPlugin currencyPlugin) {
        CurrencyConverter converter = this.converters.get(currencyPlugin);
        if (!converter.canConvert())
            return false;

        try {
            converter.convert();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Set<CurrencyPlugin> getEnabledConverters() {
        return this.converters.entrySet().stream()
                .filter(x -> x.getValue().canConvert())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

}
