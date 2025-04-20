package org.black_ixx.playerpoints.conversion.converter;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.data.PlayerProfile;
import com.willfp.eco.core.data.ProfileExtensions;
import com.willfp.eco.core.data.ServerProfile;
import com.willfp.eco.core.data.keys.PersistentDataKey;
import com.willfp.eco.core.data.keys.PersistentDataKeyType;
import com.willfp.eco.util.NamespacedKeyUtils;
import com.willfp.eco.util.PlayerUtils;
import com.willfp.ecobits.EcoBitsPlugin;
import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.Currency;
import dev.rosewood.rosegarden.RosePlugin;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.black_ixx.playerpoints.conversion.CurrencyConverter;
import org.black_ixx.playerpoints.manager.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class EcoBitsConverter extends CurrencyConverter {

    public EcoBitsConverter(RosePlugin rosePlugin) {
        super(rosePlugin, "EcoBits");
    }

    @Override
    public boolean isAvailable(String currencyId) {
        return super.isAvailable(currencyId) && Currencies.getByID(currencyId) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void convert(String currencyId) {
        Currency currency = Currencies.getByID(currencyId);
        if (currency == null)
            throw new IllegalStateException("Currency was not loaded");

        this.rosePlugin.getLogger().warning("Converting data from EcoBits, this may take a while if you have a lot of data...");

        try { // it's jank o'clock, basically everything I need in eco is package-private
            Method spigotPluginGetter = Eco.Instance.class.getDeclaredMethod("get");
            spigotPluginGetter.setAccessible(true);
            Eco spigotEco = (Eco) spigotPluginGetter.invoke(null);
            Object profileHandler = spigotEco.getClass().getDeclaredMethod("getProfileHandler").invoke(spigotEco);
            Object defaultHandler = profileHandler.getClass().getDeclaredMethod("getDefaultHandler").invoke(profileHandler);
            Set<UUID> persistedKeys = (Set<UUID>) defaultHandler.getClass().getDeclaredMethod("getSavedUUIDs").invoke(defaultHandler);
            UUID serverUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
            PersistentDataKey<String> playerNameKey = new PersistentDataKey<>(NamespacedKeyUtils.createEcoKey("player_name"), PersistentDataKeyType.STRING, "Unknown");

            Map<UUID, Integer> players = new HashMap<>();
            Map<UUID, String> usernameMap = new HashMap<>();
            int count = 0;
            for (UUID uuid : persistedKeys) {
                try {
                    if (uuid.equals(serverUUID))
                        continue;

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    PlayerProfile playerProfile = ProfileExtensions.getProfile(offlinePlayer);
                    String username = playerProfile.read(playerNameKey);
                    BigDecimal value = playerProfile.read(currency.getKey());
                    double doubleValue = value.doubleValue();
                    if ((long) doubleValue != doubleValue) {
                        double rounded = Math.round(doubleValue);
                        this.rosePlugin.getLogger().warning(String.format("Data entry [%s] was rounded from %f to %f", uuid, doubleValue, rounded));
                        doubleValue = rounded;
                    }
                    if (doubleValue > Integer.MAX_VALUE) {
                        this.rosePlugin.getLogger().warning(String.format("Data entry [%s] was too large and was truncated from %f to %d", uuid, doubleValue, Integer.MAX_VALUE));
                        doubleValue = Integer.MAX_VALUE;
                    }
                    int pointsValue = (int) doubleValue;
                    if (pointsValue > 0) {
                        players.put(uuid, pointsValue);
                        usernameMap.put(uuid, username);
                    }

                    if (++count % 500 == 0)
                        this.rosePlugin.getLogger().warning(String.format("Converted %d entries...", count));
                } catch (Exception e) {
                    this.rosePlugin.getLogger().warning(String.format("Data entry [%s] skipped due to invalid data", uuid));
                }
            }

            DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
            dataManager.importData(players, usernameMap);

            this.rosePlugin.getLogger().warning(String.format("Successfully converted %d entries!", count));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

}
