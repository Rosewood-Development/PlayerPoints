package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.plugin.ServicePriority;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        VAULT("vault", false, "Should register with Vault as a currency manager?"),
        VAULT_PRIORITY("vault-priority", "Low", "The priority level to use for the Vault hook", "Higher priorities will allow PlayerPoints to load before other economy plugins", "Valid values: [" + Arrays.stream(ServicePriority.values()).map(Enum::name).collect(Collectors.joining(", ")) + "]"),
        LEADERBOARD_PER_PAGE("leaderboard-per-page", 10, "How many players should be displayed per page on the leaderboard?"),
        CACHE_DURATION("cache-duration", 30, "How many seconds to hold a player's points in cache before being released"),
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
