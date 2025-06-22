/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package me.zoltiks.business.plugin.business.event;

import me.zoltiks.business.plugin.business.ActorBusiness;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BusinessIncomeEvent
        extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final ActorBusiness actorBusiness;

    public BusinessIncomeEvent(ActorBusiness actorBusiness) {
        this.actorBusiness = actorBusiness;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ActorBusiness getActorBusiness() {
        return this.actorBusiness;
    }
}

