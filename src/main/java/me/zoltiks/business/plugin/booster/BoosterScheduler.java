package me.zoltiks.business.plugin.booster;

import me.socrum.advanced.ini.Initer;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.actor.ActorManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class BoosterScheduler implements Initer {

    private final BukkitRunnable bukkitRunnable;

    public BoosterScheduler() {
        this.bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                ActorManager actorManager = ini.get(ActorManager.class);
                Map<Player, Actor> playerActorMap = actorManager.getPlayerActorMap();
                for (Map.Entry<Player, Actor> playerActorEntry : playerActorMap.entrySet()) {
                    Player player = playerActorEntry.getKey();
                    Actor actor = playerActorEntry.getValue();
                    List<Booster> boosterList = actor.getBoosterList();
                    for (int i = 0; i < boosterList.size(); i++) {
                        Booster booster = boosterList.get(i);
                        booster.update();
                        if (booster.getDurationInSeconds() <= 0) boosterList.remove(i);
                    }
                }
            }
        };
    }

    @Override
    public void init() {
        this.bukkitRunnable.runTaskTimer(instance, 0, 20);
    }

    @Override
    public void destroy() {
        this.bukkitRunnable.cancel();
    }

    public BukkitRunnable getBukkitRunnable() {
        return bukkitRunnable;
    }

}
