package org.black_ixx.playerpoints.hook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LeaderboardManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PointsPlaceholderExpansion extends PlaceholderExpansion {

    private final PlayerPoints playerPoints;
    private final DataManager dataManager;
    private final LeaderboardManager leaderboardManager;
    private final LocaleManager localeManager;
    private final Map<UUID, String> nameLookupCache;

    public PointsPlaceholderExpansion(PlayerPoints playerPoints) {
        this.playerPoints = playerPoints;
        this.dataManager = playerPoints.getManager(DataManager.class);
        this.leaderboardManager = playerPoints.getManager(LeaderboardManager.class);
        this.localeManager = playerPoints.getManager(LocaleManager.class);
        this.nameLookupCache = new HashMap<>();
    }

    @Override
    public String onRequest(OfflinePlayer player, String placeholder) {
        if (player == null)
            return null;

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

        if (placeholder.toLowerCase().startsWith("leaderboard_")) {
            try {
                int topPosition = Integer.parseInt(placeholder.substring("leaderboard_".length()));
                List<SortedPlayer> leaderboard = this.leaderboardManager.getLeaderboard();
                if (topPosition <= leaderboard.size()) {
                    SortedPlayer leader = leaderboard.get(topPosition - 1);
                    if (this.nameLookupCache.containsKey(leader.getUniqueId())) {
                        return this.nameLookupCache.get(leader.getUniqueId());
                    } else {
                        String name = Bukkit.getOfflinePlayer(leader.getUniqueId()).getName();
                        this.nameLookupCache.put(leader.getUniqueId(), name = (name != null ? name : "Unknown"));
                        return name;
                    }
                } else {
                    return this.localeManager.getLocaleMessage("leaderboard-empty-entry");
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
