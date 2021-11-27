package org.black_ixx.playerpoints.util;

import dev.rosewood.rosegarden.RosePlugin;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.manager.ConfigurationManager.Setting;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.StringUtil;

public final class PointsUtils {

    private static NumberFormat formatter = NumberFormat.getInstance();
    private static NavigableMap<Long, String> suffixes = new TreeMap<>();

    public static String formatPoints(long points) {
        if (formatter != null) {
            return formatter.format(points);
        } else {
            return String.valueOf(points);
        }
    }

    public static String formatPointsShorthand(long points) {
        if (points == Long.MIN_VALUE) return formatPointsShorthand(Long.MIN_VALUE + 1);
        if (points < 0) return "-" + formatPointsShorthand(-points);
        if (points < 1000) return Long.toString(points); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(points);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = points / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10D) != (truncated / 10);
        return hasDecimal ? (truncated / 10D) + suffix : (truncated / 10) + suffix;
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

        suffixes.clear();
        suffixes.put(1_000L, localeManager.getLocaleMessage("number-abbreviation-thousands"));
        suffixes.put(1_000_000L, localeManager.getLocaleMessage("number-abbreviation-millions"));
        suffixes.put(1_000_000_000L, localeManager.getLocaleMessage("number-abbreviation-billions"));
    }

    /**
     * Gets an OfflinePlayer by name, prioritizing online players.
     * This method exists just so the deprecation warning only has to be suppressed in one place.
     *
     * @param name The name of the player
     * @return An OfflinePlayer
     */
    @SuppressWarnings("deprecation")
    public static OfflinePlayer getPlayerByName(String name) {
        return Bukkit.getOfflinePlayer(name);
    }

    /**
     * Gets a list of player names to show in tab completions, vanished players
     *
     * @param arg The argument for the name
     * @return a list of online players excluding the
     */
    public static List<String> getPlayerTabComplete(String arg) {
        List<String> players = Bukkit.getOnlinePlayers().stream()
                .filter(x -> x.getMetadata("vanished").stream().noneMatch(MetadataValue::asBoolean))
                .map(Player::getName)
                .collect(Collectors.toList());
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, players, completions);
        return completions;
    }

}
