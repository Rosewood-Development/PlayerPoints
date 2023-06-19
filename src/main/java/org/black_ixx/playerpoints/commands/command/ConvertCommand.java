package org.black_ixx.playerpoints.commands.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.conversion.CurrencyPlugin;
import org.black_ixx.playerpoints.manager.ConversionManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.bukkit.Bukkit;

public class ConvertCommand extends RoseCommand {

    public ConvertCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, CurrencyPlugin plugin, boolean confirm) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        ConversionManager manager = this.rosePlugin.getManager(ConversionManager.class);

        if (!manager.getEnabledConverters().contains(plugin)) {
            locale.sendMessage(context.getSender(), "command-convert-invalid", StringPlaceholders.of("plugin", plugin.name()));
            return;
        }

        if (!confirm) {
            locale.sendMessage(context.getSender(), "command-convert-warning", StringPlaceholders.of("plugin", plugin.name()));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(PlayerPoints.getInstance(), () -> {
            if (manager.convert(plugin)) {
                locale.sendMessage(context.getSender(), "command-convert-success", StringPlaceholders.of("plugin", plugin.name()));
            } else {
                locale.sendMessage(context.getSender(), "command-convert-failure");
            }
        });
    }

    @Override
    protected String getDefaultName() {
        return "convert";
    }

    @Override
    public String getDescriptionKey() {
        return "command-convert-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.convert";
    }

}
