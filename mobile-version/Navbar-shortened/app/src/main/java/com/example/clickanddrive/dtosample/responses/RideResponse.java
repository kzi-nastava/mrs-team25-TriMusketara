package com.example.clickanddrive.dtosample.responses;

import androidx.annotation.NonNull;

import com.example.clickanddrive.dtosample.enumerations.RideStatus;

public class RideResponse {
    private Long id;
    private RideStatus status;
    private double price;

    public RideResponse() {}

    public RideResponse(Long id, RideStatus status, double price) {
        this.id = id;
        this.status = status;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @NonNull
    @Override
    public String toString() {
        return "RideResponse{" +
                "id=" + id +
                ", status=" + status +
                ", price=" + price +
                '}';
    }
}
