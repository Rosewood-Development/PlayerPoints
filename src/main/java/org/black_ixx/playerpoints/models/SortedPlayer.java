package org.black_ixx.playerpoints.models;

import java.util.UUID;

/**
 * Holder class that will sort based on the points and by the name. Note, this
 * sorts by order of highest points first and uses player name for any matches.
 *
 * @author Mitsugaru
 */
public class SortedPlayer implements Comparable<SortedPlayer> {

    /**
     * Player name.
     */
    final UUID uuid;

    /**
     * Player points.
     */
    final int points;

    /**
     * Constructor.
     *
     * @param uuid   - Player UUID.
     * @param points - Point amount.
     */
    public SortedPlayer(UUID uuid, int points) {
        this.uuid = uuid;
        this.points = points;
    }

    /**
     * Get the player UUID.
     *
     * @return UUID of the player.
     */
    public UUID getUniqueId() {
        return this.uuid;
    }

    /**
     * Get the player points.
     *
     * @return Point amount.
     */
    public int getPoints() {
        return this.points;
    }

    @Override
    public int compareTo(SortedPlayer o) {
        if (this.getPoints() > o.getPoints()) {
            return -1;
        } else if (this.getPoints() < o.getPoints()) {
            return 1;
        }
        return this.getUniqueId().compareTo(o.getUniqueId());
    }

}
