package org.black_ixx.playerpoints.commands.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.database.MySQLConnector;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;

import java.io.File;

public class ImportLegacyCommand extends RoseCommand {

    public ImportLegacyCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, String table) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        if (!(this.rosePlugin.getManager(DataManager.class).getDatabaseConnector() instanceof MySQLConnector)) {
            locale.sendMessage(context.getSender(), "command-importlegacy-only-mysql");
            return;
        }


        File file = new File(this.rosePlugin.getDataFolder(), "storage.yml");
        if (!file.exists()) {
            locale.sendMessage(context.getSender(), "command-import-no-backup");
            return;
        }

        if (this.rosePlugin.getManager(DataManager.class).importLegacyTable(table)) {
            locale.sendMessage(context.getSender(), "command-importlegacy-success", StringPlaceholders.of("table", table));
        } else {
            locale.sendMessage(context.getSender(), "command-importlegacy-failure", StringPlaceholders.of("table", table));
        }
    }

    @Override
    protected String getDefaultName() {
        return "importlegacy";
    }

    @Override
    public String getDescriptionKey() {
        return "command-importlegacy-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.importlegacy";
    }
}
