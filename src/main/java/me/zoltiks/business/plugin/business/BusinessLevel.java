package me.zoltiks.business.plugin.business;

public class BusinessLevel {
    private Business business;
    private int level;
    private double price;
    private double income;

    public BusinessLevel(Business business, int level, double price, double income) {
        this.business = business;
        this.level = level;
        this.price = price;
        this.income = income;
    }

    public Business getBusiness() {
        return this.business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getIncome() {
        return this.income;
    }

    public void setIncome(double income) {
        this.income = income;
    }
}

