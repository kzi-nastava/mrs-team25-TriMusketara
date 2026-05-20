package com.example.clickanddrive.dtosample.requests;

public class ReviewRequest {

    private Long rideId;
    private Long passengerId;
    private int driverRating;
    private int vehicleRating;
    private String comment;

    public ReviewRequest() {
    }

    public ReviewRequest(Long rideId, Long passengerId, int driverRating, int vehicleRating, String comment) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    public int getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(int vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}