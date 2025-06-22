package me.zoltiks.business.plugin.multiholo;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.socrum.advanced.ini.Initer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MultiHolo implements Initer {

    private final Location location;
    private final Map<Player, Hologram> playerHologramMap;

    public MultiHolo(Location location) {
        this.location = location;
        this.playerHologramMap = new HashMap<>();
    }

    @Override
    public void init() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.playerHologramMap.put(player, create());
        }
        showHide();
    }

    @Override
    public void destroy() {
        for (Map.Entry<Player, Hologram> playerHologramEntry : this.playerHologramMap.entrySet()) {
            Hologram hologram = playerHologramEntry.getValue();
            DHAPI.removeHologram(hologram.getName());
        }
        this.playerHologramMap.clear();
    }

    private Hologram create() {
        Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), this.location);
        hologram.setDownOrigin(true);
        return hologram;
    }

    private void showHide() {
        for (Map.Entry<Player, Hologram> playerHologramEntry : this.playerHologramMap.entrySet()) {
            Player player = playerHologramEntry.getKey();
            Hologram hologram = playerHologramEntry.getValue();
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                if (player.equals(player1)) continue; // игрок должен видеть свою голограмму
                hologram.setHidePlayer(player1); // другие игрока не должны видеть чужие голограммы
            }
        }
    }

    public void onPlayerJoin(Player player) {
        this.playerHologramMap.put(player, create());
        showHide();
    }

    public void onPlayerQuit(Player player) {
        Hologram hologram = this.playerHologramMap.get(player);

        // DecentHolograms останавливает поток при удалении голограммы, че за хрень ??? добавим соответствующие проверки (может поможет)
        Hologram nullableHologram = DHAPI.getHologram(hologram.getName());
        if (nullableHologram != null) {
            DHAPI.removeHologram(hologram.getName());
        }

        this.playerHologramMap.remove(player);
    }

    public Location getLocation() {
        return location;
    }

    public Map<Player, Hologram> getPlayerHologramMap() {
        return playerHologramMap;
    }

}
