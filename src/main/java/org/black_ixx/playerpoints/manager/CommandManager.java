package org.black_ixx.playerpoints.manager;

import com.google.common.collect.Lists;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.commands.Commander;
import org.bukkit.command.PluginCommand;

public class CommandManager extends Manager {

    public enum CommandAliases {
        ROOT {
            @Override
            public List<String> get() {
                return Collections.emptyList();
            }
        },
        BROADCAST,
        CONVERT,
        EXPORT,
        GIVEALL,
        GIVE,
        HELP,
        IMPORT,
        IMPORTLEGACY,
        LEAD,
        LOOK,
        ME,
        PAY,
        RELOAD,
        RESET,
        SET,
        TAKE,
        VERSION;

        private List<String> aliases;

        public List<String> get() {
            return this.aliases;
        }

        private void set(List<String> aliases) {
            this.aliases = aliases.stream().filter(x -> !x.trim().isEmpty()).collect(Collectors.toList());
        }
    }

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        File file = new File(this.rosePlugin.getDataFolder(), "aliases.yml");
        CommentedFileConfiguration fileConfiguration = CommentedFileConfiguration.loadConfiguration(file);

        boolean changes = false;
        for (CommandAliases value : CommandAliases.values()) {
            if (value == CommandAliases.ROOT)
                continue;

            String key = value.name().toLowerCase();
            if (fileConfiguration.contains(key)) {
                value.set(fileConfiguration.getStringList(key));
            } else {
                changes = true;
                fileConfiguration.set(key, Lists.newArrayList(key));
                value.set(Collections.singletonList(key));
            }
        }

        if (changes)
            fileConfiguration.save();

        // Register commands
        Commander commander = new Commander((PlayerPoints) this.rosePlugin);
        PluginCommand command = this.rosePlugin.getCommand("points");
        if (command != null)
            command.setExecutor(commander);
    }

    @Override
    public void disable() {

    }

}
