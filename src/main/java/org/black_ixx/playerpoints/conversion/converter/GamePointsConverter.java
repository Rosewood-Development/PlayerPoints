package org.black_ixx.playerpoints.conversion.converter;

import dev.rosewood.rosegarden.RosePlugin;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.conversion.CurrencyConverter;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.models.SortedPlayer;
import su.nightexpress.gamepoints.GamePoints;

public class GamePointsConverter extends CurrencyConverter {

    public GamePointsConverter(RosePlugin rosePlugin) {
        super(rosePlugin, "GamePoints");
    }

    @Override
    public void convert() {
        SortedSet<SortedPlayer> users = GamePoints.getInstance().getData().getUsers().stream()
                .filter(x -> x.getBalance() > 0)
                .map(x -> new SortedPlayer(x.getUUID(), x.getBalance()))
                .collect(Collectors.toCollection(TreeSet::new));

        DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
        dataManager.importData(users);
    }

}
