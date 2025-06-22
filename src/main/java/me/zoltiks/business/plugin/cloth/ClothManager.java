package me.zoltiks.business.plugin.cloth;

import me.socrum.advanced.ini.Initer;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class ClothManager implements Initer {

    private final Map<String, Cloth> stringClothMap;

    public ClothManager() {
        this.stringClothMap = new HashMap<>();

        FileConfiguration fileConfiguration = instance.getConfig();
        ConfigurationSection clothListConfigurationSection = fileConfiguration.getConfigurationSection("cloth_list");
        Set<String> clothIdSet = clothListConfigurationSection.getKeys(false);
        clothIdSet.forEach(clothId -> {
            ConfigurationSection clothConfigurationSection = clothListConfigurationSection.getConfigurationSection(clothId);
            Cloth cloth = new Cloth(
                    clothId,
                    clothConfigurationSection.getDouble("business_income_multiplier"),
                    clothConfigurationSection.getDouble("addition_income_percent_for_total_business_income"),
                    UtilM.getItemStackFromConfigurationPath(instance, clothConfigurationSection.getConfigurationSection("item").getCurrentPath())
            );
            this.stringClothMap.put(clothId, cloth);
        });

        UtilM.registerClassAsListener(instance, ini.put(new ClothListener()));
        UtilM.registerClassAsCommand(instance, "cloth", ini.put(new ClothCommand()));
    }

    public Map<String, Cloth> getStringClothMap() {
        return stringClothMap;
    }

}
