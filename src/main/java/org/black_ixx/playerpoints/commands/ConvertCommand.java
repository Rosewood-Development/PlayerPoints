package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.conversion.CurrencyPlugin;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.ConversionManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class ConvertCommand extends PointsCommand {

    public ConvertCommand() {
        super("convert", CommandManager.CommandAliases.CONVERT);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);
        if (args.length < 1) {
            localeManager.sendMessage(sender, "command-convert-usage");
            return;
        }

        CurrencyPlugin currencyPlugin = CurrencyPlugin.get(args[0]);
        ConversionManager conversionManager = plugin.getManager(ConversionManager.class);
        if (currencyPlugin == null || !conversionManager.getEnabledConverters().contains(currencyPlugin)) {
            localeManager.sendMessage(sender, "command-convert-invalid", StringPlaceholders.of("plugin", args[0]));
            return;
        }

        if (args.length != 2 || !args[1].equalsIgnoreCase("confirm")) {
            localeManager.sendMessage(sender, "command-convert-warning", StringPlaceholders.of("plugin", args[0]));
            return;
        }

        plugin.getScheduler().runTaskAsync(() -> {
            if (conversionManager.convert(currencyPlugin)) {
                localeManager.sendMessage(sender, "command-convert-success", StringPlaceholders.of("plugin", args[0]));
            } else {
                localeManager.sendMessage(sender, "command-convert-failure");
            }
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        if (args.length != 1)
            return Collections.emptyList();

        List<String> validPlugins = plugin.getManager(ConversionManager.class).getEnabledConverters().stream().map(Enum::name).collect(Collectors.toList());
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], validPlugins, completions);
        return completions;
    }

}
