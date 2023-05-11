package org.black_ixx.playerpoints.conversion.converter;

import dev.rosewood.rosegarden.RosePlugin;
import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.data.DataManager;
import me.realized.tokenmanager.data.database.Database;
import org.black_ixx.playerpoints.conversion.CurrencyConverter;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Predicate;

public class TokenManagerConverter extends CurrencyConverter {

    public TokenManagerConverter(RosePlugin rosePlugin) {
        super(rosePlugin, "TokenManager");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private final Predicate<String> uuidPredicate = (str) -> {
        try {
            UUID.fromString(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    };

    @Override
    public boolean convert() {
        TokenManagerPlugin tokenManager = (TokenManagerPlugin) this.plugin;
        DataManager dataManager = tokenManager.getDataManager();
        try {
            Field field_database = DataManager.class.getDeclaredField("database");
            field_database.setAccessible(true);
            Database database = (Database) field_database.get(dataManager);
            database.ordered(Integer.MAX_VALUE, data -> Bukkit.getScheduler().runTaskAsynchronously(this.rosePlugin, () -> {
                if (data.isEmpty())
                    return;

                final String strUUID = data.get(0).getKey();
                boolean isUUID = uuidPredicate.test(strUUID);

                SortedSet<SortedPlayer> pointsData = new TreeSet<>();
                for (Database.TopElement entry : data) {
                    try {
                        UUID uuid = isUUID ?
                                UUID.fromString(entry.getKey()) :
                                Bukkit.getOfflinePlayer(entry.getKey()).getUniqueId();

                        pointsData.add(new SortedPlayer(uuid, Math.toIntExact(entry.getTokens())));
                    } catch (Exception e) {
                        this.rosePlugin.getLogger().warning(String.format("Data entry [%s:%d] skipped due to invalid data", entry.getKey(), entry.getTokens()));
                    }
                }

                this.rosePlugin.getManager(org.black_ixx.playerpoints.manager.DataManager.class).importData(pointsData, Collections.emptyMap());
            }));
            return true;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return false;
    }
}
