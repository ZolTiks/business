package me.zoltiks.business.plugin.booster;

import me.socrum.advanced.util.UtilA;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import me.socrum.minecraft.spigot.plugin.utilm.advanced.CommandExecutorAdvanced;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.database.YamlDatabase;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.*;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class BoosterCommand implements CommandExecutorAdvanced {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // booster global add (кол-во сек) (множитель)
        if (strings.length == 4 && strings[0].equalsIgnoreCase("global") && strings[1].equalsIgnoreCase("add")) {

            if (!commandSender.hasPermission(instance.getConfig().getString("permission.booster_add_global"))) {
                UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.permission_denied");
                return true;
            }

            String stringDurationInSeconds = strings[2];
            String stringBusinessIncomeMultiplier = strings[3];

            if (!UtilA.isInteger(stringDurationInSeconds)) {
                UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.invalid_booster_duration_time");
                return true;
            }

            if (!UtilA.isDouble(stringBusinessIncomeMultiplier)) {
                UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.invalid_booster_multiplier");
                return true;
            }

            int durationInSeconds = Integer.parseInt(stringDurationInSeconds);
            double businessIncomeMultiplier = Double.parseDouble(stringBusinessIncomeMultiplier);

            for (Player player : Bukkit.getOnlinePlayers()) {
                Actor actor = Util.requiredNullableActor(player);
                if (actor == null) return true;
                actor.getBoosterList().add(new Booster(durationInSeconds, businessIncomeMultiplier));
                UtilM.sendMessageFromConfigurationPath(instance, player, "message.booster_global_received", new HashMap<String, String>() {{
                    put("%multiplier%", UtilA.number(businessIncomeMultiplier));
                    put("%duration%", UtilA.shortTimeString(durationInSeconds));
                }});
            }

            UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.booster_global_gived", new HashMap<String, String>() {{
                put("%multiplier%", UtilA.number(businessIncomeMultiplier));
                put("%duration%", UtilA.shortTimeString(durationInSeconds));
            }});
            return true;
        }

        // booster player (ник) add (кол-во сек) (множитель)
        if (strings.length == 5 && strings[0].equalsIgnoreCase("player") && strings[2].equalsIgnoreCase("add")) {

            if (!commandSender.hasPermission(instance.getConfig().getString("permission.booster_add_player"))) {
                UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.permission_denied");
                return true;
            }

            String stringDurationInSeconds = strings[3];
            String stringBusinessIncomeMultiplier = strings[4];

            if (!UtilA.isInteger(stringDurationInSeconds)) {
                UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.invalid_booster_duration_time");
                return true;
            }

            if (!UtilA.isDouble(stringBusinessIncomeMultiplier)) {
                UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.invalid_booster_multiplier");
                return true;
            }

            int durationInSeconds = Integer.parseInt(stringDurationInSeconds);
            double businessIncomeMultiplier = Double.parseDouble(stringBusinessIncomeMultiplier);

            String playerName = strings[1];
            Player player = Bukkit.getPlayerExact(playerName);
            if (player == null) {
                // оффлайн

                YamlDatabase yamlDatabase = ini.get(YamlDatabase.class);
                File dir = yamlDatabase.getDir();
                File userFile = new File(dir.getAbsolutePath() + File.separator + playerName + ".yml");
                if (!userFile.exists()) {
                    UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.player_does_not_exist");
                    return true;
                }

                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(userFile);

                // загрузить бустеры
                List<Booster> boosterList = new ArrayList<>();
                ConfigurationSection boosterListConfigurationSection = yamlConfiguration.getConfigurationSection("booster_list");
                if (boosterListConfigurationSection != null) {
                    Set<String> boosterIds = boosterListConfigurationSection.getKeys(false);
                    for (String boosterId : boosterIds) {
                        ConfigurationSection boosterConfigurationSection = boosterListConfigurationSection.getConfigurationSection(boosterId);
                        int durationInSeconds1 = boosterConfigurationSection.getInt("duration_in_seconds");
                        double businessIncomeMultiplier1 = boosterConfigurationSection.getDouble("business_income_multiplier");
                        boosterList.add(new Booster(durationInSeconds1, businessIncomeMultiplier1));
                    }
                }

                // добавить новый бустер
                boosterList.add(new Booster(durationInSeconds, businessIncomeMultiplier));

                // сохранить бустеры
                yamlConfiguration.set("booster_list", new HashMap<>()); // очистить список
                for (Booster booster : boosterList) {
                    String uniqueKey = UUID.randomUUID().toString();
                    while (yamlConfiguration.get("booster_list." + uniqueKey, null) != null) uniqueKey = UUID.randomUUID().toString();
                    yamlConfiguration.set("booster_list." + uniqueKey + ".duration_in_seconds", booster.getDurationInSeconds());
                    yamlConfiguration.set("booster_list." + uniqueKey + ".business_income_multiplier", booster.getBusinessIncomeMultiplier());
                }

            } else {
                // онлайн
                Actor actor = Util.requiredNullableActor(player);
                actor.getBoosterList().add(new Booster(durationInSeconds, businessIncomeMultiplier));
                UtilM.sendMessageFromConfigurationPath(instance, player, "message.booster_player_received", new HashMap<String, String>() {{
                    put("%multiplier%", UtilA.number(businessIncomeMultiplier));
                    put("%duration%", UtilA.shortTimeString(durationInSeconds));
                }});
            }

            UtilM.sendMessageFromConfigurationPath(instance, commandSender, "message.booster_player_gived", new HashMap<String, String>() {{
                put("%player%", playerName);
                put("%multiplier%", UtilA.number(businessIncomeMultiplier));
                put("%duration%", UtilA.shortTimeString(durationInSeconds));
            }});
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (strings.length == 1) {
            List<String> competitions = new ArrayList<String>() {{
                add("global");
                add("player");
            }};
            List<String> stringList = StringUtil.copyPartialMatches(strings[strings.length - 1], competitions, new ArrayList());
            Collections.sort(stringList);
            return stringList;
        }
        if (strings.length == 2) {
            List<String> competitions = new ArrayList<String>() {{
                add("add");
            }};
            Bukkit.getOnlinePlayers().forEach(player -> competitions.add(player.getName()));
            List<String> stringList = StringUtil.copyPartialMatches(strings[strings.length - 1], competitions, new ArrayList());
            Collections.sort(stringList);
            return stringList;
        }
        return Collections.emptyList();
    }

}
