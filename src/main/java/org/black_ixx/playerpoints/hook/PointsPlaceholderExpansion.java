package org.black_ixx.playerpoints.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LeaderboardManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class PointsPlaceholderExpansion extends PlaceholderExpansion {

    private final PlayerPoints playerPoints;
    private final DataManager dataManager;
    private final LeaderboardManager leaderboardManager;
    private final LocaleManager localeManager;

    public PointsPlaceholderExpansion(PlayerPoints playerPoints) {
        this.playerPoints = playerPoints;
        this.dataManager = playerPoints.getManager(DataManager.class);
        this.leaderboardManager = playerPoints.getManager(LeaderboardManager.class);
        this.localeManager = playerPoints.getManager(LocaleManager.class);
    }

    @Override
    public String onRequest(OfflinePlayer player, String placeholder) {
        if (player != null) {
            switch (placeholder.toLowerCase()) {
                case "points":
                    return String.valueOf(this.dataManager.getEffectivePoints(player.getUniqueId()));
                case "points_formatted":
                    return PointsUtils.formatPoints(this.dataManager.getEffectivePoints(player.getUniqueId()));
                case "points_shorthand":
                    return PointsUtils.formatPointsShorthand(this.dataManager.getEffectivePoints(player.getUniqueId()));
                case "leaderboard_position":
                    Long position = this.leaderboardManager.getPlayerLeaderboardPosition(player.getUniqueId());
                    return String.valueOf(position != null ? position : -1);
                case "leaderboard_position_formatted":
                    try {
                        Long position1 = this.leaderboardManager.getPlayerLeaderboardPosition(player.getUniqueId());
                        return PointsUtils.formatPoints(position1 != null ? position1 : -1);
                    } catch (Exception e) {
                        return null;
                    }
            }
        }

        if (placeholder.toLowerCase().startsWith("leaderboard_")) {
            try {
                String suffix = placeholder.substring("leaderboard_".length());
                int underscoreIndex = suffix.indexOf('_');
                int position;
                if (underscoreIndex != -1) {
                    String positionValue = suffix.substring(0, underscoreIndex);
                    suffix = suffix.substring(underscoreIndex + 1);
                    position = Integer.parseInt(positionValue);

                } else {
                    position = Integer.parseInt(suffix);
                    suffix = "";
                }

                List<SortedPlayer> leaderboard = this.leaderboardManager.getLeaderboard();
                if (position > leaderboard.size())
                    return this.localeManager.getLocaleMessage("leaderboard-empty-entry");

                SortedPlayer leader = leaderboard.get(position - 1);

                // Display the player's name
                if (suffix.isEmpty())
                    return leader.getUsername();

                switch (suffix.toLowerCase()) {
                    case "amount":
                        return String.valueOf(leader.getPoints());
                    case "amount_formatted":
                        return PointsUtils.formatPoints(leader.getPoints());
                    case "amount_shorthand":
                        return PointsUtils.formatPointsShorthand(leader.getPoints());
                }
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return this.playerPoints.getDescription().getName().toLowerCase();
    }

    @Override
    public String getAuthor() {
        return this.playerPoints.getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return this.playerPoints.getDescription().getVersion();
    }

}
