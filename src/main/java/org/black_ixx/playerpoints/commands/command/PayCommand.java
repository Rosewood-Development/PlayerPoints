package org.black_ixx.playerpoints.commands.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.models.Tuple;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PayCommand extends RoseCommand {

    public PayCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, String player, Integer amount) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        final PlayerPoints plugin = (PlayerPoints) this.rosePlugin;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Tuple<UUID, String> target = PointsUtils.getPlayerByName(player);

            if (target == null) {
                locale.sendMessage(context.getSender(), "argument-handler-offline-player", StringPlaceholders.single("player", player));
                return;
            }


            if (amount <= 0) {
                locale.sendMessage(context.getSender(), "invalid-amount");
                return;
            }

            Player sender = (Player) context.getSender();
            if (plugin.getAPI().pay(sender.getUniqueId(), target.getFirst(), amount)) {
                // Send success message to sender
                locale.sendMessage(sender, "command-pay-sent", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                        .addPlaceholder("currency", locale.getCurrencyName(amount))
                        .addPlaceholder("player", target.getSecond())
                        .build());

                // Send success message to target
                Player onlinePlayer = Bukkit.getPlayer(target.getFirst());
                if (onlinePlayer != null) {
                    locale.sendMessage(onlinePlayer, "command-pay-received", StringPlaceholders.builder("amount", PointsUtils.formatPoints(amount))
                            .addPlaceholder("currency", locale.getCurrencyName(amount))
                            .addPlaceholder("player", sender.getName())
                            .build());
                }

            } else {
                locale.sendMessage(sender, "command-pay-lacking-funds", StringPlaceholders.single("currency", locale.getCurrencyName(0)));
            }
        });
    }

    @Override
    protected String getDefaultName() {
        return "pay";
    }

    @Override
    public String getDescriptionKey() {
        return "command-pay-description";
    }

    @Override
    public String getRequiredPermission() {
        return "playerpoints.pay";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
