package org.black_ixx.playerpoints.conversion.converter;

import dev.rosewood.rosegarden.RosePlugin;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.black_ixx.playerpoints.conversion.CurrencyConverter;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.Bukkit;
import su.nexmedia.engine.api.data.AbstractDataHandler;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.data.PointsDataHandler;

public class GamePointsConverter extends CurrencyConverter {

    public GamePointsConverter(RosePlugin rosePlugin) {
        super(rosePlugin, "GamePoints");
    }

    @Override
    public void convert() {
        PointsDataHandler pointsDataHandler = ((GamePoints) Bukkit.getPluginManager().getPlugin("GamePoints")).getData();
        try {
            Method method_getConnection = AbstractDataHandler.class.getDeclaredMethod("getConnection");
            method_getConnection.setAccessible(true);
            Connection connection = (Connection) method_getConnection.invoke(pointsDataHandler);

            this.rosePlugin.getLogger().warning("Converting data from GamePoints, this may take a while if you have a lot of data...");
            String query = "SELECT uuid, name, balance FROM gamepoints_users";
            try (Statement statement = connection.createStatement()) {
                SortedSet<SortedPlayer> players = new TreeSet<>();
                Map<UUID, String> usernameMap = new HashMap<>();

                int count = 0;
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    try {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        String name = resultSet.getString("name");
                        int balance = resultSet.getInt("balance");
                        if (balance > 0)
                            players.add(new SortedPlayer(uuid, name, balance));
                        usernameMap.put(uuid, name);

                        if (++count % 500 == 0)
                            this.rosePlugin.getLogger().warning(String.format("Converted %d entries...", count));
                    } catch (Exception ignored) { }
                }

                DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
                dataManager.importData(players, Collections.emptyMap());
                dataManager.updateCachedUsernames(usernameMap);

                this.rosePlugin.getLogger().warning(String.format("Successfully converted %d entries!", count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
