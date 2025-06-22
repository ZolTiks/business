/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.plugin.Plugin
 */
package me.zoltiks.business.plugin.gui;

import me.socrum.advanced.ini.Initer;
import me.zoltiks.business.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class GuiListener
implements Initer,
Listener {
    private final GuiManager guiManager;

    public GuiListener(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @Override
    public void init() {
        Main.instance.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)Main.instance);
    }

    @Override
    public void destroy() {
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        InventoryView inventoryView = event.getView();
        Inventory topInventory = inventoryView.getTopInventory();
        HashMap<Player, Gui> playerGuiHashMap = this.guiManager.getPlayerGuiHashMap();
        Gui gui = playerGuiHashMap.get((Object)player);
        if (gui == null) {
            return;
        }
        if (!topInventory.equals((Object)gui.getInventory())) {
            return;
        }
        this.guiManager.close(player);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        InventoryView inventoryView = event.getView();
        Inventory topInventory = inventoryView.getTopInventory();
        if (event.getClickedInventory() == null) {
            return;
        }
        if (event.getSlot() < 0) {
            return;
        }
        HashMap<Player, Gui> playerGuiHashMap = this.guiManager.getPlayerGuiHashMap();
        Gui gui = playerGuiHashMap.get((Object)player);
        if (gui == null) {
            return;
        }
        if (!topInventory.equals((Object)gui.getInventory())) {
            return;
        }
        gui.click(event);
    }

    public GuiManager getGuiManager() {
        return this.guiManager;
    }
}

