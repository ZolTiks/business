package me.zoltiks.business.plugin.gui.realization;

import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.business.ActorBusiness;
import me.zoltiks.business.plugin.business.Business;
import me.zoltiks.business.plugin.gui.Gui;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.zoltiks.business.Main.instance;

public class MyBusinessList extends Gui {

    private Actor actor;

    public MyBusinessList(Player player, String title, int size) {
        super(player, title, size);
        this.actor = Util.requiredNullableActor(player);
        if (this.actor == null) {
            return;
        }
    }

    @Override
    public void init() {
        this.update();
    }

    @Override
    public void destroy() {
    }

    @Override
    protected void click(InventoryClickEvent event) {
        event.setCancelled(true);

        InventoryView inventoryView = event.getView();
        Inventory topInventory = inventoryView.getTopInventory();
        int slot = event.getSlot();
        ItemStack itemStack = topInventory.getItem(slot);

        if (!(itemStack == null || itemStack.getType() == Material.AIR)) {
            // телепортировать если есть разрешение
            List<ActorBusiness> actorBusinessList = this.actor.getActorBusinessList();
            for (int i = 0; i < actorBusinessList.size(); i++) {
                if (i != slot) continue;
                ActorBusiness actorBusiness = actorBusinessList.get(i);
                Business business = actorBusiness.getBusiness();
                Player player = this.getPlayer();

                // business.teleportation.N
                if (!player.hasPermission("business.teleportation." + business.getId())) {
                    UtilM.sendMessageFromConfigurationPath(instance, player, "message.teleportation_to_business_location_denied");
                    return;
                }

                int x = business.getHologramX();
                int y = business.getHologramY();
                int z = business.getHologramZ();
                Location location = new Location(Bukkit.getWorld("world"), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());

                player.teleport(location);
                UtilM.sendMessageFromConfigurationPath(instance, this.getPlayer(), "message.teleported_to_business_location");
                return;
            }
        }

        this.update();
    }

    @Override
    public void update() {
        super.getInventory().clear();
        List<ActorBusiness> actorBusinessList = this.actor.getActorBusinessList();
        for (int i = 0; i < actorBusinessList.size(); ++i) {
            ActorBusiness actorBusiness = actorBusinessList.get(i);
            ItemStack itemStack = actorBusiness.getItemStackVisualization();

            // добавить в описание координаты бизнеса
            Business business = actorBusiness.getBusiness();
            int x = business.getHologramX();
            int y = business.getHologramY();
            int z = business.getHologramZ();

            itemStack = itemStack.clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> stringList = new ArrayList<>(itemMeta.getLore());
            stringList.add("&8Координаты: &eX:&b" + x + " &eY:&b" + y + " &eZ:&b" + z);
            stringList.add("");
            stringList.add("&eКликните чтобы телепортироваться!");
            itemMeta.setLore(UtilM.colorize(stringList));
            itemStack.setItemMeta(itemMeta);

            super.getInventory().setItem(i, itemStack);
        }
    }

}

