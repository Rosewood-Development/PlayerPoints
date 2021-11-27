package org.black_ixx.playerpoints.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.OfflinePlayer;

public class PointsPlaceholderExpansion extends PlaceholderExpansion {

    private final PlayerPoints playerPoints;
    private final DataManager dataManager;

    public PointsPlaceholderExpansion(PlayerPoints playerPoints) {
        this.playerPoints = playerPoints;
        this.dataManager = playerPoints.getManager(DataManager.class);
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
