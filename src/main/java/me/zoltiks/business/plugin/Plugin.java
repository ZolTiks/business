package me.zoltiks.business.plugin;

import me.socrum.advanced.ini.Initer;
import me.zoltiks.business.plugin.actor.ActorManager;
import me.zoltiks.business.plugin.booster.BoosterManager;
import me.zoltiks.business.plugin.business.BusinessManager;
import me.zoltiks.business.plugin.cloth.ClothManager;
import me.zoltiks.business.plugin.command.CommandManager;
import me.zoltiks.business.plugin.database.YamlDatabase;
import me.zoltiks.business.plugin.gui.GuiManager;
import me.zoltiks.business.plugin.miningblock.MiningBlockManager;
import me.zoltiks.business.plugin.multiholo.MultiHoloManager;
import me.zoltiks.business.plugin.offlineincome.OfflineIncomeManager;
import me.zoltiks.business.plugin.scheduler.Scheduler;

import static me.zoltiks.business.Main.ini;

public class Plugin implements Initer {

    public Plugin() {
        ini.put(new MultiHoloManager());
        ini.put(new YamlDatabase());
        ini.put(new MiningBlockManager());
        ini.put(new ClothManager());
        ini.put(new BusinessManager());
        ini.put(new OfflineIncomeManager());
        ini.put(new ActorManager());
        ini.put(new BoosterManager());
        ini.put(new GuiManager());
        ini.put(new CommandManager());
        ini.put(new Scheduler());
        ini.put(new PAPI());
    }

}

