package org.black_ixx.playerpoints;

import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.black_ixx.playerpoints.event.PlayerPointsResetEvent;
import org.black_ixx.playerpoints.storage.StorageHandler;

import java.util.UUID;

/**
 * API hook.
 */
public class PlayerPointsAPI {
    /**
     * Plugin instance.
     */
    private final PlayerPoints plugin;

    /**
     * Constructor
     *
     * @param p - Player points plugin.
     */
    public PlayerPointsAPI(PlayerPoints p) {
        this.plugin = p;
    }

    /**
     * Give points to specified player.
     *
     * @param playerId UUID of player
     * @param amount   of points to give
     * @return True if we successfully adjusted points, else false
     */
    public boolean give(UUID playerId, int amount) {
        if (playerId == null) {
            return false;
        }
        PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId, amount);
        plugin.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            final int total = look(playerId) + event.getChange();
            return plugin.getModuleForClass(StorageHandler.class).setPoints(playerId.toString(), total);
        }
        return false;
    }

    @Deprecated
    public boolean give(String playerName, int amount) {
        boolean success = false;
        if (playerName != null) {
            success = give(plugin.translateNameToUUID(playerName), amount);
        }
        return success;
    }

    /**
     * Take points from specified player. If the amount given is not already
     * negative, we make it negative.
     *
     * @param playerId UUID of player
     * @param amount   of points to give
     * @return True if we successfully adjusted points, else false
     */
    public boolean take(UUID playerId, int amount) {
        final int points = look(playerId);
        int take = amount;
        if (take > 0) {
            take *= -1;
        }
        if ((points + take) < 0) {
            return false;
        }
        return give(playerId, take);
    }

    @Deprecated
    public boolean take(String playerName, int amount) {
        boolean success = false;
        if (playerName != null) {
            success = take(plugin.translateNameToUUID(playerName), amount);
        }
        return success;
    }

    /**
     * Look up the current points of a player, if available. If player does not
     * exist in the config file, or if given a null UUID, then we default to 0.
     *
     * @param playerId Player UUID
     * @return Points that the player has
     */
    public int look(UUID playerId) {
        int amount = 0;
        if (playerId != null) {
            amount = plugin.getModuleForClass(StorageHandler.class).getPoints(playerId.toString());
        }
        return amount;
    }

    @Deprecated
    public int look(String playerName) {
        return look(plugin.translateNameToUUID(playerName));
    }

    /**
     * Transfer points from one player to another. Attempts to take the points,
     * if available, from the source account and then attempts to give same
     * amount to target account. If it takes, but does not give, then we return
     * the amount back to the source since it failed.
     *
     * @return True if we successfully adjusted points, else false
     */
    public boolean pay(UUID source, UUID target, int amount) {
        if (take(source, amount)) {
            if (give(target, amount)) {
                return true;
            } else {
                give(source, amount);
            }
        }
        return false;
    }

    @Deprecated
    public boolean pay(String sourceName, String targetName, int amount) {
        boolean success = false;
        if (sourceName != null && targetName != null) {
            success = pay(plugin.translateNameToUUID(sourceName), plugin.translateNameToUUID(targetName), amount);
        }
        return success;
    }

    /**
     * Sets a player's points to a given value.
     *
     * @param playerId UUID of player
     * @param amount   of points that it should be set to
     * @return True if successful
     */
    public boolean set(UUID playerId, int amount) {
        if (playerId == null) {
            return false;
        }
        PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId,
                amount - look(playerId));
        plugin.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            return plugin.getModuleForClass(StorageHandler.class).setPoints(
                    playerId.toString(),
                    look(playerId) + event.getChange());
        }
        return false;
    }

    @Deprecated
    public boolean set(String playerName, int amount) {
        boolean success = false;
        if (playerName != null) {
            success = set(plugin.translateNameToUUID(playerName), amount);
        }
        return success;
    }

    /**
     * Reset a player's points by removing their entry from the config.
     *
     * @param playerId Player UUID
     * @return True if successful
     */
    public boolean reset(UUID playerId) {
        if (playerId == null) {
            return false;
        }
        PlayerPointsResetEvent event = new PlayerPointsResetEvent(playerId);
        plugin.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            return plugin.getModuleForClass(StorageHandler.class).setPoints(
                    playerId.toString(), event.getChange());
        }
        return false;
    }

    @Deprecated
    public boolean reset(String playerName) {
        boolean success = false;
        if (playerName != null) {
            success = reset(plugin.translateNameToUUID(playerName));
        }
        return success;
    }
}
