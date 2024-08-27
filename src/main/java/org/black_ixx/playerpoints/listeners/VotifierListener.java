package org.black_ixx.playerpoints.listeners;

import com.vexsoftware.votifier.model.VotifierEvent;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.setting.SettingKey;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
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
        if (event.getVote().getUsername() == null)
            return;

        String name = event.getVote().getUsername();
        PointsUtils.getPlayerByName(name, playerInfo -> {
            if (playerInfo == null)
                return;

            int amount = SettingKey.VOTE_AMOUNT.get();
            Player player = Bukkit.getPlayer(playerInfo.getFirst());

            if (!SettingKey.VOTE_ONLINE.get() || player != null) {
                this.plugin.getAPI().give(playerInfo.getFirst(), amount);
                if (player != null)
                    this.plugin.getManager(LocaleManager.class).sendMessage(player, "votifier-voted", StringPlaceholders.builder("service", event.getVote().getServiceName())
                            .add("amount", SettingKey.VOTE_AMOUNT.get())
                            .build());
            }
        });
    }
}
