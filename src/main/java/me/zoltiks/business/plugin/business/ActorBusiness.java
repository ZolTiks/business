package me.zoltiks.business.plugin.business;

import me.socrum.advanced.util.UtilA;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.booster.BoosterUtil;
import me.zoltiks.business.plugin.cloth.ClothUtil;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.zoltiks.business.Main.instance;
import static org.bukkit.Bukkit.getLogger;

public class ActorBusiness {
    private final Actor actor;
    private final Business business;
    private int level;

    public ActorBusiness(Actor actor, Business business, int level) {
        this.actor = actor;
        this.business = business;
        this.level = level;
    }

    public ItemStack getItemStackVisualization() {
        ItemStack itemStack = this.business.getItemStackVisualization().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        ArrayList<String> description = new ArrayList<String>();
        if (itemMeta.hasLore()) {
            List lore = itemMeta.getLore();
            for (int i = 0; i < lore.size(); ++i) {
                description.add(((String) lore.get(i)).replace("%level%", String.valueOf(this.level)).replace("%max_level%", String.valueOf(this.business.getBusinessLevelList().size())).replace("%income%", Util.format(this.business.getBusinessLevelList().get(this.level - 1).getIncome())).replace("%income_delay%", String.valueOf(this.business.getIncomeDefaultDelay())));
            }
        }
        itemMeta.setLore(UtilM.colorize(description));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public double getTotalIncome() {
        // 1. Получаем базовый доход бизнеса текущего уровня
        BusinessLevel businessLevel = null;
        for (BusinessLevel bl : this.business.getBusinessLevelList()) {
            if (bl.getLevel() == this.level) {
                businessLevel = bl;
                break;
            }
        }
        if (businessLevel == null) return 0;

        double baseIncome = businessLevel.getIncome();
        Player player = this.actor.getPlayer();

        // 2. Получаем все множители
        double playerMultiplier = this.actor.getBusinessIncomeMultiplier();
        double boosterMultiplier = BoosterUtil.getTotalBoosterBusinessIncomeMultiplier(player);
        double clothMultiplier = ClothUtil.getTotalClothBusinessIncomeMultiplier(player);

        // 3. Получаем формулу из конфига (если есть)
        FileConfiguration config = instance.getConfig();
        String formula = config.getString("business_final_income_formula",
                "%business_income% * %player_business_income_multiplier% * %total_booster_business_income_multiplier% * %total_cloth_business_income_multiplier%");

        // 4. Заменяем плейсхолдеры в формуле
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%business_income%", String.valueOf(baseIncome));
        placeholders.put("%player_business_income_multiplier%", String.valueOf(playerMultiplier));
        placeholders.put("%total_booster_business_income_multiplier%", String.valueOf(boosterMultiplier));
        placeholders.put("%total_cloth_business_income_multiplier%", String.valueOf(clothMultiplier));

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            formula = formula.replace(entry.getKey(), entry.getValue());
        }

        // 5. Обрабатываем научную нотацию
        formula = formula.replaceAll("([0-9]+\\.[0-9]+)E([+-]?[0-9]+)", "$1*10^$2");

        try {
            // 6. Вычисляем итоговый доход
            double calculatedIncome = Util.eval(formula);
            return calculatedIncome;

        } catch (Exception e) {
            // 8. Fallback: простой расчет если формула не сработала
            double fallbackIncome = baseIncome * playerMultiplier * boosterMultiplier * clothMultiplier;
            instance.getLogger().warning(String.format(
                    "Failed to calculate income using formula '%s'. Using fallback calculation: %.2f",
                    formula,
                    fallbackIncome
            ));
            return fallbackIncome;
        }
    }

    public Actor getActor() {
        return this.actor;
    }

    public Business getBusiness() {
        return this.business;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

