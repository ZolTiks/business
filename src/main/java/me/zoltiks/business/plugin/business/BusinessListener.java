package me.zoltiks.business.plugin.business;

import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import me.socrum.minecraft.spigot.plugin.utilm.advanced.ListenerAdvanced;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.business.event.BusinessIncomeEvent;
import me.zoltiks.business.plugin.gui.GuiManager;
import me.zoltiks.business.plugin.gui.realization.BusinessUpConfirm;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class BusinessListener implements ListenerAdvanced {

    @EventHandler
    private void onBusinessIncomeEvent(BusinessIncomeEvent event) {
        ActorBusiness actorBusiness = event.getActorBusiness();
        Actor actor = actorBusiness.getActor();
        Player player = actor.getPlayer();
        Business business = actorBusiness.getBusiness();

        double totalIncome = actorBusiness.getTotalIncome();
        instance.getEconomy().depositPlayer(player, totalIncome);

        UtilM.sendMessageFromConfigurationPath(instance, player, "message.salary_received", new HashMap<String, String>() {{
            put("%business_display_name%", business.getItemStackVisualization().getItemMeta().getDisplayName());
            put("%salary%", Util.format(totalIncome));
            put("%business_income_multiplier%", Util.number(actor.getBusinessIncomeMultiplier(), 2));
        }});
    }

    @EventHandler
    private void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        Location location = player.getLocation();

        BusinessManager businessManager = ini.get(BusinessManager.class);
        Map<Integer, Business> integerBusinessMap = businessManager.getIntegerBusinessMap();

        Actor actor = Util.requiredNullableActor(player);
        List<ActorBusiness> actorBusinessList = actor.getActorBusinessList();

        for (Map.Entry<Integer, Business> integerBusinessEntry : integerBusinessMap.entrySet()) {
            Business business = integerBusinessEntry.getValue();
            Location businessLocation = new Location(
                    Bukkit.getWorld("world"),
                    business.getHologramX(),
                    business.getHologramY(),
                    business.getHologramZ()
            );
            if (!location.getWorld().equals(businessLocation.getWorld())) continue;

            FileConfiguration fileConfiguration = instance.getConfig();
            int openMenuRadius = fileConfiguration.getInt("hologram.open_menu_radius");
            if (location.distance(businessLocation) > openMenuRadius) continue;

            ActorBusiness actorBusiness = null;
            for (ActorBusiness actorBusiness1 : actorBusinessList) {
                if (!business.equals(actorBusiness1.getBusiness())) continue;
                actorBusiness = actorBusiness1;
                break;
            }

            String title = ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("confirm_gui.title"));
            int size = instance.getConfig().getInt("confirm_gui.size");
            GuiManager guiManager = ini.get(GuiManager.class);
            guiManager.open(player, new BusinessUpConfirm(player, title, size, business, actorBusiness));
            return;
        }
    }

}

