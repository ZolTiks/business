package me.zoltiks.business.plugin.cloth;

import me.socrum.advanced.sum.Sum;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.zoltiks.business.Main.ini;

public class ClothUtil {

    @Nullable
    public static Cloth getClothByItemStack(ItemStack itemStack) {
        ClothManager clothManager = ini.get(ClothManager.class);
        Map<String, Cloth> stringClothMap = clothManager.getStringClothMap();
        for (Map.Entry<String, Cloth> stringClothEntry : stringClothMap.entrySet()) {
            String clothId = stringClothEntry.getKey();
            Cloth cloth = stringClothEntry.getValue();
            if (!UtilM.isSimilar(itemStack, cloth.getItemStack())) continue;
            return cloth;
        }
        return null;
    }

    @Nullable
    public static Cloth getPlayerHelmetCloth(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemStack = playerInventory.getHelmet();
        return getClothByItemStack(itemStack);
    }

    @Nullable
    public static Cloth getPlayerChestplateCloth(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemStack = playerInventory.getChestplate();
        return getClothByItemStack(itemStack);
    }

    @Nullable
    public static Cloth getPlayerLeggingsCloth(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemStack = playerInventory.getLeggings();
        return getClothByItemStack(itemStack);
    }

    @Nullable
    public static Cloth getPlayerBootsCloth(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemStack = playerInventory.getBoots();
        return getClothByItemStack(itemStack);
    }

    public static List<Cloth> getPlayerClothList(Player player) {
        List<Cloth> clothList = new ArrayList<>();
        clothList.add(getPlayerHelmetCloth(player));
        clothList.add(getPlayerChestplateCloth(player));
        clothList.add(getPlayerLeggingsCloth(player));
        clothList.add(getPlayerBootsCloth(player));
        while (clothList.contains(null)) clothList.remove(null);
        return clothList;
    }

    /**
     * @return суммарный "x2 + x2 = x3, а не x4" множитель всех одетых одежд игрока если есть или 1
     */
    public static double getTotalClothBusinessIncomeMultiplier(Player player) {
        double totalClothBusinessIncomeMultiplier = 1;
        List<Cloth> playerClothList = getPlayerClothList(player);
        for (Cloth cloth : playerClothList) {
            double multiplier = cloth.getBusinessIncomeMultiplier();
            if (multiplier == 1) continue; // при умножении на 1 результат не меняется
            if (multiplier > 1) multiplier = Sum.subtract(multiplier, 1);
            totalClothBusinessIncomeMultiplier = Sum.add(totalClothBusinessIncomeMultiplier, multiplier);
        }
        return totalClothBusinessIncomeMultiplier;
    }

    /**
     * @return суммарный дополнительный процент от дохода бизнеса который игрок получит от всех одетых одежд если есть или 0
     */
    public static double getTotalAdditionIncomePercentForTotalBusinessIncome(Player player) {
        double totalAdditionIncomePercentForTotalBusinessIncome = 0;
        List<Cloth> playerClothList = getPlayerClothList(player);
        for (Cloth cloth : playerClothList) {
            totalAdditionIncomePercentForTotalBusinessIncome = Sum.add(totalAdditionIncomePercentForTotalBusinessIncome, cloth.getAdditionIncomePercentForTotalBusinessIncome());
        }
        return totalAdditionIncomePercentForTotalBusinessIncome;
    }

}
