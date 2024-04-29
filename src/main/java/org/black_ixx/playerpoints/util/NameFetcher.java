package org.black_ixx.playerpoints.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class NameFetcher {

    private static final Cache<UUID, String> UUID_NAME_LOOKUP = CacheBuilder.newBuilder().expireAfterAccess(30L, TimeUnit.MINUTES).build();
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";

    /**
     * Fetches the name of a player by their UUID.
     * If the player is currently online, their name will be returned immediately.
     * If that fails, it will attempt to fetch the name from our username cache in the database.
     * If that fails and the player has joined the server before, it will use their last known name if it is known.
     * If that fails, it will fetch their username using Mojang's API.
     * If that fails, it will return "Unknown".
     * This operation is done in place and should be run in an async task.
     *
     * @param uuid The UUID of the player.
     * @return The name of the player or "Unknown" if the name lookup failed.
     */
    public static String getName(UUID uuid) {
        try {
            return UUID_NAME_LOOKUP.get(uuid, () -> fetch(uuid));
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    private static String fetch(UUID uuid) {
        // Look for an online player first
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer != null)
            return onlinePlayer.getName();

        // Attempt to find name from our username cache in the database
        String name = PlayerPoints.getInstance().getManager(DataManager.class).lookupCachedUsername(uuid);
        if (name != null) {
            UUID_NAME_LOOKUP.put(uuid, name);
            return name;
        }

        // Attempt to find name from OfflinePlayer
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        name = offlinePlayer.getName();
        if (name != null) {
            UUID_NAME_LOOKUP.put(uuid, name);
            return name;
        }

        // Attempt to find name from Mojang's API
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            connection.setReadTimeout(5000);
            try (Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                NameResponse[] names = gson.fromJson(reader, NameResponse[].class);
                name = names[names.length - 1].getName();
                UUID_NAME_LOOKUP.put(uuid, name);
                return name;
            }
        } catch (Exception e) {
            UUID_NAME_LOOKUP.put(uuid, "Unknown");
            return "Unknown";
        }
    }

    private static class NameResponse {
        private String name;

        public String getName() {
            return this.name;
        }
    }

    private static class UUIDTypeAdapter extends TypeAdapter<UUID> {

        @Override
        public void write(JsonWriter out, UUID value) throws IOException {
            out.value(fromUUID(value));
        }

        @Override
        public UUID read(JsonReader in) throws IOException {
            return fromString(in.nextString());
        }

        public static String fromUUID(UUID value) {
            return value.toString().replace("-", "");
        }

        public static UUID fromString(String input) {
            return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        }

    }

}
