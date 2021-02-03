package org.black_ixx.playerpoints.storage.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

import lib.PatPeter.SQLibrary.SQLite;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.DatabaseStorage;

/**
 * Storage handler for SQLite.
 * 
 * @author Mitsugaru
 */
public class SQLiteStorage extends DatabaseStorage {

    /**
     * SQLite reference.
     */
    private final SQLite sqlite;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - PlayerPoints instance.
     */
    public SQLiteStorage(PlayerPoints plugin) {
        super(plugin);
        sqlite = new SQLite(plugin.getLogger(), " ", plugin.getDataFolder()
                .getAbsolutePath(), "storage");
        sqlite.open();
        SetupQueries("playerpoints");
        if(!sqlite.isTable("playerpoints")) {
            build();
        }
    }

    @Override
    public int getPoints(String name) {
        int points = 0;
        if(name == null || name.equals("")) {
            return points;
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = sqlite.prepare(GET_POINTS);
            statement.setString(1, name);
            result = sqlite.query(statement);
            if(result != null && result.next()) {
                points = result.getInt("points");
            }
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create getter statement.", e);
        } finally {
            cleanup(result, statement);
        }
        return points;
    }

    @Override
    public boolean setPoints(String name, int points) {
        boolean value = false;
        if(name == null || name.equals("")) {
            return value;
        }
        final boolean exists = playerEntryExists(name);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            if(exists) {
                statement = sqlite.prepare(UPDATE_PLAYER);
            } else {
                statement = sqlite.prepare(INSERT_PLAYER);
            }
            statement.setInt(1, points);
            statement.setString(2, name);
            result = sqlite.query(statement);
            value = true;
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create setter statement.", e);
        } finally {
            cleanup(result, statement);
        }
        return value;
    }

    @Override
    public boolean playerEntryExists(String name) {
        boolean has = false;
        if(name == null || name.equals("")) {
            return false;
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = sqlite.prepare(GET_POINTS);
            statement.setString(1, name);
            result = sqlite.query(statement);
            if(result.next()) {
                has = true;
            }
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create player check statement.", e);
        } finally {
            cleanup(result, statement);
        }
        return has;
    }
    
    @Override
    public boolean removePlayer(String id) {
        boolean deleted = false;
        if(id == null || id.equals("")) {
            return false;
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = sqlite.prepare(REMOVE_PLAYER);
            statement.setString(1, id);
            result = sqlite.query(statement);
            deleted = true;
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create player remove statement.", e);
        } finally {
            cleanup(result, statement);
        }
        return deleted;
    }

    @Override
    public Collection<String> getPlayers() {
        Collection<String> players = new HashSet<>();

        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = sqlite.prepare(GET_PLAYERS);
            result = sqlite.query(statement);

            while(result.next()) {
                String name = result.getString("playername");
                if(name != null) {
                    players.add(name);
                }
            }
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create get players statement.", e);
        } finally {
            cleanup(result, statement);
        }

        return players;
    }

    @Override
    public boolean destroy() {
        boolean success = false;
        plugin.getLogger().info("Creating playerpoints table");
        try {
            sqlite.query("DROP TABLE playerpoints;");
            success = true;
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not drop SQLite table.", e);
        }
        return success;
    }

    @Override
    public boolean build() {
        boolean success = false;
        plugin.getLogger().info("Creating playerpoints table");
        try {
            sqlite.query("CREATE TABLE playerpoints (id INTEGER PRIMARY KEY, playername varchar(36) NOT NULL, points INTEGER NOT NULL, UNIQUE(playername));");
            success = true;
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create SQLite table.", e);
        }
        return success;
    }

}