/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package me.zoltiks.business.plugin.gui.realization;

import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.business.ActorBusiness;
import me.zoltiks.business.plugin.business.Business;
import me.zoltiks.business.plugin.business.BusinessLevel;
import me.zoltiks.business.plugin.gui.Gui;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.zoltiks.business.Main.instance;

public class BusinessUpConfirm
extends Gui {
    private Actor actor;
    private Business business;
    private ActorBusiness actorBusiness;
    private int infoSlot = Util.guiXYSlot(instance.getConfig().getInt("confirm_gui.info.position.x"), instance.getConfig().getInt("confirm_gui.info.position.y"));
    private int cancelSlot = Util.guiXYSlot(instance.getConfig().getInt("confirm_gui.cancel.position.x"), instance.getConfig().getInt("confirm_gui.cancel.position.y"));
    private int acceptSlot = Util.guiXYSlot(instance.getConfig().getInt("confirm_gui.accept.position.x"), instance.getConfig().getInt("confirm_gui.accept.position.y"));

    public BusinessUpConfirm(Player player, String title, int size, Business business, ActorBusiness actorBusiness) {
        super(player, title, size);
        this.actor = Util.requiredNullableActor(player);
        if (this.actor == null) {
            return;
        }
        this.business = business;
        this.actorBusiness = actorBusiness;
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
        int slot = event.getSlot();
        final Player player = super.getPlayer();
        if (slot == this.cancelSlot) {
            super.getPlayer().closeInventory();
            return;
        }
        if (slot == this.acceptSlot) {
            if (this.actorBusiness == null) {
                final double price = this.business.getBusinessLevelList().get(0).getPrice();
                if (!instance.getEconomy().has((OfflinePlayer)player, price)) {
                    Util.sendMessageFromConfigurationPath((CommandSender)player, "message.not_enough_money", false, (Map<String, String>)new HashMap<String, String>(){
                        {
                            this.put("%required_money_amount%", Util.format(price));
                            this.put("%current_money_amount%", Util.format(instance.getEconomy().getBalance((OfflinePlayer)player)));
                        }
                    });
                } else {
                    instance.getEconomy().withdrawPlayer((OfflinePlayer)player, price);
                    this.actor.getActorBusinessList().add(new ActorBusiness(this.actor, this.business, 1));
                    player.playSound(player.getLocation(), "thanks_for_buy", 1000, 1);
                    Util.sendMessageFromConfigurationPath((CommandSender)player, "message.business_bought", false, (Map<String, String>)new HashMap<String, String>(){
                        {
                            this.put("%business_display_name%", BusinessUpConfirm.this.business.getItemStackVisualization().getItemMeta().getDisplayName());
                        }
                    });
                    player.closeInventory();
                    player.sendTitle((Object)ChatColor.GREEN + "\u041a\u0443\u043f\u043b\u0435\u043d\u043e", "", 20, 20, 20);
                }
            } else {
                int businessLevel = this.actorBusiness.getLevel();
                if (businessLevel >= this.business.getBusinessLevelList().size()) {
                    Util.sendMessageFromConfigurationPath((CommandSender)player, "message.business_has_max_level", false);
                } else {
                    final BusinessLevel businessLevel1 = this.business.getBusinessLevelList().get(businessLevel);
                    if (!instance.getEconomy().has((OfflinePlayer)player, businessLevel1.getPrice())) {
                        player.playSound(player.getLocation(), "no_money", 1000, 1);
                        Util.sendMessageFromConfigurationPath((CommandSender)player, "message.not_enough_money", false, (Map<String, String>)new HashMap<String, String>(){
                            {
                                this.put("%required_money_amount%", Util.format(businessLevel1.getPrice()));
                                this.put("%current_money_amount%", Util.format(instance.getEconomy().getBalance((OfflinePlayer)player)));
                            }
                        });
                    } else {
                        instance.getEconomy().withdrawPlayer((OfflinePlayer)player, businessLevel1.getPrice());
                        player.playSound(player.getLocation(), "thanks_for_buy", 1000, 1);
                        this.actorBusiness.setLevel(businessLevel + 1);
                        Util.sendMessageFromConfigurationPath((CommandSender)player, "message.business_level_up", false, (Map<String, String>)new HashMap<String, String>(){
                            {
                                this.put("%business_display_name%", BusinessUpConfirm.this.business.getItemStackVisualization().getItemMeta().getDisplayName());
                                this.put("%new_level%", String.valueOf(BusinessUpConfirm.this.actorBusiness.getLevel()));
                            }
                        });
                    }
                }
            }
            super.getInventory().clear();
            super.getInventory().setItem(this.infoSlot, this.getInfoItemStack());
            super.getInventory().setItem(this.cancelSlot, this.getCancelItemStack());
            super.getInventory().setItem(this.acceptSlot, this.getAcceptItemStack());
            this.update();
            return;
        }
        this.update();
    }

    @Override
    public void update() {
        super.getInventory().clear();
        super.getInventory().setItem(this.infoSlot, this.getInfoItemStack());
        super.getInventory().setItem(this.cancelSlot, this.getCancelItemStack());
        super.getInventory().setItem(this.acceptSlot, this.getAcceptItemStack());
    }

    private ItemStack getCancelItemStack() {
        return Util.parseConfigItemStack("confirm_gui.cancel.item");
    }

    private ItemStack getAcceptItemStack() {
        return Util.parseConfigItemStack("confirm_gui.accept.item");
    }

    private ItemStack getInfoItemStack() {
        if (this.actorBusiness == null) {
            final BusinessLevel businessLevel = this.business.getBusinessLevelList().get(0);
            return this.business(instance.getConfig().getStringList("confirm_gui.new_business.lore"), (Map<String, String>)new HashMap<String, String>(){
                {
                    this.put("%level%", Util.format(businessLevel.getLevel()));
                    this.put("%max_level%", String.valueOf(BusinessUpConfirm.this.business.getBusinessLevelList().size()));
                    this.put("%income%", Util.format(businessLevel.getIncome()));
                    this.put("%income_delay%", String.valueOf(BusinessUpConfirm.this.business.getIncomeDefaultDelay()));
                    this.put("%price%", Util.format(businessLevel.getPrice()));
                }
            });
        }
        final int businessLevel = this.actorBusiness.getLevel();
        if (businessLevel >= this.business.getBusinessLevelList().size()) {
            final BusinessLevel businessLevel1 = this.business.getBusinessLevelList().get(this.business.getBusinessLevelList().size() - 1);
            return this.business(instance.getConfig().getStringList("confirm_gui.max_business.lore"), (Map<String, String>)new HashMap<String, String>(){
                {
                    this.put("%level%", String.valueOf(businessLevel1.getLevel()));
                    this.put("%max_level%", String.valueOf(BusinessUpConfirm.this.business.getBusinessLevelList().size()));
                    this.put("%income%", Util.format(businessLevel1.getIncome()));
                    this.put("%income_delay%", String.valueOf(BusinessUpConfirm.this.business.getIncomeDefaultDelay()));
                }
            });
        }
        final BusinessLevel nextBusinessLevel = this.business.getBusinessLevelList().get(businessLevel);
        return this.business(instance.getConfig().getStringList("confirm_gui.upgrade_business.lore"), (Map<String, String>)new HashMap<String, String>(){
            {
                this.put("%level%", String.valueOf(nextBusinessLevel.getLevel() - 1));
                this.put("%max_level%", String.valueOf(BusinessUpConfirm.this.business.getBusinessLevelList().size()));
                this.put("%next_level%", String.valueOf(nextBusinessLevel.getLevel()));
                this.put("%current_income%", Util.format(BusinessUpConfirm.this.business.getBusinessLevelList().get(businessLevel - 1).getIncome()));
                this.put("%next_income%", Util.format(nextBusinessLevel.getIncome()));
                this.put("%income_delay%", String.valueOf(BusinessUpConfirm.this.business.getIncomeDefaultDelay()));
                this.put("%price%", Util.format(nextBusinessLevel.getPrice()));
            }
        });
    }

    private ItemStack business(List<String> lore, Map<String, String> placeholders) {
        ItemStack itemStack = this.business.getItemStackVisualization().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        ArrayList<String> stringList = new ArrayList<String>();
        for (int i = 0; i < lore.size(); ++i) {
            stringList.add(this.injectPlaceholders(lore.get(i), placeholders));
        }
        itemMeta.setLore(stringList);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private String injectPlaceholders(String string, Map<String, String> placeholders) {
        for (Map.Entry<String, String> stringStringEntry : placeholders.entrySet()) {
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            string = ChatColor.translateAlternateColorCodes((char)'&', (String)string.replace(key, value));
        }
        return string;
    }
}

