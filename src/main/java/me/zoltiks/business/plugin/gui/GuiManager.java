package me.zoltiks.business.plugin.gui;

import me.socrum.advanced.ini.Initer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GuiManager implements Initer {

    private final HashMap<Player, Gui> playerGuiHashMap;
    private final GuiListener guiListener;

    public GuiManager() {
        this.playerGuiHashMap = new HashMap();
        this.guiListener = new GuiListener(this);
    }

    @Override
    public void init() {
        this.guiListener.init();
    }

    @Override
    public void destroy() {
        this.guiListener.destroy();
        for (Player player : Bukkit.getOnlinePlayers()) player.closeInventory();
    }

    public void open(Player player, Gui gui) {
        this.playerGuiHashMap.put(player, gui);
        gui.init();
    }

    public void close(Player player) {
        Gui gui = this.playerGuiHashMap.get(player);
        if (gui == null) {
            return;
        }
        gui.destroy();
        this.playerGuiHashMap.remove(player);
    }

    public HashMap<Player, Gui> getPlayerGuiHashMap() {
        return this.playerGuiHashMap;
    }

    public GuiListener getGuiListener() {
        return this.guiListener;
    }

}

