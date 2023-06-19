package org.black_ixx.playerpoints.commands.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.manager.ConfigurationManager.Setting;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeadCommand extends RoseCommand {

    private final Map<String, Integer> pageMap = new HashMap<>();

    public LeadCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, @Optional Integer page) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        int limit = Setting.LEADERBOARD_PER_PAGE.getInt();

        if (page != null) {
            this.pageMap.put(context.getSender().getName(), page - 1);
        }

        Bukkit.getScheduler().runTaskAsynchronously(this.rosePlugin, () -> {
            List<SortedPlayer> leaders = this.rosePlugin.getManager(DataManager.class).getTopSortedPoints(null);
            int currentPage = this.pageMap.getOrDefault(context.getSender().getName(), 0);
            int numPages = (int) Math.ceil(leaders.size() / (double) limit);

            if (currentPage < 0) {
                currentPage = 0;
                this.pageMap.put(context.getSender().getName(), currentPage);
            } else if (currentPage >= numPages) {
                currentPage = numPages - 1;
                this.pageMap.put(context.getSender().getName(), currentPage);
            }

            List<SortedPlayer> listedPlayers = leaders.stream()
                    .skip((long) currentPage * limit)
                    .limit(limit)
                    .collect(Collectors.toList());

            if (leaders.isEmpty()) {
                currentPage = 0;
                numPages = 0;
            }

            locale.sendMessage(context.getSender(), "command-lead-title", StringPlaceholders.of("page", currentPage + 1, "pages", numPages));

            for (int i = 0; i < listedPlayers.size(); i++) {
                int position = currentPage * limit + i + 1;
                SortedPlayer sortedPlayer = listedPlayers.get(i);

                locale.sendMessage(context.getSender(), "command-lead-entry", StringPlaceholders.builder("position", position)
                        .add("player", sortedPlayer.getUsername())
                        .add("amount", sortedPlayer.getPoints())
                        .add("currency", locale.getCurrencyName(sortedPlayer.getPoints()))
                        .build());
            }
        });
    }

    @Override
    protected String getDefaultName() {
        return "lead";
    }

    @Override
    public String getDescriptionKey() {
        return "command-lead-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.lead";
    }

}
