package org.black_ixx.playerpoints.listeners;

import com.vexsoftware.votifier.model.VotifierEvent;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.ConfigurationManager.Setting;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VotifierListener implements Listener {

    private final PlayerPoints plugin;

    public VotifierListener(PlayerPoints plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void vote(VotifierEvent event) {
        if (event.getVote().getUsername() == null) {
            return;
        }
        String name = event.getVote().getUsername();
        OfflinePlayer offlinePlayer = PointsUtils.getPlayerByName(name);
        int amount = Setting.VOTE_AMOUNT.getInt();
        boolean pay = false;
        if (Setting.VOTE_ONLINE.getBoolean()) {
            Player player = offlinePlayer.getPlayer();
            if (player != null && player.isOnline()) {
                pay = true;
                this.plugin.getManager(LocaleManager.class).sendMessage(player, "votifier-voted", StringPlaceholders.builder("service", event.getVote().getServiceName())
                        .addPlaceholder("amount", Setting.VOTE_AMOUNT.getInt())
                        .build());
            }
        } else {
            pay = true;
        }
        if (pay) {
            this.plugin.getAPI().give(offlinePlayer.getUniqueId(), amount);
        }
    }
}
