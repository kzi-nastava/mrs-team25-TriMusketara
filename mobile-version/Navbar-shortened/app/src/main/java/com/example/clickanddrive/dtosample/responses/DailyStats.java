package com.example.clickanddrive.dtosample.responses;

public class DailyStats {
    private String date;
    private int numberOfRides;
    private double totalKilometers;
    private double totalMoney;
    private int cumulativeRides;
    private double cumulativeKilometers;
    private double cumulativeMoney;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumberOfRides() {
        return numberOfRides;
    }

    public void setNumberOfRides(int numberOfRides) {
        this.numberOfRides = numberOfRides;
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

    public int getCumulativeRides() {
        return cumulativeRides;
    }

    public void setCumulativeRides(int cumulativeRides) {
        this.cumulativeRides = cumulativeRides;
    }

    public double getCumulativeKilometers() {
        return cumulativeKilometers;
    }

    public void setCumulativeKilometers(double cumulativeKilometers) {
        this.cumulativeKilometers = cumulativeKilometers;
    }

    public double getCumulativeMoney() {
        return cumulativeMoney;
    }

    public void setCumulativeMoney(double cumulativeMoney) {
        this.cumulativeMoney = cumulativeMoney;
    }
}
