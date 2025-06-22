package me.zoltiks.business.plugin.actor;

import me.socrum.advanced.ini.Initer;
import me.zoltiks.business.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ActorListener implements Initer, Listener {

    private final ActorManager actorManager;

    public ActorListener(ActorManager actorManager) {
        this.actorManager = actorManager;
    }

    @Override
    public void init() {
        Main.instance.getServer().getPluginManager().registerEvents(this, Main.instance);
    }

    @Override
    public void destroy() {
    }

    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.actorManager.add(player);
    }

    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.actorManager.del(player);
    }

    public ActorManager getActorManager() {
        return this.actorManager;
    }
}

