package me.zoltiks.business.plugin.actor;

import me.socrum.advanced.ini.Initer;
import me.zoltiks.business.plugin.actor.event.ActorLoadedEvent;
import me.zoltiks.business.plugin.actor.event.ActorUnloadEvent;
import me.zoltiks.business.plugin.database.Database;
import me.zoltiks.business.plugin.database.YamlDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static me.zoltiks.business.Main.ini;

public class ActorManager implements Initer {

    private final Map<Player, Actor> playerActorMap;
    private final ActorListener actorListener;

    public ActorManager() {
        this.playerActorMap = new HashMap<>();
        this.actorListener = new ActorListener(this);
    }

    @Override
    public void init() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.add(player);
        }
        this.actorListener.init();
    }

    @Override
    public void destroy() {
        this.actorListener.destroy();
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.del(player);
        }
    }

    public void add(Player player) {
        Database database = ini.get(YamlDatabase.class);
        Actor actor = database.load(player.getName());
        this.playerActorMap.put(player, actor);
        Bukkit.getPluginManager().callEvent(new ActorLoadedEvent(actor));
    }

    public void del(Player player) {
        Actor actor = this.playerActorMap.get(player);
        Bukkit.getPluginManager().callEvent(new ActorUnloadEvent(actor));
        Database database = ini.get(YamlDatabase.class);
        database.save(actor);
        this.playerActorMap.remove(player);
    }

    public Map<Player, Actor> getPlayerActorMap() {
        return playerActorMap;
    }

    public ActorListener getActorListener() {
        return this.actorListener;
    }

}

