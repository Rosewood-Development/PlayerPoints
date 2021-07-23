package org.black_ixx.playerpoints.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.StringUtil;

public final class PointsUtils {

    private static NumberFormat formatter = NumberFormat.getInstance();

    public static String formatPoints(long points) {
        if (formatter != null) {
            return formatter.format(points);
        } else {
            return String.valueOf(points);
        }
    }

    public static void setFormatter(String separator) {
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
