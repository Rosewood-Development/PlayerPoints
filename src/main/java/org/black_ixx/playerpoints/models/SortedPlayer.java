package org.black_ixx.playerpoints.models;

import java.util.UUID;
import org.black_ixx.playerpoints.util.NameFetcher;

/**
 * Stores information about a player and how many points they have.
 *
 * Holder class that will sort based on the points and by the name. Note, this
 * sorts by order of highest points first and uses the UUID for any matches.
 *
 * @author Mitsugaru
 */
public class SortedPlayer implements Comparable<SortedPlayer> {

    private final UUID uuid;
    private final String username;
    private final int points;

    public SortedPlayer(UUID uuid, String username, int points) {
        this.uuid = uuid;
        this.username = username;
        this.points = points;
    }

    public SortedPlayer(UUID uuid, int points) {
        this(uuid, NameFetcher.getName(uuid), points);
    }

    /**
     * @return UUID of the player.
     */
    public UUID getUniqueId() {
        return this.uuid;
    }

    /**
     * @return Username of the player.
     */
    public String getUsername() {
        return this.username;
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
