package org.black_ixx.playerpoints.util;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.SettingKey;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public final class PointsUtils {

    private static NumberFormat formatter = NumberFormat.getInstance();
    private static String decimal;
    private static final NavigableMap<Long, String> SUFFIXES = new TreeMap<>();
    private static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    /**
     * Formats a number from 1100 to 1,100
     *
     * @param points The points value to format
     * @return The formatted shorthand value
     */
    public static String formatPoints(long points) {
        if (formatter != null) {
            return formatter.format(points);
        } else {
            return String.valueOf(points);
        }
    }

    /**
     * @return Gets the decimal separator for the shorthand points format
     */
    public static char getDecimalSeparator() {
        if (decimal == null || decimal.trim().isEmpty())
            return '.';
        return decimal.charAt(0);
    }

    /**
     * Formats a number from 1100 to 1.1k
     * Adapted from <a href="https://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java"></a>
     *
     * @param points The points value to format
     * @return The formatted shorthand value
     */
    public static String formatPointsShorthand(long points) {
        if (points == Long.MIN_VALUE) return formatPointsShorthand(Long.MIN_VALUE + 1);
        if (points < 0) return "-" + formatPointsShorthand(-points);
        if (points < 1000) return Long.toString(points);

        Map.Entry<Long, String> entry = SUFFIXES.floorEntry(points);
        Long divideBy = entry.getKey();
        String suffix = entry.getValue();

        long truncated = points / (divideBy / 10);
        return ((truncated / 10D) + suffix).replaceFirst(Pattern.quote("."), getDecimalSeparator() + "");
    }

    public static void setCachedValues(RosePlugin rosePlugin) {
        LocaleManager localeManager = rosePlugin.getManager(LocaleManager.class);

        String separator = localeManager.getLocaleMessage("currency-separator");
        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        if (!separator.isEmpty()) {
            symbols.setGroupingSeparator(separator.charAt(0));
            decimalFormat.setGroupingUsed(true);
            decimalFormat.setGroupingSize(3);
            decimalFormat.setDecimalFormatSymbols(symbols);
            formatter = decimalFormat;
        } else {
            formatter = null;
        }

        SUFFIXES.clear();
        SUFFIXES.put(1_000L, localeManager.getLocaleMessage("number-abbreviation-thousands"));
        SUFFIXES.put(1_000_000L, localeManager.getLocaleMessage("number-abbreviation-millions"));
        SUFFIXES.put(1_000_000_000L, localeManager.getLocaleMessage("number-abbreviation-billions"));
        decimal = localeManager.getLocaleMessage("currency-decimal");
    }

    /**
     * Gets an OfflinePlayer by name, prioritizing online players.
     *
     * @param name The name of the player
     * @param callback A callback to run with a tuple of the player's UUID and name, or null if not found
     */
    @SuppressWarnings("deprecation")
    public static void getPlayerByName(String name, Consumer<Tuple<UUID, String>> callback) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            callback.accept(new Tuple<>(player.getUniqueId(), player.getName()));
            return;
        }

        PlayerPoints plugin = PlayerPoints.getInstance();
        DataManager dataManager = plugin.getManager(DataManager.class);
        plugin.getScheduler().runTaskAsync(() -> {
            UUID uuid = plugin.getManager(DataManager.class).lookupCachedUUID(name);
            if (uuid != null) {
                Tuple<UUID, String> tuple = new Tuple<>(uuid, name);
                plugin.getScheduler().runTask(() -> callback.accept(tuple));
                return;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if (offlinePlayer.getName() != null && offlinePlayer.hasPlayedBefore()) {
                Tuple<UUID, String> tuple = new Tuple<>(offlinePlayer.getUniqueId(), offlinePlayer.getName());
                plugin.getScheduler().runTask(() -> callback.accept(tuple));
                dataManager.updateCachedUsernames(Collections.singletonMap(tuple.getFirst(), tuple.getSecond()));
                return;
            }

            plugin.getScheduler().runTask(() -> callback.accept(null));
        });
    }

    /**
     * Gets an OfflinePlayer by name, prioritizing online players.
     * Warning: This method can cause a blocking call to the database for UUID lookups.
     *
     * @param name The name of the player
     * @return a tuple of the player's UUID and name, or null if not found
     */
    public static Tuple<UUID, String> getPlayerByName(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null)
            return new Tuple<>(player.getUniqueId(), player.getName());

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (offlinePlayer.getName() != null && offlinePlayer.hasPlayedBefore())
            return new Tuple<>(offlinePlayer.getUniqueId(), offlinePlayer.getName());

        UUID uuid = PlayerPoints.getInstance().getManager(DataManager.class).lookupCachedUUID(name);
        if (uuid != null)
            return new Tuple<>(uuid, name);

        return null;
    }

    public static List<String> getPlayerTabCompleteWithoutSelf(CommandContext context) {
        return getPlayerTabComplete(context, true);
    }

    public static List<String> getPlayerTabComplete(CommandContext context) {
        return getPlayerTabComplete(context, false);
    }

    /**
     * @return a list of all accounts + online players excluding vanished players
     */
    private static List<String> getPlayerTabComplete(CommandContext context, boolean hideSelf) {
        Set<String> usernames = Bukkit.getOnlinePlayers().stream()
                .filter(PointsUtils::isVisible)
                .filter(x -> !hideSelf || !Objects.equals(x, context.getSender()))
                .map(Player::getName)
                .collect(Collectors.toCollection(HashSet::new));

        usernames.addAll(context.getRosePlugin().getManager(DataManager.class).getAllAccountNames());

        return new ArrayList<>(usernames);
    }

    public static boolean isVisible(Player player) {
        return player.getMetadata("vanished").stream().noneMatch(MetadataValue::asBoolean);
    }

    public static UUID getSenderUUID(CommandSender sender) {
        if (sender instanceof Entity) {
            return ((Entity) sender).getUniqueId();
        } else {
            return CONSOLE_UUID;
        }
    }

}
