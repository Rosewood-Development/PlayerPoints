package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.database.MySQLConnector;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.bukkit.command.CommandSender;

public class ImportLegacyCommand extends BasePointsCommand {

    public ImportLegacyCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context, String tableName) {
        CommandSender sender = context.getSender();
        DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
        if (!(dataManager.getDatabaseConnector() instanceof MySQLConnector)) {
            this.localeManager.sendCommandMessage(sender, "command-importlegacy-only-mysql");
            return;
        }

        String safeTable = tableName.replaceAll("[^A-Za-z0-9_]", "");
        this.rosePlugin.getScheduler().runTaskAsync(() -> {
            if (dataManager.importLegacyTable(safeTable)) {
                this.localeManager.sendCommandMessage(sender, "command-importlegacy-success", StringPlaceholders.of("table", safeTable));
            } else {
                this.localeManager.sendCommandMessage(sender, "command-importlegacy-failure", StringPlaceholders.of("table", safeTable));
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("importlegacy")
                .descriptionKey("command-importlegacy-description")
                .permission("playerpoints.importlegacy")
                .arguments(ArgumentsDefinition.builder()
                        .required("tableName", ArgumentHandlers.STRING)
                        .build())
                .build();
    }

}
