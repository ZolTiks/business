package me.zoltiks.business.plugin.offlineincome;

public class OfflineIncome {

    private long seconds;
    private double percent;

    public OfflineIncome(long seconds, double percent) {
        this.seconds = seconds;
        this.percent = percent;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

}
