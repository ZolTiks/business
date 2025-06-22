package me.zoltiks.business.plugin.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;

import java.util.List;

public class Holo {

    public void createHolo(String text, String name, List<String> lines, Location location, boolean saveToFile) {
        DHAPI.createHologram("name", location, true);
        DNAPI.addHologramLine(DNAPI.getHologram(name), text);
    }

    public void deleteHolo(String name) {
        DNAPI.removeHologram(name);
    }

}
