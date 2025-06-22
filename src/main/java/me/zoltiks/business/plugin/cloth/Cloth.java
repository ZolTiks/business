package me.zoltiks.business.plugin.cloth;

import org.bukkit.inventory.ItemStack;

public class Cloth {

    private String id;
    private double businessIncomeMultiplier;
    private double additionIncomePercentForTotalBusinessIncome;
    private ItemStack itemStack;

    public Cloth(String id, double businessIncomeMultiplier, double additionIncomePercentForTotalBusinessIncome, ItemStack itemStack) {
        this.id = id;
        this.businessIncomeMultiplier = businessIncomeMultiplier;
        this.additionIncomePercentForTotalBusinessIncome = additionIncomePercentForTotalBusinessIncome;
        this.itemStack = itemStack;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getBusinessIncomeMultiplier() {
        return businessIncomeMultiplier;
    }

    public void setBusinessIncomeMultiplier(double businessIncomeMultiplier) {
        this.businessIncomeMultiplier = businessIncomeMultiplier;
    }

    public double getAdditionIncomePercentForTotalBusinessIncome() {
        return additionIncomePercentForTotalBusinessIncome;
    }

    public void setAdditionIncomePercentForTotalBusinessIncome(double additionIncomePercentForTotalBusinessIncome) {
        this.additionIncomePercentForTotalBusinessIncome = additionIncomePercentForTotalBusinessIncome;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

}
