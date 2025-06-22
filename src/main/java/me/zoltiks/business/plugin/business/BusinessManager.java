package me.zoltiks.business.plugin.business;

import me.socrum.advanced.ini.Initer;
import me.socrum.minecraft.spigot.plugin.utilm.Updatable;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import me.zoltiks.business.FormulaEvaluator;
import me.zoltiks.business.Main;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

import static me.zoltiks.business.FormulaEvaluator.eval;
import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class BusinessManager implements Initer, Updatable {

    private final Map<Integer, Business> integerBusinessMap;

    public BusinessManager() {
        this.integerBusinessMap = new TreeMap<>();
        UtilM.registerClassAsListener(instance, ini.put(new BusinessListener()));
        FileConfiguration fileConfiguration = Main.instance.getConfig();
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("business_list");
        Set keySet = configurationSection.getKeys(false);
        for (Object key : keySet) {
            ConfigurationSection businessConfigurationSection = configurationSection.getConfigurationSection((String) key);
            int businessId = Integer.parseInt((String) key);
            int incomeDelay = businessConfigurationSection.getInt("income_delay");
            Business business = new Business(
                    businessId,
                    Util.parseConfigItemStack(businessConfigurationSection.getConfigurationSection("item").getCurrentPath()),
                    null,
                    incomeDelay,
                    businessConfigurationSection.getInt("x"),
                    businessConfigurationSection.getInt("y"),
                    businessConfigurationSection.getInt("z")
            );
            ArrayList<BusinessLevel> businessLevelList = new ArrayList<BusinessLevel>();
            String levelPriceFormula = businessConfigurationSection.getString("level_price_formula");
            String levelIncomeFormula = businessConfigurationSection.getString("level_income_formula");
            int maxLevel = businessConfigurationSection.getInt("max_level");
            for (int i = 0; i < maxLevel; ++i) {

                businessLevelList.add(new BusinessLevel(business, i + 1, Util.eval(levelPriceFormula.replace("%level%", String.valueOf(i + 1))), Util.eval(levelIncomeFormula.replace("%level%", String.valueOf(i + 1)))));
            }
            business.setBusinessLevelList(businessLevelList);
            this.integerBusinessMap.put(businessId, business);
        }
    }

    public BusinessLevel getBusinessLevelForUpgrade(Actor actor, Business business, int currentLevel) {
        List<BusinessLevel> levels = business.getBusinessLevelList();

        if (currentLevel >= levels.size()) {
            // Создаем виртуальный уровень на основе последнего доступного
            BusinessLevel lastLevel = levels.get(levels.size() - 1);
            return new BusinessLevel(
                    business,
                    currentLevel + 1,
                    lastLevel.getPrice() * 1.5, // Увеличиваем цену
                    lastLevel.getIncome() * 1.2 // Увеличиваем доход
            );
        }

        return levels.get(currentLevel);
    }

    @Override
    public void update() {
        this.getIntegerBusinessMap().forEach((businessId, business) -> business.update());
    }

    public Map<Integer, Business> getIntegerBusinessMap() {
        return this.integerBusinessMap;
    }

}

