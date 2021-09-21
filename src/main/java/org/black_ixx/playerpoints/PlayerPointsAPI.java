package org.black_ixx.playerpoints;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.black_ixx.playerpoints.event.PlayerPointsResetEvent;
import org.black_ixx.playerpoints.manager.DataManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * The API for the PlayerPoints plugin.
 * Used to manipulate a player's points balance.
 *
 * Note: This API does not send any messages and changes will be saved to the database automatically.
 */
public class PlayerPointsAPI {

    private final PlayerPoints plugin;

    public PlayerPointsAPI(PlayerPoints plugin) {
        this.plugin = plugin;
    }

    //#region Deprecated

    /**
     * Gives a player a specified amount of points
     *
     * @param playerId The player to give points to
     * @param amount The amount of points to give
     * @return true if the transaction was successful, false otherwise
     * @deprecated Use {@link PlayerPointsAPI#give(UUID, int)} instead, this is no longer run async
     */
    @NotNull
    @Deprecated
    public CompletableFuture<Boolean> giveAsync(@NotNull UUID playerId, int amount) {
        return CompletableFuture.completedFuture(this.give(playerId, amount));
    }

    /**
     * Gives a collection of players a specified amount of points
     *
     * @param playerIds The players to give points to
     * @param amount The amount of points to give
     * @return true if any transaction was successful, false otherwise
     * @deprecated Use {@link PlayerPointsAPI#giveAll(Collection, int)} instead, this is no longer run async
     */
    @NotNull
    @Deprecated
    public CompletableFuture<Boolean> giveAllAsync(@NotNull Collection<UUID> playerIds, int amount) {
        return CompletableFuture.completedFuture(this.giveAll(playerIds, amount));
    }

    /**
     * Takes a specified amount of points from a player
     *
     * @param playerId The player to take points from
     * @param amount The amount of points to take
     * @return true if the transaction was successful, false otherwise
     * @deprecated Use {@link PlayerPointsAPI#take(UUID, int)} instead, this is no longer run async
     */
    @NotNull
    @Deprecated
    public CompletableFuture<Boolean> takeAsync(@NotNull UUID playerId, int amount) {
        return CompletableFuture.completedFuture(this.take(playerId, amount));
    }

    /**
     * Looks at the number of points a player has
     *
     * @param playerId The player to give points to
     * @return the amount of points a player has
     * @deprecated Use {@link PlayerPointsAPI#look(UUID)} instead, this is no longer run async
     */
    @NotNull
    @Deprecated
    public CompletableFuture<Integer> lookAsync(@NotNull UUID playerId) {
        return CompletableFuture.completedFuture(this.look(playerId));
    }

    /**
     * Takes points from a source player and gives them to a target player
     *
     * @param source The player to take points from
     * @param target The player to give points to
     * @param amount The amount of points to take/give
     * @return true if the transaction was successful, false otherwise
     * @deprecated Use {@link PlayerPointsAPI#pay(UUID, UUID, int)} instead, this is no longer run async
     */
    @NotNull
    @Deprecated
    public CompletableFuture<Boolean> payAsync(@NotNull UUID source, @NotNull UUID target, int amount) {
        return CompletableFuture.completedFuture(this.pay(source, target, amount));
    }

    /**
     * Sets a player's points to a specified amount
     *
     * @param playerId The player to set the points of
     * @param amount The amount of points to set to
     * @return true if the transaction was successful, false otherwise
     * @deprecated Use {@link PlayerPointsAPI#set(UUID, int)} instead, this is no longer run async
     */
    @NotNull
    @Deprecated
    public CompletableFuture<Boolean> setAsync(@NotNull UUID playerId, int amount) {
        return CompletableFuture.completedFuture(this.set(playerId, amount));
    }

    /**
     * Sets a player's points to zero
     *
     * @param playerId The player to reset the points of
     * @return true if the transaction was successful, false otherwise
     * @deprecated Use {@link PlayerPointsAPI#reset(UUID)} instead, this is no longer run async
     */
    @NotNull
    @Deprecated
    public CompletableFuture<Boolean> resetAsync(@NotNull UUID playerId) {
        return CompletableFuture.completedFuture(this.reset(playerId));
    }

    //#endregion

    /**
     * Gives a player a specified amount of points
     *
     * @param playerId The player to give points to
     * @param amount The amount of points to give
     * @return true if the transaction was successful, false otherwise
     */
    public boolean give(@NotNull UUID playerId, int amount) {
        Objects.requireNonNull(playerId);

        PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId, amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        return this.plugin.getManager(DataManager.class).offsetPoints(playerId, amount);
    }

    /**
     * Gives a collection of players a specified amount of points
     *
     * @param playerIds The players to give points to
     * @param amount The amount of points to give
     * @return true if any transaction was successful, false otherwise
     */
    @NotNull
    public boolean giveAll(@NotNull Collection<UUID> playerIds, int amount) {
        Objects.requireNonNull(playerIds);

        boolean success = false;
        for (UUID uuid : playerIds)
            success |= this.give(uuid, amount);

        return success;
    }

    /**
     * Takes a specified amount of points from a player
     *
     * @param playerId The player to take points from
     * @param amount The amount of points to take
     * @return true if the transaction was successful, false otherwise
     */
    public boolean take(@NotNull UUID playerId, int amount) {
        Objects.requireNonNull(playerId);

        return this.give(playerId, -amount);
    }

    /**
     * Looks at the number of points a player has
     *
     * @param playerId The player to give points to
     * @return the amount of points a player has
     */
    public int look(@NotNull UUID playerId) {
        Objects.requireNonNull(playerId);

        return this.plugin.getManager(DataManager.class).getEffectivePoints(playerId);
    }

    /**
     * Takes points from a source player and gives them to a target player
     *
     * @param source The player to take points from
     * @param target The player to give points to
     * @param amount The amount of points to take/give
     * @return true if the transaction was successful, false otherwise
     */
    public boolean pay(@NotNull UUID source, @NotNull UUID target, int amount) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);

        if (!this.take(source, amount))
            return false;

        if (!this.give(target, amount)) {
            this.give(source, amount);
            return false;
        }

        return true;
    }

    /**
     * Sets a player's points to a specified amount
     *
     * @param playerId The player to set the points of
     * @param amount The amount of points to set to
     * @return true if the transaction was successful, false otherwise
     */
    public boolean set(@NotNull UUID playerId, int amount) {
        DataManager dataManager = this.plugin.getManager(DataManager.class);
        int points = dataManager.getEffectivePoints(playerId);
        PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId, amount - points);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        return dataManager.setPoints(playerId, amount);
    }

    /**
     * Sets a player's points to zero
     *
     * @param playerId The player to reset the points of
     * @return true if the transaction was successful, false otherwise
     */
    public boolean reset(@NotNull UUID playerId) {
        PlayerPointsResetEvent event = new PlayerPointsResetEvent(playerId);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        return this.plugin.getManager(DataManager.class).setPoints(playerId, 0);
    }

}
