package me.zoltiks.business.plugin.scheduler;

import me.socrum.advanced.ini.Initer;
import me.zoltiks.business.plugin.actor.ActorManager;
import me.zoltiks.business.plugin.business.BusinessManager;
import org.bukkit.scheduler.BukkitRunnable;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class Scheduler implements Initer {

    private final BukkitRunnable bukkitRunnable;

    public Scheduler() {
        this.bukkitRunnable = new BukkitRunnable() {
            public void run() {
                ini.get(BusinessManager.class).update();
            }
        };
    }

    @Override
    public void init() {
        this.bukkitRunnable.runTaskTimer(instance, 20L, 20L);
    }

    @Override
    public void destroy() {
        this.bukkitRunnable.cancel();
    }
}

