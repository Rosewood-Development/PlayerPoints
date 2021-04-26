package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Manages a cache and buffer of previous point values for a player.
 * Used to prevent the placeholder from doing constant main thread operations to fetch the point values.
 */
public class PointsCacheManager extends Manager implements Listener {

    private final Map<UUID, Integer> pointsCache;

    public PointsCacheManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.pointsCache = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {
        this.pointsCache.clear();
    }

    /**
     * Gets the points value for a player.
     *
     * @param playerId The UUID of the player
     * @return The points value for a player or -1 if the value is not yet loaded
     */
    public int getPoints(UUID playerId) {
        if (this.pointsCache.containsKey(playerId))
            return this.pointsCache.get(playerId);

        int points = this.rosePlugin.getManager(DataManager.class).getPoints(playerId).join();
        this.pointsCache.put(playerId, points);
        return points;
    }

    /**
     * Updates the value in cache
     *
     * @param playerId The player UUID
     * @param amount The points amount
     */
    public void updatePoints(UUID playerId, int amount) {
        this.pointsCache.put(playerId, amount);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.pointsCache.remove(event.getPlayer().getUniqueId());
    }

}
