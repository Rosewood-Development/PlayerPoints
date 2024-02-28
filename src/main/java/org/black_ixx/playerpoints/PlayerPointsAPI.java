package org.black_ixx.playerpoints;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.black_ixx.playerpoints.event.PlayerPointsResetEvent;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.black_ixx.playerpoints.util.PointsUtils;
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

        return this.plugin.getManager(DataManager.class).offsetPoints(playerId, event.getChange());
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
        Objects.requireNonNull(playerId);

        DataManager dataManager = this.plugin.getManager(DataManager.class);
        int points = dataManager.getEffectivePoints(playerId);
        PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId, amount - points);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        return dataManager.setPoints(playerId, points + event.getChange());
    }

    /**
     * Sets a player's points to zero
     *
     * @param playerId The player to reset the points of
     * @return true if the transaction was successful, false otherwise
     */
    public boolean reset(@NotNull UUID playerId) {
        Objects.requireNonNull(playerId);

        PlayerPointsResetEvent event = new PlayerPointsResetEvent(playerId);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        return this.plugin.getManager(DataManager.class).setPoints(playerId, 0);
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

}
