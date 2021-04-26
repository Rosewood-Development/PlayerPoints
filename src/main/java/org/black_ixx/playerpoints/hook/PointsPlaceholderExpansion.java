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
