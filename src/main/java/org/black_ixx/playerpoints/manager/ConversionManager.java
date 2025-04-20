package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.conversion.CurrencyConverter;
import org.black_ixx.playerpoints.conversion.CurrencyPlugin;

public class ConversionManager extends Manager {

    private final Map<CurrencyPlugin, CurrencyConverter> converters;

    public ConversionManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.converters = new HashMap<>();
    }

    @Override
    public void reload() {
        this.rosePlugin.getScheduler().runTask(() -> {
            for (CurrencyPlugin currencyPlugin : CurrencyPlugin.values())
                if (currencyPlugin.isAvailable())
                    this.converters.put(currencyPlugin, currencyPlugin.getConverter());
        });
    }

    @Override
    public void disable() {
        this.converters.clear();
    }

    public boolean canConvert(CurrencyPlugin currencyPlugin, String currencyId) {
        CurrencyConverter converter = this.converters.get(currencyPlugin);
        return converter.isAvailable(currencyId);
    }

    public boolean convert(CurrencyPlugin currencyPlugin, String currencyId) {
        CurrencyConverter converter = this.converters.get(currencyPlugin);
        try {
            converter.convert(currencyId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Set<CurrencyPlugin> getEnabledConverters() {
        return this.converters.entrySet().stream()
                .filter(x -> x.getValue().isAvailable())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

}
