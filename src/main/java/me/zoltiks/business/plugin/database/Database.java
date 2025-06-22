package me.zoltiks.business.plugin.database;

import me.socrum.advanced.ini.Initer;
import me.zoltiks.business.plugin.actor.Actor;

public interface Database extends Initer {

    public Actor load(String var1);

    public void save(Actor var1);
}

