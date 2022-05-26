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
import su.nexmedia.engine.api.data.AbstractDataHandler;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.data.GamePointsData;

public class GamePointsConverter extends CurrencyConverter {

    public GamePointsConverter(RosePlugin rosePlugin) {
        super(rosePlugin, "GamePoints");
    }

    @Override
    public void convert() {
        GamePointsData gamePointsData = GamePoints.getInstance().getData();
        try {
            Method method_getConnection = AbstractDataHandler.class.getDeclaredMethod("getConnection");
            method_getConnection.setAccessible(true);
            Connection connection = (Connection) method_getConnection.invoke(gamePointsData);

            String query = "SELECT uuid, name, balance FROM gamepoints_users";
            try (Statement statement = connection.createStatement()) {
                SortedSet<SortedPlayer> players = new TreeSet<>();
                Map<UUID, String> usernameMap = new HashMap<>();

                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    try {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        String name = resultSet.getString("name");
                        int balance = resultSet.getInt("balance");
                        if (balance > 0)
                            players.add(new SortedPlayer(uuid, name, balance));
                        usernameMap.put(uuid, name);
                    } catch (Exception ignored) { }
                }

                DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
                dataManager.importData(players, Collections.emptyMap());
                dataManager.updateCachedUsernames(usernameMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
