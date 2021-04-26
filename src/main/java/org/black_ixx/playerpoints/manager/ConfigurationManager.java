package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import org.black_ixx.playerpoints.PlayerPoints;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        VAULT("vault", false, "Should register with Vault as a currency manager?"),
        LEADERBOARD_PER_PAGE("leaderboard-per-page", 10, "How many players should be displayed per page on the leaderboard?"),
        VOTE("vote", null, "Votifier hook settings"),
        VOTE_ENABLED("vote.enabled", false, "If the votifier hook should be enabled"),
        VOTE_AMOUNT("vote.amount", 100, "How many points should be awarded per vote"),
        VOTE_ONLINE("vote.online", false, "Should points only be awarded when the player who voted is online?");

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
