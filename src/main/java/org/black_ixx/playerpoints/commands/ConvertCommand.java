package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.conversion.CurrencyPlugin;
import org.black_ixx.playerpoints.manager.ConversionManager;
import org.bukkit.command.CommandSender;

public class ConvertCommand extends BasePointsCommand {

    public ConvertCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context, CurrencyPlugin currencyPlugin, String confirm) {
        CommandSender sender = context.getSender();
        ConversionManager conversionManager = this.rosePlugin.getManager(ConversionManager.class);
        if (!conversionManager.getEnabledConverters().contains(currencyPlugin)) {
            this.localeManager.sendCommandMessage(sender, "command-convert-invalid", StringPlaceholders.of("plugin", currencyPlugin.name().toLowerCase()));
            return;
        }

        if (confirm == null) {
            this.localeManager.sendCommandMessage(sender, "command-convert-warning", StringPlaceholders.of("plugin", currencyPlugin.name().toLowerCase()));
            return;
        }

        this.rosePlugin.getScheduler().runTaskAsync(() -> {
            if (conversionManager.convert(currencyPlugin)) {
                this.localeManager.sendCommandMessage(sender, "command-convert-success", StringPlaceholders.of("plugin", currencyPlugin.name().toLowerCase()));
            } else {
                this.localeManager.sendCommandMessage(sender, "command-convert-failure");
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("convert")
                .descriptionKey("command-convert-description")
                .permission("playerpoints.convert")
                .arguments(ArgumentsDefinition.builder()
                        .required("plugin", ArgumentHandlers.forEnum(CurrencyPlugin.class))
                        .optional("confirm", ArgumentHandlers.forValues(String.class, "confirm"))
                        .build())
                .build();
    }

}
