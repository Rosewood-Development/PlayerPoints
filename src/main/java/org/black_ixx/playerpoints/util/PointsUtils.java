package org.black_ixx.playerpoints.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.StringUtil;

public final class PointsUtils {

    private static NumberFormat formatter = NumberFormat.getInstance();

    public static String formatPoints(long points) {
        return formatter.format(points);
    }

    public static void setFormatter(String separator) {
        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(!separator.isEmpty() ? separator.charAt(0) : ',');
        decimalFormat.setDecimalFormatSymbols(symbols);
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        formatter = decimalFormat;
    }

    /**
     * Gets an OfflinePlayer by name, prioritizing online players.
     *
     * @param name The name of the player
     * @return An OfflinePlayer
     */
    @SuppressWarnings("deprecation")
    public static OfflinePlayer getPlayerByName(String name) {
        OfflinePlayer player = Bukkit.getPlayer(name);
        if (player != null) {
            return player;
        } else {
            return Bukkit.getOfflinePlayer(name);
        }
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
