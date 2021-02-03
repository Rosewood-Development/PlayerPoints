package org.black_ixx.playerpoints.storage;

import java.util.Collection;

/**
 * Represents a storage model object.
 * 
 * @author Mitsugaru
 */
public interface IStorage {

    /**
     * Get the amount of points the given player has.
     * 
     * @param id
     *            - Id of player.
     * @return Points the player has.
     */
    int getPoints(String id);

    /**
     * Set the amount of points for the given player.
     * 
     * @param id
     *            - Player id
     * @param points
     *            - Amount of points to set.
     * @return True if we were successful in editing, else false.
     */
    boolean setPoints(String id, int points);

    /**
     * Check whether the player already exists in the storage medium.
     * 
     * @param id
     *            - Player id.
     * @return True if player is in storage, else false.
     */
    boolean playerEntryExists(String id);

    /**
     * Remove player entry.
     * 
     * @param id
     *            - Player to remove.
     * @return True if entry was removed, else false.
     */
    boolean removePlayer(String id);

    /**
     * Completely destroy all records.
     * 
     * @return True if successful, else false.
     */
    boolean destroy();

    /**
     * Build storage.
     * 
     * @return True if successful, else false.
     */
    boolean build();

    /**
     * Get the existing players in storage.
     * 
     * @return Collection of player IDs.
     */
    Collection<String> getPlayers();

}