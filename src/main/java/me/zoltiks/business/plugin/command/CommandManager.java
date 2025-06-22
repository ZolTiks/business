package me.zoltiks.business.plugin.command;

import me.socrum.advanced.ini.Initer;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class CommandManager implements Initer {

    public CommandManager() {
        UtilM.registerClassAsCommand(instance, "business", ini.put(new Command()));
    }

}

