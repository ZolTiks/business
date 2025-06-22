package me.zoltiks.business.plugin.multiholo;

import me.socrum.advanced.ini.Initer;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class MultiHoloManager implements Initer {

    private final List<MultiHolo> multiHoloList;

    public MultiHoloManager() {
        this.multiHoloList = new ArrayList<>();
        UtilM.registerClassAsListener(instance, ini.put(new MultiHoloListener()));
    }

    @Override
    public void destroy() {
        for (MultiHolo multiHolo : this.multiHoloList) multiHolo.destroy();
    }

    // жизненный цикл мульти-голограммы обрабатывается самостоятельно
    public MultiHolo createMultiHolo(Location location) {
        MultiHolo multiHolo = new MultiHolo(location);
        multiHolo.init();
        this.multiHoloList.add(multiHolo);
        return multiHolo;
    }

    public List<MultiHolo> getMultiHoloList() {
        return multiHoloList;
    }

}
