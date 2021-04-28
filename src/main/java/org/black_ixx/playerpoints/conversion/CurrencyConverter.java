package org.black_ixx.playerpoints.conversion;

import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class CurrencyConverter {

    protected final RosePlugin rosePlugin;
    protected final Plugin plugin;

    public CurrencyConverter(RosePlugin rosePlugin, String pluginName) {
        this.rosePlugin = rosePlugin;
        this.plugin = Bukkit.getPluginManager().getPlugin(pluginName);
    }

    public boolean canConvert() {
        return this.plugin != null && this.plugin.isEnabled();
    }

    public abstract void convert();

}
