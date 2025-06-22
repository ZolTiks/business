package me.zoltiks.business.plugin.database;

import me.socrum.advanced.util.UtilA;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.booster.Booster;
import me.zoltiks.business.plugin.business.ActorBusiness;
import me.zoltiks.business.plugin.business.Business;
import me.zoltiks.business.plugin.business.BusinessManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class YamlDatabase implements Database {

    private File dir;

    public YamlDatabase() {
        this.dir = new File(instance.getDataFolder() + File.separator + "database");
        if (!this.dir.exists()) this.dir.mkdirs();
    }

    @Override
    public Actor load(String playerName) {
        BusinessManager businessManager = ini.get(BusinessManager.class);
        Map<Integer, Business> integerBusinessMap = businessManager.getIntegerBusinessMap();
        try {
            Actor actor = null;
            File data = new File(this.dir + File.separator + playerName + ".yml");
            if (!data.exists()) {
                data.createNewFile();
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(data);
                yamlConfiguration.set("business_income_multiplier", 1);
                yamlConfiguration.set("last_exit_timestamp", -1);
                yamlConfiguration.set("booster_list", new HashMap<>());
                yamlConfiguration.set("prestige.level", 0);
                yamlConfiguration.save(data);
                actor = new Actor(
                        Bukkit.getPlayerExact(playerName),
                        -1,
                        1.0,
                        new CopyOnWriteArrayList<>(),
                        new ArrayList<>()
                );
                return actor;
            }
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(data);

            // бустеры
            List<Booster> boosterList = new ArrayList<>();
            ConfigurationSection boosterListConfigurationSection = yamlConfiguration.getConfigurationSection("booster_list");
            if (boosterListConfigurationSection != null) {
                Set<String> boosterIds = boosterListConfigurationSection.getKeys(false);
                for (String boosterId : boosterIds) {
                    ConfigurationSection boosterConfigurationSection = boosterListConfigurationSection.getConfigurationSection(boosterId);
                    int durationInSeconds = boosterConfigurationSection.getInt("duration_in_seconds");
                    double businessIncomeMultiplier = boosterConfigurationSection.getDouble("business_income_multiplier");
                    boosterList.add(new Booster(durationInSeconds, businessIncomeMultiplier));
                }
            }

            actor = new Actor(
                    Bukkit.getPlayerExact(playerName),
                    yamlConfiguration.getLong("last_exit_timestamp", -1),
                    yamlConfiguration.getDouble("business_income_multiplier", 1.0),
                    null,
                    boosterList
            );

            return actor;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Actor actor) {
        try {
            File data = new File(this.dir + File.separator + actor.getPlayer().getName() + ".yml");
            if (!data.exists()) {
                data.createNewFile();
            }
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(data);
            yamlConfiguration.set("last_exit_timestamp", UtilA.timestamp());
            yamlConfiguration.set("business_income_multiplier", actor.getBusinessIncomeMultiplier());
            List<ActorBusiness> actorBusinessList = actor.getActorBusinessList();

            yamlConfiguration.set("business_list", null);

            for (ActorBusiness actorBusiness : actorBusinessList) {
                yamlConfiguration.set("business_list." + actorBusiness.getBusiness().getId() + ".level", actorBusiness.getLevel());
            }

            // бустеры
            yamlConfiguration.set("booster_list", new HashMap<>()); // очистить список
            List<Booster> boosterList = actor.getBoosterList();
            for (Booster booster : boosterList) {
                String uniqueKey = UUID.randomUUID().toString();
                while (yamlConfiguration.get("booster_list." + uniqueKey, null) != null) uniqueKey = UUID.randomUUID().toString();
                yamlConfiguration.set("booster_list." + uniqueKey + ".duration_in_seconds", booster.getDurationInSeconds());
                yamlConfiguration.set("booster_list." + uniqueKey + ".business_income_multiplier", booster.getBusinessIncomeMultiplier());
            }

            yamlConfiguration.save(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

}

