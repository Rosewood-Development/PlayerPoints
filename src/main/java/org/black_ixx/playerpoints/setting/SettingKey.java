package org.black_ixx.playerpoints.setting;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.RoseSettingSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.black_ixx.playerpoints.PlayerPoints;
import static dev.rosewood.rosegarden.config.RoseSettingSerializers.*;

public class SettingKey {

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
    public static final RoseSetting<CommentedConfigurationSection> VOTE = create("vote", "Votifier hook settings");
    public static final RoseSetting<Boolean> VOTE_ENABLED = create("vote.enabled", BOOLEAN, false, "If the votifier hook should be enabled");
    public static final RoseSetting<Integer> VOTE_AMOUNT = create("vote.amount", INTEGER, 100, "How many points should be awarded per vote");
    public static final RoseSetting<Boolean> VOTE_ONLINE = create("vote.online", BOOLEAN, false, "Should points only be awarded when the player who voted is online?");
    public static final RoseSetting<String> BASE_COMMAND_REDIRECT = create("base-command-redirect", STRING, "", "Which command should we redirect to when using '/points' with no subcommand specified?", "You can use a value here such as 'me' to show the output of '/points me'", "If you have any aliases defined, do not use them here", "If left as blank, the default behavior of showing '/points version' with bypassed permissions will be used");
    public static final RoseSetting<CommentedConfigurationSection> LEGACY_DATABASE = create("legacy-database-mode", "Are you upgrading from a much older version of PlayerPoints?", "If you have done anything special with the database settings previously, you may need this", "WARNING: This setting may be removed in the future. Try to get your database updated to use the new format!");
    public static final RoseSetting<Boolean> LEGACY_DATABASE_MODE = create("legacy-database-mode.enabled", BOOLEAN, false, "Should we use legacy database mode?");
    public static final RoseSetting<String> LEGACY_DATABASE_NAME = create("legacy-database-mode.table-name", STRING, "playerpoints", "The name of the legacy database table");

    private static <T> RoseSetting<T> create(String key, RoseSettingSerializer<T> serializer, T defaultValue, String... comments) {
        RoseSetting<T> setting = RoseSetting.backed(PlayerPoints.getInstance(), key, serializer, defaultValue, comments);
        KEYS.add(setting);
        return setting;
    }

    private static RoseSetting<CommentedConfigurationSection> create(String key, String... comments) {
        RoseSetting<CommentedConfigurationSection> setting = RoseSetting.backedSection(PlayerPoints.getInstance(), key, comments);
        KEYS.add(setting);
        return setting;
    }

    public static List<RoseSetting<?>> getKeys() {
        return Collections.unmodifiableList(KEYS);
    }

    private SettingKey() {}

}
