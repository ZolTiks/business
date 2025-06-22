/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package me.zoltiks.business.plugin.gui;

import me.socrum.advanced.ini.Initer;
import me.socrum.minecraft.spigot.plugin.utilm.Updatable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Gui
        implements Initer,
        Updatable {
    private final Player player;
    private final Inventory inventory;

    public Gui(Player player, String title, int size) {
        this.player = player;
        this.inventory = Bukkit.createInventory((InventoryHolder) this.player, (int) size, (String) ChatColor.translateAlternateColorCodes((char) '&', (String) title));
        this.player.openInventory(this.inventory);
    }

    protected abstract void click(InventoryClickEvent var1);

    public Player getPlayer() {
        return this.player;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}

