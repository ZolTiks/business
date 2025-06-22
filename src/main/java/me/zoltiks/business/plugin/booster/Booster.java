package me.zoltiks.business.plugin.booster;

import me.socrum.minecraft.spigot.plugin.utilm.Updatable;

public class Booster implements Updatable {

    private int durationInSeconds;
    private double businessIncomeMultiplier;

    public Booster(int durationInSeconds, double businessIncomeMultiplier) {
        this.durationInSeconds = durationInSeconds;
        this.businessIncomeMultiplier = businessIncomeMultiplier;
    }

    @Override
    public void update() {
        this.durationInSeconds--;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public double getBusinessIncomeMultiplier() {
        return businessIncomeMultiplier;
    }

    public void setBusinessIncomeMultiplier(double businessIncomeMultiplier) {
        this.businessIncomeMultiplier = businessIncomeMultiplier;
    }

}
