package org.black_ixx.playerpoints.commands.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.conversion.CurrencyPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyPluginArgumentHandler extends RoseCommandArgumentHandler<CurrencyPlugin> {

    public CurrencyPluginArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, CurrencyPlugin.class);
    }

    @Override
    protected CurrencyPlugin handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();
        CurrencyPlugin value = CurrencyPlugin.get(input);
        if (value == null)
            throw new HandledArgumentException("argument-handler-currency-plugin", StringPlaceholders.of("input", input));

        return value;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        return Arrays.stream(CurrencyPlugin.values()).map(CurrencyPlugin::name).collect(Collectors.toList());
    }

}
