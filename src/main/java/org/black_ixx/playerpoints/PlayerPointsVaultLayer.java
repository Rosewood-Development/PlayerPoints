package org.black_ixx.playerpoints;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.black_ixx.playerpoints.util.PointsUtils;
import org.bukkit.OfflinePlayer;

/**
 * Vault economy layer for PlayerPoints.
 *
 * @author Mitsugaru
 */
public class PlayerPointsVaultLayer implements Economy {

    private final PlayerPoints plugin;
    private final LocaleManager localeManager;

    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public PlayerPointsVaultLayer(PlayerPoints plugin) {
        this.plugin = plugin;
        this.localeManager = plugin.getManager(LocaleManager.class);
    }

    @Override
    public boolean isEnabled() {
        return this.plugin.isEnabled();
    }

    @Override
    public String getName() {
        return this.plugin.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        StringBuilder sb = new StringBuilder();
        int points = (int) amount;
        sb.append(points).append(" ");
        if (points == 1) {
            sb.append(this.currencyNameSingular());
        } else {
            sb.append(this.currencyNamePlural());
        }
        return sb.toString();
    }

    @Override
    public String currencyNamePlural() {
        return this.localeManager.getLocaleMessage("currency-plural");
    }

    @Override
    public String currencyNameSingular() {
        return this.localeManager.getLocaleMessage("currency-singular");
    }

    @Override
    public boolean hasAccount(String playerName) {
        boolean has = false;
        UUID id = this.handleTranslation(playerName);
        if (id != null) {
            has = this.plugin.getManager(DataManager.class).playerEntryExists(id).join();
        }
        return has;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return this.hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        return this.plugin.getAPI().lookAsync(this.handleTranslation(playerName)).join();
    }

    @Override
    public double getBalance(String playerName, String world) {
        return this.getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        int current = this.plugin.getAPI().lookAsync(this.handleTranslation(playerName)).join();
        return current >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return this.has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        int points = (int) amount;
        boolean result = this.plugin.getAPI().takeAsync(this.handleTranslation(playerName), points).join();
        int balance = this.plugin.getAPI().lookAsync(this.handleTranslation(playerName)).join();

        EconomyResponse response;
        if (result) {
            response = new EconomyResponse(amount, balance,
                    ResponseType.SUCCESS, null);
        } else {
            response = new EconomyResponse(amount, balance,
                    ResponseType.FAILURE, "Lack funds");
        }
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName,
                                          double amount) {
        return this.withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        int points = (int) amount;

        boolean result = this.plugin.getAPI().giveAsync(this.handleTranslation(playerName), points).join();
        int balance = this.plugin.getAPI().lookAsync(this.handleTranslation(playerName)).join();

        EconomyResponse response;
        if (result) {
            response = new EconomyResponse(amount, balance,
                    ResponseType.SUCCESS, null);
        } else {
            response = new EconomyResponse(amount, balance,
                    ResponseType.FAILURE, null);
        }
        return response;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return this.depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        // Assume true as the storage handler will dynamically add players.
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return this.createPlayerAccount(playerName);
    }

    private UUID handleTranslation(String name) {
        try {
            return UUID.fromString(name);
        } catch (IllegalArgumentException e) {
            return PointsUtils.getPlayerByName(name).getUniqueId();
        }
    }

    @Override
    public EconomyResponse createBank(String bank, OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        // Assume true as the storage handler will dynamically add players.
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String world) {
        // Assume true as the storage handler will dynamically add players.
        return true;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        int points = (int) amount;
        boolean result = this.plugin.getAPI().giveAsync(player.getUniqueId(), points).join();
        int balance = this.plugin.getAPI().lookAsync(player.getUniqueId()).join();

        EconomyResponse response;
        if (result) {
            response = new EconomyResponse(amount, balance,
                    ResponseType.SUCCESS, null);
        } else {
            response = new EconomyResponse(amount, balance,
                    ResponseType.FAILURE, null);
        }
        return response;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
        return this.depositPlayer(player, amount);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return this.plugin.getAPI().lookAsync(player.getUniqueId()).join();
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return this.getBalance(player);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return this.plugin.getAPI().lookAsync(player.getUniqueId()).join() >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String world, double amount) {
        return this.has(player, amount);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return this.plugin.getManager(DataManager.class).playerEntryExists(player.getUniqueId()).join();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String world) {
        return this.hasAccount(player);
    }

    @Override
    public EconomyResponse isBankMember(String bank, OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public EconomyResponse isBankOwner(String bank, OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        int points = (int) amount;
        boolean result = this.plugin.getAPI().takeAsync(player.getUniqueId(), points).join();
        int balance = this.plugin.getAPI().lookAsync(player.getUniqueId()).join();

        EconomyResponse response;
        if (result) {
            response = new EconomyResponse(amount, balance, ResponseType.SUCCESS, null);
        } else {
            response = new EconomyResponse(amount, balance, ResponseType.FAILURE, "Lack funds");
        }
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
        return this.withdrawPlayer(player, amount);
    }

}
