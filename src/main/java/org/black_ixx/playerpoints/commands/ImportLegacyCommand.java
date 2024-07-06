package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.database.MySQLConnector;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.CommandManager;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ImportLegacyCommand extends PointsCommand {

    public ImportLegacyCommand() {
        super("importlegacy", CommandManager.CommandAliases.IMPORTLEGACY);
    }

    @Override
    public void execute(PlayerPoints plugin, CommandSender sender, String[] args) {
        plugin.getScheduler().runTaskAsync(() -> {
            LocaleManager localeManager = plugin.getManager(LocaleManager.class);
            if (!(plugin.getManager(DataManager.class).getDatabaseConnector() instanceof MySQLConnector)) {
                localeManager.sendMessage(sender, "command-importlegacy-only-mysql");
                return;
            }

            if (args.length < 1) {
                localeManager.sendMessage(sender, "command-importlegacy-usage");
                return;
            }

            if (plugin.getManager(DataManager.class).importLegacyTable(args[0])) {
                localeManager.sendMessage(sender, "command-importlegacy-success", StringPlaceholders.of("table", args[0]));
            } else {
                localeManager.sendMessage(sender, "command-importlegacy-failure", StringPlaceholders.of("table", args[0]));
            }
        });
    }

    @Override
    public List<String> tabComplete(PlayerPoints plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
