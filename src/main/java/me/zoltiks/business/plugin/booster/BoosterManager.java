package me.zoltiks.business.plugin.booster;

import me.socrum.advanced.ini.Initer;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class BoosterManager implements Initer {

    public BoosterManager() {
        ini.put(new BoosterScheduler());
        UtilM.registerClassAsCommand(instance, "booster", ini.put(new BoosterCommand()));
    }

}
