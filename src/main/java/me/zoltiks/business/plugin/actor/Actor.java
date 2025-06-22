package me.zoltiks.business.plugin.actor;

import me.zoltiks.business.plugin.booster.Booster;
import me.zoltiks.business.plugin.business.ActorBusiness;
import org.bukkit.entity.Player;

import java.util.List;

public class Actor {
    private final Player player;
    private long lastExitTimestamp;
    private double businessIncomeMultiplier;
    private List<ActorBusiness> actorBusinessList;
    private List<Booster> boosterList;

    public Actor(Player player, long lastExitTimestamp, double businessIncomeMultiplier,
                 List<ActorBusiness> actorBusinessList, List<Booster> boosterList) {
        this.player = player;
        this.lastExitTimestamp = lastExitTimestamp;
        this.businessIncomeMultiplier = businessIncomeMultiplier;
        this.actorBusinessList = actorBusinessList;
        this.boosterList = boosterList;
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getLastExitTimestamp() {
        return lastExitTimestamp;
    }

    public void setLastExitTimestamp(long lastExitTimestamp) {
        this.lastExitTimestamp = lastExitTimestamp;
    }

    public double getBusinessIncomeMultiplier() {
        return this.businessIncomeMultiplier;
    }

    public void setBusinessIncomeMultiplier(double businessIncomeMultiplier) {
        this.businessIncomeMultiplier = businessIncomeMultiplier;
    }

    public List<ActorBusiness> getActorBusinessList() {
        return this.actorBusinessList;
    }

    public void setActorBusinessList(List<ActorBusiness> actorBusinessList) {
        this.actorBusinessList = actorBusinessList;
    }

    public List<Booster> getBoosterList() {
        return boosterList;
    }

    public void setBoosterList(List<Booster> boosterList) {
        this.boosterList = boosterList;
    }


}

