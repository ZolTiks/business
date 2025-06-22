package me.zoltiks.business.plugin.booster;

import me.socrum.advanced.sum.Sum;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.entity.Player;

import java.util.List;

public class BoosterUtil {

    public static List<Booster> getPlayerBoosterList(Player player) {
        Actor actor = Util.requiredNullableActor(player);
        return actor.getBoosterList();
    }

    /**
     * @return суммарный множитель всех бустеров игрока (если есть или 1), сложение путем "x2 + x2 = x3, а не x4"
     */
    public static double getTotalBoosterBusinessIncomeMultiplier(Player player) {
        double totalClothBusinessIncomeMultiplier = 1;
        List<Booster> playerBoosterList = getPlayerBoosterList(player);
        for (Booster booster : playerBoosterList) {
            double multiplier = booster.getBusinessIncomeMultiplier();
            if (multiplier == 1) continue; // при умножении на 1 результат не меняется
            if (multiplier > 1) multiplier = Sum.subtract(multiplier, 1);
            totalClothBusinessIncomeMultiplier = Sum.add(totalClothBusinessIncomeMultiplier, multiplier);
        }
        return totalClothBusinessIncomeMultiplier;
    }

}
