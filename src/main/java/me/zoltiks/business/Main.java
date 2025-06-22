package me.zoltiks.business;

import me.socrum.advanced.ini.Ini;
import me.zoltiks.business.plugin.Plugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    public static Main instance;
    public static Ini ini;

    // dependency
    private Economy economy;

    public void onEnable() {
        instance = this;
        ini = new Ini();
        this.setupEconomy();
        refresh();
    }

    public void onDisable() {
        ini.destroy();
    }

    public void refresh() {
        File f = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!f.exists()) this.saveDefaultConfig();
        this.reloadConfig();

        HandlerList.unregisterAll(this); // unregister all events for this plugin

        ini.destroy();
        ini.put(new Plugin());
        ini.init();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = rsp.getProvider();
        return this.economy != null;
    }

    public static Main getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public void setEconomy(Economy economy) {
        this.economy = economy;
    }

}

