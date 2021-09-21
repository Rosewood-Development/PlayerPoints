package org.black_ixx.playerpoints.models;

import java.util.UUID;

/**
 * Holder class that will sort based on the points and by the name. Note, this
 * sorts by order of highest points first and uses the UUID for any matches.
 *
 * @author Mitsugaru
 */
public class SortedPlayer implements Comparable<SortedPlayer> {

    /**
     * Player UUID.
     */
    final UUID uuid;

    /**
     * Player points.
     */
    final int points;

    public SortedPlayer(UUID uuid, int points) {
        this.uuid = uuid;
        this.points = points;
    }

    /**
     * @return UUID of the player.
     */
    public UUID getUniqueId() {
        return this.uuid;
    }

    /**
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
