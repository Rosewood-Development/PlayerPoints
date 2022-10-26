package org.black_ixx.playerpoints.conversion.converter;

import dev.rosewood.rosegarden.RosePlugin;
import me.scruffyboy13.Economy.EconomyMain;
import me.scruffyboy13.Economy.eco.Economy;
import me.scruffyboy13.Economy.eco.PlayerBalance;
import org.black_ixx.playerpoints.conversion.CurrencyConverter;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.models.SortedPlayer;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Convertor for Economy Plugin
 * Spigot: https://www.spigotmc.org/resources/economy.87053/
 * Github: https://github.com/akrentz6/Economy
 * <p>
 * Added by RemainingToast
 */
public class EconomyConverter extends CurrencyConverter {

    public EconomyConverter(RosePlugin rosePlugin) {
        super(rosePlugin, "Economy");
    }

    @Override
    public boolean convert() {
        final EconomyMain plugin = (EconomyMain) this.plugin;
        final SortedSet<SortedPlayer> players = new TreeSet<>();
        try {
            Field field_eco = EconomyMain.class.getDeclaredField("eco");
            field_eco.setAccessible(true);

            Economy eco = (Economy) field_eco.get(plugin);

            for (PlayerBalance bal : eco.getPlayers()) {
                if (bal.getBalance() <= 0) {
                    continue;
                }

                players.add(new SortedPlayer(bal.getUUID(), (int) Math.round(bal.getBalance())));
            }

            this.rosePlugin.getManager(DataManager.class).importData(players, Collections.emptyMap());
            return true;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return false;
    }
}
