package me.zoltiks.business.plugin.business;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.socrum.advanced.util.UtilA;
import me.socrum.minecraft.spigot.plugin.utilm.Updatable;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.actor.ActorManager;
import me.zoltiks.business.plugin.business.event.BusinessIncomeEvent;
import me.zoltiks.business.plugin.holograms.DNAPI;
import me.zoltiks.business.plugin.multiholo.MultiHolo;
import me.zoltiks.business.plugin.multiholo.MultiHoloManager;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class Business implements Updatable {

    private int id;
    private ItemStack itemStackVisualization;
    private List<BusinessLevel> businessLevelList;
    private int incomeDelay;
    private int incomeDefaultDelay;
    private int hologramX;
    private int hologramY;
    private int hologramZ;
    private MultiHolo multiHolo;

    public Business(int id, ItemStack itemStackVisualization, List<BusinessLevel> businessLevelList, int incomeDelay, int hologramX, int hologramY, int hologramZ) {
        this.id = id;
        this.itemStackVisualization = itemStackVisualization;
        this.businessLevelList = businessLevelList;
        this.incomeDelay = incomeDelay;
        this.incomeDefaultDelay = incomeDelay;
        this.hologramX = hologramX;
        this.hologramY = hologramY;
        this.hologramZ = hologramZ;

        Location hologramLocation = new Location(Bukkit.getWorld("world"), this.hologramX, this.hologramY + 0.5, this.hologramZ);
        this.multiHolo = ini.get(MultiHoloManager.class).createMultiHolo(hologramLocation);
    }

    @Override
    public void update() {
        if (--this.incomeDelay <= 0) {
            this.incomeDelay = this.incomeDefaultDelay;
            ActorManager actorManager = ini.get(ActorManager.class);
            Map<Player, Actor> playerActorHashMap = actorManager.getPlayerActorMap();
            playerActorHashMap.forEach((player, actor) -> {
                List<ActorBusiness> actorBusinessList = actor.getActorBusinessList();
                for (ActorBusiness actorBusiness : actorBusinessList) {
                    if (!actorBusiness.getBusiness().equals(this)) continue;
                    BusinessIncomeEvent businessIncomeEvent = new BusinessIncomeEvent(actorBusiness);
                    Bukkit.getPluginManager().callEvent(businessIncomeEvent);
                }
            });
        }

        ActorManager actorManager = ini.get(ActorManager.class);
        Map<Player, Actor> playerActorHashMap = actorManager.getPlayerActorMap();
        for (Map.Entry<Player, Actor> playerActorEntry : playerActorHashMap.entrySet()) {
            Player player = playerActorEntry.getKey();
            Hologram hologram = this.multiHolo.getPlayerHologramMap().get(player);
            if (hologram == null) continue;

            Actor actor = Util.requiredNullableActor(player);
            ActorBusiness actorBusiness = null;
            List<ActorBusiness> actorBusinessList = actor.getActorBusinessList();
            for (ActorBusiness actorBusiness1 : actorBusinessList) {
                if (this.getId() != actorBusiness1.getBusiness().getId()) continue;
                actorBusiness = actorBusiness1;
                break;
            }

            int businessLevel = actorBusiness == null ? 0 : actorBusiness.getLevel();
            boolean maxLevel = businessLevel >= this.getBusinessLevelList().size();
            boolean notBuyed = actorBusiness == null;

            FileConfiguration fileConfiguration = instance.getConfig();
            ConfigurationSection hologramConfigurationSection = fileConfiguration.getConfigurationSection("hologram");

            if (notBuyed) {
                List<String> stringList = UtilA.placeholders(hologramConfigurationSection.getStringList("not_buyed"), new HashMap<String, String>() {{
                    put("%id%", String.valueOf(id));
                }});
                DNAPI.setHologramLines(hologram, stringList);
                continue;
            }

            if (maxLevel) {
                List<String> stringList = UtilA.placeholders(hologramConfigurationSection.getStringList("max"), new HashMap<String, String>() {{
                    put("%id%", String.valueOf(id));
                }});
                DNAPI.setHologramLines(hologram, stringList);
            } else {
                List<String> stringList = UtilA.placeholders(hologramConfigurationSection.getStringList("buyed"), new HashMap<String, String>() {{
                    put("%id%", String.valueOf(id));
                }});
                DNAPI.setHologramLines(hologram, stringList);
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemStack getItemStackVisualization() {
        return this.itemStackVisualization;
    }

    public void setItemStackVisualization(ItemStack itemStackVisualization) {
        this.itemStackVisualization = itemStackVisualization;
    }

    public List<BusinessLevel> getBusinessLevelList() {
        return this.businessLevelList;
    }

    public void setBusinessLevelList(List<BusinessLevel> businessLevelList) {
        this.businessLevelList = businessLevelList;
    }

    public int getIncomeDelay() {
        return this.incomeDelay;
    }

    public void setIncomeDelay(int incomeDelay) {
        this.incomeDelay = incomeDelay;
    }

    public int getIncomeDefaultDelay() {
        return this.incomeDefaultDelay;
    }

    public void setIncomeDefaultDelay(int incomeDefaultDelay) {
        this.incomeDefaultDelay = incomeDefaultDelay;
    }

    public int getHologramX() {
        return hologramX;
    }

    public void setHologramX(int hologramX) {
        this.hologramX = hologramX;
    }

    public int getHologramY() {
        return hologramY;
    }

    public void setHologramY(int hologramY) {
        this.hologramY = hologramY;
    }

    public int getHologramZ() {
        return hologramZ;
    }

    public void setHologramZ(int hologramZ) {
        this.hologramZ = hologramZ;
    }

    public MultiHolo getMultiHolo() {
        return multiHolo;
    }

    public void setMultiHolo(MultiHolo multiHolo) {
        this.multiHolo = multiHolo;
    }

}

