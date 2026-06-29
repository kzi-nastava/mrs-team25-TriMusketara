package com.example.clickanddrive.dtosample.responses;

public class SummaryStats {
    public int getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(int totalRides) {
        this.totalRides = totalRides;
    }

    public double getTotalKilometers() {
        return totalKilometers;
    }

    public void setTotalKilometers(double totalKilometers) {
        this.totalKilometers = totalKilometers;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public double getAvgRidesPerDay() {
        return avgRidesPerDay;
    }

    public void setAvgRidesPerDay(double avgRidesPerDay) {
        this.avgRidesPerDay = avgRidesPerDay;
    }

    public double getAvgKilometersPerDay() {
        return avgKilometersPerDay;
    }

    public void setAvgKilometersPerDay(double avgKilometersPerDay) {
        this.avgKilometersPerDay = avgKilometersPerDay;
    }

    public double getAvgMoneyPerDay() {
        return avgMoneyPerDay;
    }

    public void setAvgMoneyPerDay(double avgMoneyPerDay) {
        this.avgMoneyPerDay = avgMoneyPerDay;
    }

    private int totalRides;
    private double totalKilometers;
    private double totalMoney;
    private double avgRidesPerDay;
    private double avgKilometersPerDay;
    private double avgMoneyPerDay;
}
