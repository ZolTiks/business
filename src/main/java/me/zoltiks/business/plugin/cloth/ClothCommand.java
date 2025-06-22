package me.zoltiks.business.plugin.cloth;

import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import me.socrum.minecraft.spigot.plugin.utilm.advanced.CommandExecutorAdvanced;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class ClothCommand implements CommandExecutorAdvanced {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        FileConfiguration fileConfiguration = instance.getConfig();

        ClothManager clothManager = ini.get(ClothManager.class);
        Map<String, Cloth> stringClothMap = clothManager.getStringClothMap();

        // /cloth (ник игрока онлайн) add (ID одежды)
        if (strings.length == 3 && strings[1].equalsIgnoreCase("add")) {

            if (!commandSender.hasPermission(fileConfiguration.getString("permission.cloth_add_player"))) {
                UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.permission_denied");
                return true;
            }

            String anotherPlayerName = strings[0];
            Player anotherPlayer = Bukkit.getPlayerExact(anotherPlayerName);
            if (anotherPlayer == null) {
                UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.player_offline");
                return true;
            }

            String clothId = strings[2];
            Cloth cloth = stringClothMap.get(clothId);
            if (cloth == null) {
                UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.cloth_does_not_exist");
                return true;
            }

            UtilM.addItem(anotherPlayer, cloth.getItemStack());
            UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.cloth_gived");
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (strings.length == 1) {
            List<String> competitions = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> competitions.add(player.getName()));
            List<String> stringList = StringUtil.copyPartialMatches(strings[strings.length - 1], competitions, new ArrayList());
            Collections.sort(stringList);
            return stringList;
        }
        if (strings.length == 2) {
            List<String> competitions = new ArrayList<String>() {{
                add("add");
            }};
            List<String> stringList = StringUtil.copyPartialMatches(strings[strings.length - 1], competitions, new ArrayList());
            Collections.sort(stringList);
            return stringList;
        }
        return Collections.emptyList();
    }

}
