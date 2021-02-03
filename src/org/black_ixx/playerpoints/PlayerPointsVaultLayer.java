package org.black_ixx.playerpoints;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.black_ixx.playerpoints.services.IModule;
import org.black_ixx.playerpoints.storage.StorageHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

/**
 * Vault economy layer for PlayerPoints.
 * 
 * @author Mitsugaru
 */
public class PlayerPointsVaultLayer implements Economy, IModule {

    /**
     * Plugin instance.
     */
    private final PlayerPoints plugin;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public PlayerPointsVaultLayer(PlayerPoints plugin) {
        this.plugin = plugin;
    }

    @Override
    public void starting() {
        // Set to low priority. Allow for other, standard economy plugins to
        // supercede ours.
        plugin.getServer().getServicesManager()
                .register(Economy.class, this, plugin, ServicePriority.Low);
    }

    @Override
    public void closing() {
        plugin.getServer().getServicesManager().unregister(Economy.class, this);
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getName() {
        return plugin.getName();
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
        if(points == 1) {
            sb.append(currencyNameSingular());
        } else {
            sb.append(currencyNamePlural());
        }
        return sb.toString();
    }

    @Override
    public String currencyNamePlural() {
        return "Points";
    }

    @Override
    public String currencyNameSingular() {
        return "Point";
    }

    @Override
    public boolean hasAccount(String playerName) {
    	boolean has = false;
    	UUID id = handleTranslation(playerName);
    	if(id != null) {
    		has = plugin.getModuleForClass(StorageHandler.class).playerEntryExists(id.toString());
    	}
        return has;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        return plugin.getAPI().look(handleTranslation(playerName));
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        int current = plugin.getAPI().look(handleTranslation(playerName));
        return current >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        int points = (int) amount;
        boolean result = plugin.getAPI().take(handleTranslation(playerName), points);
        int balance = plugin.getAPI().look(handleTranslation(playerName));

        EconomyResponse response;
        if(result) {
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
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        int points = (int) amount;
        boolean result = plugin.getAPI().give(handleTranslation(playerName), points);
        int balance = plugin.getAPI().look(handleTranslation(playerName));

        EconomyResponse response;
        if(result) {
            response = new EconomyResponse(amount, balance,
                    ResponseType.SUCCESS, null);
        } else {
            response = new EconomyResponse(amount, balance,
                    ResponseType.FAILURE, null);
        }
        return response;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName,
            double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
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
        return createPlayerAccount(playerName);
    }
    
    private UUID handleTranslation(String name) {
        UUID id = null;
        try {
            UUID.fromString(name);
        } catch(IllegalArgumentException e) {
            id = plugin.translateNameToUUID(name);
        }
        return id;
    }

	@Override
	public EconomyResponse createBank(String bank, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
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
        boolean result = plugin.getAPI().give(player.getUniqueId(), points);
        int balance = plugin.getAPI().look(player.getUniqueId());

        EconomyResponse response;
        if(result) {
            response = new EconomyResponse(amount, balance,
                    ResponseType.SUCCESS, null);
        } else {
            response = new EconomyResponse(amount, balance,
                    ResponseType.FAILURE, null);
        }
        return response;
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String world,
			double amount) {
		return depositPlayer(player, amount);
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return plugin.getAPI().look(player.getUniqueId());
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		return getBalance(player);
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		int current = plugin.getAPI().look(player.getUniqueId());
        return current >= amount;
	}

	@Override
	public boolean has(OfflinePlayer player, String world, double amount) {
		return has(player, amount);
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return plugin.getModuleForClass(StorageHandler.class).playerEntryExists(player.getUniqueId().toString());
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String world) {
		return hasAccount(player);
	}

	@Override
	public EconomyResponse isBankMember(String bank, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
	}

	@Override
	public EconomyResponse isBankOwner(String bank, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		int points = (int) amount;
        boolean result = plugin.getAPI().take(player.getUniqueId(), points);
        int balance = plugin.getAPI().look(player.getUniqueId());

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
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String world,
			double amount) {
		return withdrawPlayer(player, amount);
	}
}