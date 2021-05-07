package org.black_ixx.playerpoints.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.PointsCacheManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.OfflinePlayer;

public class PointsPlaceholderExpansion extends PlaceholderExpansion {

    private final PlayerPoints playerPoints;
    private final PointsCacheManager pointsCacheManager;

    public PointsPlaceholderExpansion(PlayerPoints playerPoints) {
        this.playerPoints = playerPoints;
        this.pointsCacheManager = playerPoints.getManager(PointsCacheManager.class);
    }

    @Override
    public String onRequest(OfflinePlayer player, String placeholder) {
        if (player == null)
            return null;

        if (placeholder.equalsIgnoreCase("points")) {
            return PointsUtils.formatPoints(this.pointsCacheManager.getPoints(player.getUniqueId()));
        }

        if (placeholder.startsWith("player_")) {
            return getPlayer(placeholder);
        }

        return null;
    }
    private String getPlayer(String placeholder) {
        String playerr = placeholder.split("player_")[1];
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerr);
        return PointsUtils.formatPoints(this.pointsCacheManager.getPoints(player.getUniqueId()));
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
