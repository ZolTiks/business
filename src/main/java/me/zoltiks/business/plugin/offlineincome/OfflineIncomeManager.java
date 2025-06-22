package me.zoltiks.business.plugin.offlineincome;

import me.socrum.advanced.ini.Initer;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class OfflineIncomeManager implements Initer {

    public OfflineIncomeManager() {
        UtilM.registerClassAsListener(instance, ini.put(new OfflineIncomeListener()));
    }

}
