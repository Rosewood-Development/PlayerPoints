package org.black_ixx.playerpoints;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.black_ixx.playerpoints.event.PlayerPointsResetEvent;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.PointsCacheManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * The API for the PlayerPoints plugin.
 * Used to manipulate a player's points balance.
 * <p>
 * Note: This API does not send any messages and changes will be saved to the database automatically.
 */
public class PlayerPointsAPI {

    private final PlayerPoints plugin;

    PlayerPointsAPI(PlayerPoints plugin) {
        this.plugin = plugin;
    }

    /**
     * Give points to specified player.
     *
     * @param playerId UUID of player
     * @param amount   of points to give
     * @return True if we successfully adjusted points, else false
     */
    @NotNull
    public CompletableFuture<Boolean> giveAsync(@NotNull UUID playerId, int amount) {
        Objects.requireNonNull(playerId);

        PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId, amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return CompletableFuture.completedFuture(false);

        return this.lookAsync(playerId).thenCompose(points -> {
            int total = points + event.getChange();
            return this.plugin.getManager(DataManager.class).setPoints(playerId, total);
        });
    }

    /**
     * Take points from specified player. If the amount given is not already
     * negative, we make it negative.
     *
     * @param playerId UUID of player
     * @param amount   of points to give
     * @return True if we successfully adjusted points, else false
     */
    @NotNull
    public CompletableFuture<Boolean> takeAsync(@NotNull UUID playerId, int amount) {
        return this.lookAsync(playerId).thenCompose(points -> {
            int take = amount;
            if (take > 0)
                take *= -1;

            if (points + take < 0)
                return CompletableFuture.completedFuture(false);

            return this.giveAsync(playerId, take);
        });
    }

    /**
     * Look up the current points of a player, if available. If player does not
     * exist in the config file, or if given a null UUID, then we default to 0.
     *
     * @param playerId Player UUID
     * @return Points that the player has
     */
    @NotNull
    public CompletableFuture<Integer> lookAsync(@NotNull UUID playerId) {
        Objects.requireNonNull(playerId);

        return this.plugin.getManager(DataManager.class).getPoints(playerId);
    }

    /**
     * Transfer points from one player to another. Attempts to take the points,
     * if available, from the source account and then attempts to give same
     * amount to target account. If it takes, but does not give, then we return
     * the amount back to the source since it failed.
     *
     * @return True if we successfully adjusted points, else false
     */
    @NotNull
    public CompletableFuture<Boolean> payAsync(@NotNull UUID sourceId, @NotNull UUID targetId, int amount) {
        Objects.requireNonNull(sourceId);
        Objects.requireNonNull(targetId);

        return this.takeAsync(sourceId, amount).thenCompose(takeResult -> {
            if (takeResult) {
                return this.giveAsync(targetId, amount).thenCompose(giveResult -> {
                    if (giveResult) {
                        return CompletableFuture.completedFuture(true);
                    } else {
                        return this.giveAsync(sourceId, amount);
                    }
                });
            } else {
                return CompletableFuture.completedFuture(false);
            }
        });
    }

    /**
     * Sets a player's points to a given value.
     *
     * @param playerId UUID of player
     * @param amount   of points that it should be set to
     * @return True if successful
     */
    @NotNull
    public CompletableFuture<Boolean> setAsync(@NotNull UUID playerId, int amount) {
        return this.lookAsync(playerId).thenCompose(balance -> {
            PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId, amount - balance);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                return this.plugin.getManager(DataManager.class).setPoints(playerId, balance + event.getChange());
            } else {
                return CompletableFuture.completedFuture(false);
            }
        });
    }

    /**
     * Reset a player's points by removing their entry from the config.
     *
     * @param playerId Player UUID
     * @return True if successful
     */
    @NotNull
    public CompletableFuture<Boolean> resetAsync(@NotNull UUID playerId) {
        Objects.requireNonNull(playerId);

        PlayerPointsResetEvent event = new PlayerPointsResetEvent(playerId);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            return this.plugin.getManager(DataManager.class).setPoints(playerId, event.getChange());
        } else {
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Give points to specified player.
     *
     * @param playerId UUID of player
     * @param amount   of points to give
     * @return True if we successfully adjusted points, else false
     * @deprecated Use {@link #giveAsync(UUID, int)} as this will run on the main thread
     */
    @Deprecated
    public boolean give(@NotNull UUID playerId, int amount) {
        return this.giveAsync(playerId, amount).join();
    }

    /**
     * Take points from specified player. If the amount given is not already
     * negative, we make it negative.
     *
     * @param playerId UUID of player
     * @param amount   of points to give
     * @return True if we successfully adjusted points, else false
     * @deprecated Use {@link #takeAsync(UUID, int)} as this will run on the main thread
     */
    @Deprecated
    public boolean take(@NotNull UUID playerId, int amount) {
        return this.takeAsync(playerId, amount).join();
    }

    /**
     * Look up the current points of a player, if available. If player does not
     * exist in the config file, or if given a null UUID, then we default to 0.
     *
     * @param playerId Player UUID
     * @return Points that the player has
     * @deprecated Use {@link #lookAsync(UUID)} as this will use a cached value
     */
    @Deprecated
    public int look(@NotNull UUID playerId) {
        return this.plugin.getManager(PointsCacheManager.class).getPoints(playerId);
    }

    /**
     * Transfer points from one player to another. Attempts to take the points,
     * if available, from the source account and then attempts to give same
     * amount to target account. If it takes, but does not give, then we return
     * the amount back to the source since it failed.
     *
     * @return True if we successfully adjusted points, else false
     * @deprecated Use {@link #payAsync(UUID, UUID, int)} as this will run on the main thread
     */
    @Deprecated
    public boolean pay(@NotNull UUID source, @NotNull UUID target, int amount) {
        return this.payAsync(source, target, amount).join();
    }

    /**
     * Sets a player's points to a given value.
     *
     * @param playerId UUID of player
     * @param amount   of points that it should be set to
     * @return True if successful
     * @deprecated Use {@link #setAsync(UUID, int)} as this will run on the main thread
     */
    @Deprecated
    public boolean set(@NotNull UUID playerId, int amount) {
        return this.setAsync(playerId, amount).join();
    }

    /**
     * Reset a player's points by removing their entry from the config.
     *
     * @param playerId Player UUID
     * @return True if successful
     * @deprecated Use {@link #resetAsync(UUID)} as this will run on the main thread
     */
    @Deprecated
    public boolean reset(@NotNull UUID playerId) {
        return this.resetAsync(playerId).join();
    }

}
