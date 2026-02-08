package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.commands.arguments.StringSuggestingArgumentHandler;
import org.black_ixx.playerpoints.config.SettingKey;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.command.CommandSender;

public class LeadCommand extends BasePointsCommand {

    /**
     * Current page the player is viewing
     */
    private final Map<String, Integer> pageMap = new HashMap<>();

    public LeadCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    @RoseExecutable
    public void execute(CommandContext context, String pageArg) {
        CommandSender sender = context.getSender();
        String name = sender.getName();
        int current = this.pageMap.getOrDefault(name, 0);

        if (pageArg == null) {
            this.pageMap.put(name, 0);
            this.send(context);
            return;
        } else if (pageArg.equalsIgnoreCase("prev")) {
            this.pageMap.put(name, current - 1);
            this.send(context);
            return;
        } else if (pageArg.equalsIgnoreCase("next")) {
            this.pageMap.put(name, current + 1);
            this.send(context);
            return;
        } else {
            try {
                current = Integer.parseInt(pageArg);
                this.pageMap.put(name, current - 1);
                this.send(context);
                return;
            } catch (NumberFormatException e) {
                // Handle notification later
            }
        }

        // Handle invalid input
        this.localeManager.sendCommandMessage(sender, "command-lead-usage");
    }

    private void send(CommandContext context) {
        this.rosePlugin.getScheduler().runTaskAsync(() -> {
            DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
            int leaderboardSize = dataManager.getLeaderboardSize();

            CommandSender sender = context.getSender();
            int limit = SettingKey.LEADERBOARD_PER_PAGE.get();
            int currentPage = this.pageMap.getOrDefault(sender.getName(), 0);
            int numPages = (int) Math.ceil(leaderboardSize / (double) limit);

            // Bounds check
            if (currentPage < 0) {
                currentPage = 0;
                this.pageMap.put(sender.getName(), currentPage);
            } else if (currentPage >= numPages) {
                currentPage = numPages - 1;
                this.pageMap.put(sender.getName(), currentPage);
            }

            List<SortedPlayer> leaders = dataManager.getTopSortedPoints(limit, currentPage * limit);
            if (leaders.isEmpty()) {
                currentPage = 0;
                numPages = 0;
            }

            this.localeManager.sendCommandMessage(sender, "command-lead-title", StringPlaceholders.builder("page", currentPage + 1)
                    .add("pages", numPages).build());

            // Page through
            for (int i = 0; i < leaders.size(); i++) {
                int position = currentPage * limit + i + 1;
                SortedPlayer player = leaders.get(i);

                this.localeManager.sendSimpleCommandMessage(sender, "command-lead-entry", StringPlaceholders.builder("position", position)
                        .add("player", player.getUsername())
                        .add("amount", PointsUtils.formatPoints(player.getPoints()))
                        .add("currency", this.localeManager.getCurrencyName(player.getPoints()))
                        .build());
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("lead")
                .descriptionKey("command-lead-description")
                .permission("playerpoints.lead")
                .arguments(ArgumentsDefinition.builder()
                        .optional("page", new StringSuggestingArgumentHandler("prev", "next", "1"))
                        .build())
                .build();
    }

}
