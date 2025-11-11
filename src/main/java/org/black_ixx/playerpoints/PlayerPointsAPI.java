package org.black_ixx.playerpoints;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.black_ixx.playerpoints.event.PlayerPointsResetEvent;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.black_ixx.playerpoints.models.TransactionType;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Gives a player a specified amount of points
     *
     * @param playerId The player to give points to
     * @param amount The amount of points to give
     * @return true if the transaction was successful, false otherwise
     */
    public boolean give(@NotNull UUID playerId, int amount) {
        return this.give(playerId, null, amount);
    }

    /**
     * Gives a player a specified amount of points
     *
     * @param playerId The player to give points to
     * @param sourceId The player giving the points, nullable
     * @param amount The amount of points to give
     * @return true if the transaction was successful, false otherwise
     */
    public boolean give(@NotNull UUID playerId, @Nullable UUID sourceId, int amount) {
        Objects.requireNonNull(playerId);

        PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId, amount, TransactionType.OFFSET);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        if (amount == 0)
            return true;

        return this.plugin.getManager(DataManager.class).offsetPoints(TransactionType.OFFSET, playerId, amount > 0 ? "Give" : "Take", sourceId, event.getChange());
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
        return this.giveAll(playerIds, null, amount);
    }

    /**
     * Gives a collection of players a specified amount of points
     *
     * @param playerIds The players to give points to
     * @param sourceId The player giving the points, nullable
     * @param amount The amount of points to give
     * @return true if any transaction was successful, false otherwise
     */
    @NotNull
    public boolean giveAll(@NotNull Collection<UUID> playerIds, @Nullable UUID sourceId, int amount) {
        Objects.requireNonNull(playerIds);

        boolean success = false;
        for (UUID uuid : playerIds)
            success |= this.give(uuid, sourceId, amount);

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
        return this.take(playerId, null, amount);
    }

    /**
     * Takes a specified amount of points from a player
     *
     * @param playerId The player to take points from
     * @param sourceId The player taking the points, nullable
     * @param amount The amount of points to take
     * @return true if the transaction was successful, false otherwise
     */
    public boolean take(@NotNull UUID playerId, @Nullable UUID sourceId, int amount) {
        Objects.requireNonNull(playerId);

        return this.give(playerId, sourceId, -amount);
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
     * Looks at the number of points a player has formatted with number separators
     *
     * @param playerId The player to give points to
     * @return the amount of points a player has
     */
    public String lookFormatted(@NotNull UUID playerId) {
        Objects.requireNonNull(playerId);

        return PointsUtils.formatPoints(this.plugin.getManager(DataManager.class).getEffectivePoints(playerId));
    }

    /**
     * Looks at the number of points a player has formatted as shorthand notation
     *
     * @param playerId The player to give points to
     * @return the amount of points a player has
     */
    public String lookShorthand(@NotNull UUID playerId) {
        Objects.requireNonNull(playerId);

        return PointsUtils.formatPointsShorthand(this.plugin.getManager(DataManager.class).getEffectivePoints(playerId));
    }

    /**
     * Takes points from a source player and gives them to a target player
     *
     * @param sourceId The player to take points from
     * @param targetId The player to give points to
     * @param amount The amount of points to take/give, must be positive
     * @return true if the transaction was successful, false otherwise
     */
    public boolean pay(@NotNull UUID sourceId, @NotNull UUID targetId, int amount) {
        Objects.requireNonNull(sourceId);
        Objects.requireNonNull(targetId);

        PlayerPointsChangeEvent takeEvent = new PlayerPointsChangeEvent(sourceId, -amount, TransactionType.PAY_SENDER);
        Bukkit.getPluginManager().callEvent(takeEvent);
        if (takeEvent.isCancelled() || -takeEvent.getChange() <= 0) // If the giving amount is now 0 or negative, cancel the payment
            return false;

        DataManager dataManager = this.plugin.getManager(DataManager.class);
        if (!dataManager.offsetPoints(TransactionType.PAY_SENDER, sourceId, "Pay", targetId, takeEvent.getChange())) // Sender balance is not enough, cancel the payment
            return false;

        PlayerPointsChangeEvent giveEvent = new PlayerPointsChangeEvent(targetId, amount, TransactionType.PAY_RECEIVER);
        Bukkit.getPluginManager().callEvent(giveEvent);
        // If the receiving amount is now 0 or negative, or the amount could not be given, cancel the payment and refund the sender
        if (giveEvent.isCancelled() || giveEvent.getChange() <= 0 || !dataManager.offsetPoints(TransactionType.PAY_RECEIVER, targetId, "Pay", sourceId, giveEvent.getChange())) {
            dataManager.offsetPoints(TransactionType.OFFSET, sourceId, "Refunded Pay", targetId, -takeEvent.getChange());
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
        return this.set(playerId, null, amount);
    }

    /**
     * Sets a player's points to a specified amount
     *
     * @param playerId The player to set the points of
     * @param sourceId The player taking the points, nullable
     * @param amount The amount of points to set to
     * @return true if the transaction was successful, false otherwise
     */
    public boolean set(@NotNull UUID playerId, @Nullable UUID sourceId, int amount) {
        Objects.requireNonNull(playerId);

        DataManager dataManager = this.plugin.getManager(DataManager.class);
        int points = dataManager.getEffectivePoints(playerId);
        PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId, amount - points, TransactionType.SET);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        return dataManager.setPoints(TransactionType.SET, playerId, "Set", sourceId, points + event.getChange());
    }

    /**
     * Sets a player's points to zero
     *
     * @param playerId The player to reset the points of
     * @return true if the transaction was successful, false otherwise
     */
    public boolean reset(@NotNull UUID playerId) {
        return this.reset(playerId, null);
    }

    /**
     * Sets a player's points to zero
     *
     * @param playerId The player to reset the points of
     * @param sourceId The player resetting the points, nullable
     * @return true if the transaction was successful, false otherwise
     */
    public boolean reset(@NotNull UUID playerId, @Nullable UUID sourceId) {
        Objects.requireNonNull(playerId);

        PlayerPointsResetEvent event = new PlayerPointsResetEvent(playerId);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        return this.plugin.getManager(DataManager.class).setPoints(TransactionType.SET, playerId, "Reset", sourceId, 0);
    }

    /**
     * Gets a List of a maximum number of players sorted by the number of points they have.
     *
     * @param limit The maximum number of players to get
     * @return a List of all players sorted by the number of points they have.
     */
    public List<SortedPlayer> getTopSortedPoints(int limit) {
        return this.plugin.getManager(DataManager.class).getTopSortedPoints(limit);
    }

    /**
     * @return a List of all players sorted by the number of points they have.
     */
    public List<SortedPlayer> getTopSortedPoints() {
        return this.plugin.getManager(DataManager.class).getTopSortedPoints(null);
    }

    /**
     * Gets a known PlayerPoints account UUID by its name.
     * If the name is for a Player account, returns the Player UUID.
     * Returns {@code null} for accounts that do not exist.
     *
     * @param name The name of the player/account
     * @return The UUID of the account
     */
    public UUID getAccountUUIDByName(@NotNull String name) {
        Objects.requireNonNull(name);

        return this.plugin.getManager(DataManager.class).lookupCachedUUID(name);
    }

}
