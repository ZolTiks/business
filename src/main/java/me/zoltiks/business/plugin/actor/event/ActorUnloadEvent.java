package me.zoltiks.business.plugin.actor.event;

import me.zoltiks.business.plugin.actor.Actor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ActorUnloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Actor actor;

    public ActorUnloadEvent(Actor actor) {
        this.actor = actor;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

}

