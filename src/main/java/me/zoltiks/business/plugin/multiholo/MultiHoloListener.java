package me.zoltiks.business.plugin.multiholo;

import me.socrum.minecraft.spigot.plugin.utilm.advanced.ListenerAdvanced;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

import static me.zoltiks.business.Main.ini;

public class MultiHoloListener implements ListenerAdvanced {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<MultiHolo> multiHoloList = ini.get(MultiHoloManager.class).getMultiHoloList();
        multiHoloList.forEach(multiHolo -> multiHolo.onPlayerJoin(player));
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        List<MultiHolo> multiHoloList = ini.get(MultiHoloManager.class).getMultiHoloList();
        multiHoloList.forEach(multiHolo -> multiHolo.onPlayerQuit(player));
    }

}
