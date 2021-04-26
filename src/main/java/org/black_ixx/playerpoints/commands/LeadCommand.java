package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.ConfigurationManager.Setting;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

/**
 * Handles the leader board commands.
 *
 * @author Mitsugaru
 */
public class LeadCommand extends CommandHandler {

    /**
     * Current page the player is viewing.
     */
    private final Map<String, Integer> pageMap = new HashMap<>();

    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public LeadCommand(PlayerPoints plugin) {
        super(plugin, "lead");
    }

    @Override
    public void noArgs(CommandSender sender) {
        LocaleManager localeManager = this.plugin.getManager(LocaleManager.class);
        int limit = Setting.LEADERBOARD_PER_PAGE.getInt();

        this.plugin.getManager(DataManager.class).getAllPoints().thenAccept(leaders -> {
            int currentPage = this.pageMap.getOrDefault(sender.getName(), 0);
            int numPages = (int) Math.ceil(leaders.size() / (double) limit);

            // Bounds check
            if (currentPage < 0) {
                currentPage = 0;
                this.pageMap.put(sender.getName(), currentPage);
            } else if (currentPage >= numPages) {
                currentPage = numPages - 1;
                this.pageMap.put(sender.getName(), currentPage);
            }

            List<SortedPlayer> listedPlayers = leaders.stream()
                    .skip((long) currentPage * limit)
                    .limit(limit)
                    .collect(Collectors.toList());

            if (leaders.isEmpty()) {
                currentPage = 0;
                numPages = 0;
            }

            localeManager.sendMessage(sender, "command-lead-title", StringPlaceholders.builder("page", currentPage + 1)
                    .addPlaceholder("pages", numPages).build());

            // Page through
            for (int i = 0; i < listedPlayers.size(); i++) {
                int position = currentPage * limit + i + 1;
                SortedPlayer player = listedPlayers.get(i);

                localeManager.sendSimpleMessage(sender, "command-lead-entry", StringPlaceholders.builder("position", position)
                        .addPlaceholder("player", Bukkit.getOfflinePlayer(player.getUniqueId()).getName())
                        .addPlaceholder("amount", PointsUtils.formatPoints(player.getPoints()))
                        .addPlaceholder("currency", localeManager.getCurrencyName(player.getPoints()))
                        .build());
            }
        });
    }

    @Override
    public void unknownCommand(CommandSender sender, String[] args) {
        String pageArg = args[0];

        int current = this.pageMap.getOrDefault(sender.getName(), 0);

        if (pageArg.equalsIgnoreCase("prev")) {
            this.pageMap.put(sender.getName(), current - 1);
            this.noArgs(sender);
            return;
        } else if (pageArg.equals("next")) {
            this.pageMap.put(sender.getName(), current + 1);
            this.noArgs(sender);
            return;
        } else {
            try {
                current = Integer.parseInt(pageArg);
                this.pageMap.put(sender.getName(), current - 1);
                this.noArgs(sender);
                return;
            } catch (NumberFormatException e) {
                // Handle notification later
            }
        }

        // Handle invalid input
        this.plugin.getManager(LocaleManager.class).sendMessage(sender, "command-give-usage");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? Arrays.asList("next", "prev", "1") : Collections.emptyList();
    }

    @Override
    public boolean hasPermission(Permissible permissible) {
        return permissible.hasPermission("playerpoints.lead");
    }

}
