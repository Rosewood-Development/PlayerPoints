package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import org.black_ixx.playerpoints.PlayerPoints;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        VAULT("vault", false, "Should we register with Vault as a currency manager?"),
        VAULT_PRIORITY("vault-priority", "Low", "The priority level to use for the Vault hook", "Higher priorities will allow PlayerPoints to load before other economy plugins", "Valid values: [Lowest, Low, Normal, High, Highest]"),
        TREASURY("treasury", false, "Should we register with Treasury as a currency manager?"),
        TREASURY_PRIORITY("treasury-priority", "LOW", "The priority level to use for the Treasury hook", "Higher priorities will allow PlayerPoints to load before other economy plugins", "Valid values: [LOW, NORMAL, HIGH]"),
        LEADERBOARD_PER_PAGE("leaderboard-per-page", 10, "The number of players to be displayed per page on the leaderboard?"),
        CACHE_DURATION("cache-duration", 30, "The number of seconds to hold a player's points in cache before being released"),
        BUNGEECORD_SEND_UPDATES("bungeecord-send-updates", true, "Should we send updates to other servers when a player's points value changes?", "This should work for any type of proxy"),
        LEADERBOARD_DISABLE("leaderboard-disable", false, "Should the leaderboard be disabled?", "Setting this to true will cause leaderboard placeholders to no longer function"),
        LEADERBOARD_PLACEHOLDER_ENTRIES("leaderboard-placeholder-entries", 10, "The number of entries to keep updated in the leaderboard placeholder", "Only a certain number of leaderboard entries can be available at a time", "Accessing an entry that does not exist will display a placeholder message instead"),
        LEADERBOARD_PLACEHOLDER_REFRESH_INTERVAL("leaderboard-placeholder-refresh-interval", 15, "The number of seconds between leaderboard placeholder updates"),
        STARTING_BALANCE("starting-balance", 0, "The amount of points new players will start with"),
        VOTE("vote", null, "Votifier hook settings"),
        VOTE_ENABLED("vote.enabled", false, "If the votifier hook should be enabled"),
        VOTE_AMOUNT("vote.amount", 100, "How many points should be awarded per vote"),
        VOTE_ONLINE("vote.online", false, "Should points only be awarded when the player who voted is online?"),
        BASE_COMMAND_REDIRECT("base-command-redirect", "", "Which command should we redirect to when using '/points' with no subcommand specified?", "You can use a value here such as 'me' to show the output of '/points me'", "If you have any aliases defined, do not use them here", "If left as blank, the default behavior of showing '/points version' with bypassed permissions will be used"),
        LEGACY_DATABASE("legacy-database-mode", null, "Are you upgrading from a much older version of PlayerPoints?", "If you have done anything special with the database settings previously, you may need this", "WARNING: This setting may be removed in the future. Try to get your database updated to use the new format!"),
        LEGACY_DATABASE_MODE("legacy-database-mode.enabled", false, "Should we use legacy database mode?"),
        LEGACY_DATABASE_NAME("legacy-database-mode.table-name", "playerpoints", "The name of the legacy database table");

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return PlayerPoints.getInstance().getManager(ConfigurationManager.class).getConfig();
        }
    }

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }

    @Override
    protected String[] getHeader() {
        return new String[] {
                "__________ __                           __________       __        __",
                "\\______   \\  | _____  ___ __  __________\\______   \\____ |__| _____/  |_  ______",
                " |     ___/  | \\__  \\<   |  |/ __ \\_  __ \\     ___/  _ \\|  |/    \\   __\\/  ___/",
                " |    |   |  |__/ __ \\\\___  \\  ___/|  | \\/    |  (  <_> )  |   |  \\  |  \\___ \\",
                " |____|   |____(____  / ____|\\___  >__|  |____|   \\____/|__|___|  /__| /____  >",
                "                    \\/\\/         \\/                             \\/          \\/"
        };
    }

}
