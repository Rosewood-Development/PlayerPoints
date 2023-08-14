package org.black_ixx.playerpoints.listeners;

import com.vexsoftware.votifier.model.VotifierEvent;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.UUID;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.ConfigurationManager.Setting;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.Tuple;
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
        PointsUtils.getPlayerByName(name).thenAccept(playerInfo -> {
            if (playerInfo == null)
                return;

            int amount = Setting.VOTE_AMOUNT.getInt();
            Player player = Bukkit.getPlayer(playerInfo.getFirst());

            if (!Setting.VOTE_ONLINE.getBoolean() || player != null) {
                this.plugin.getAPI().give(playerInfo.getFirst(), amount);
                if (player != null)
                    this.plugin.getManager(LocaleManager.class).sendMessage(player, "votifier-voted", StringPlaceholders.builder("service", event.getVote().getServiceName())
                            .addPlaceholder("amount", Setting.VOTE_AMOUNT.getInt())
                            .build());
            }
        });
    }
}
