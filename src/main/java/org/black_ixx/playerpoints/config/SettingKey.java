package org.black_ixx.playerpoints.config;

import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.SettingHolder;
import dev.rosewood.rosegarden.config.SettingSerializer;
import java.util.ArrayList;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.configuration.ConfigurationSection;
import static dev.rosewood.rosegarden.config.SettingSerializers.*;

public class SettingKey implements SettingHolder {

    public static final SettingKey INSTANCE = new SettingKey();
    private static final List<RoseSetting<?>> KEYS = new ArrayList<>();

    public static final RoseSetting<Boolean> VAULT = create("vault", BOOLEAN, false, "Should we register with Vault as a currency manager?");
    public static final RoseSetting<String> VAULT_PRIORITY = create("vault-priority", STRING, "Low", "The priority level to use for the Vault hook", "Higher priorities will allow PlayerPoints to load before other economy plugins", "Valid values: [Lowest, Low, Normal, High, Highest]");
    public static final RoseSetting<Boolean> TREASURY = create("treasury", BOOLEAN, false, "Should we register with Treasury as a currency manager?");
    public static final RoseSetting<String> TREASURY_PRIORITY = create("treasury-priority", STRING, "LOW", "The priority level to use for the Treasury hook", "Higher priorities will allow PlayerPoints to load before other economy plugins", "Valid values: [LOW, NORMAL, HIGH]");
    public static final RoseSetting<Integer> LEADERBOARD_PER_PAGE = create("leaderboard-per-page", INTEGER, 10, "The number of players to be displayed per page on the leaderboard?");
    public static final RoseSetting<Integer> CACHE_DURATION = create("cache-duration", INTEGER, 30, "The number of seconds to hold a player's points in cache before being released");
    public static final RoseSetting<Boolean> BUNGEECORD_SEND_UPDATES = create("bungeecord-send-updates", BOOLEAN, true, "Should we send updates to other servers when a player's points value changes?", "This should work for any type of proxy");
    public static final RoseSetting<Boolean> LEADERBOARD_DISABLE = create("leaderboard-disable", BOOLEAN, false, "Should the leaderboard be disabled?", "Setting this to true will cause leaderboard placeholders to no longer function");
    public static final RoseSetting<Integer> LEADERBOARD_PLACEHOLDER_ENTRIES = create("leaderboard-placeholder-entries", INTEGER, 10, "The number of entries to keep updated in the leaderboard placeholder", "Only a certain number of leaderboard entries can be available at a time", "Accessing an entry that does not exist will display a placeholder message instead");
    public static final RoseSetting<Integer> LEADERBOARD_PLACEHOLDER_REFRESH_INTERVAL = create("leaderboard-placeholder-refresh-interval", INTEGER, 15, "The number of seconds between leaderboard placeholder updates");
    public static final RoseSetting<Integer> STARTING_BALANCE = create("starting-balance", INTEGER, 0, "The amount of points new players will start with");
    public static final RoseSetting<Integer> MINIMUM_PAY_AMOUNT = create("minimum-pay-amount", INTEGER, -1, "The minimum number of points you must send when using the pay command", "Set to -1 for no minimum amount");
    public static final RoseSetting<Boolean> TAB_COMPLETE_SHOW_ALL_PLAYERS = create("tab-complete-show-all-players", BOOLEAN, false, "When true, all players that have a PlayerPoints balance will show in tab complete.", "If false, only non-vanished online players will be displayed.");
    public static final RoseSetting<Integer> CACHED_ACCOUNT_LIST_REFRESH_INTERVAL = create("cached-account-list-refresh-interval", INTEGER, 300, "How often (in seconds) should we update the list of accounts for tab completion purposes?");
    public static final RoseSetting<Boolean> SHOW_NON_PLAYER_ACCOUNTS_ON_LEADERBOARDS = create("show-non-player-accounts-on-leaderboards", BOOLEAN, false, "Should we show non-player accounts on leaderboards?");
    public static final RoseSetting<Boolean> LOG_TRANSACTIONS = create("log-transactions", BOOLEAN, true, "Should points transactions be logged to the playerpoints_transaction_log table in the database?", "This table is only able to be be queried manually but can be disabled if you do not intend to use it.");
    public static final RoseSetting<ConfigurationSection> VOTE = create("vote", "Votifier hook settings");
    public static final RoseSetting<Boolean> VOTE_ENABLED = create("vote.enabled", BOOLEAN, false, "If the votifier hook should be enabled");
    public static final RoseSetting<Integer> VOTE_AMOUNT = create("vote.amount", INTEGER, 100, "How many points should be awarded per vote");
    public static final RoseSetting<Boolean> VOTE_ONLINE = create("vote.online", BOOLEAN, false, "Should points only be awarded when the player who voted is online?");
    public static final RoseSetting<String> BASE_COMMAND_REDIRECT = create("base-command-redirect", STRING, "me", "Which command should we redirect to when using '/points' with no subcommand specified?", "You can use any points command here that does not take any arguments", "If left as blank, the default behavior of showing '/points me' with bypassed permissions will be used");
    public static final RoseSetting<ConfigurationSection> LEGACY_DATABASE = create("legacy-database-mode", "Are you upgrading from a much older version of PlayerPoints?", "If you have done anything special with the database settings previously, you may need this", "WARNING: This setting may be removed in the future. Try to get your database updated to use the new format!");
    public static final RoseSetting<Boolean> LEGACY_DATABASE_MODE = create("legacy-database-mode.enabled", BOOLEAN, false, "Should we use legacy database mode?");
    public static final RoseSetting<String> LEGACY_DATABASE_NAME = create("legacy-database-mode.table-name", STRING, "playerpoints", "The name of the legacy database table");

    private static <T> RoseSetting<T> create(String key, SettingSerializer<T> serializer, T defaultValue, String... comments) {
        RoseSetting<T> setting = RoseSetting.ofBackedValue(key, PlayerPoints.getInstance(), serializer, defaultValue, comments);
        KEYS.add(setting);
        return setting;
    }

    private static RoseSetting<ConfigurationSection> create(String key, String... comments) {
        RoseSetting<ConfigurationSection> setting = RoseSetting.ofBackedSection(key, PlayerPoints.getInstance(), comments);
        KEYS.add(setting);
        return setting;
    }

    private SettingKey() {}

    @Override
    public List<RoseSetting<?>> get() {
        return KEYS;
    }

}
