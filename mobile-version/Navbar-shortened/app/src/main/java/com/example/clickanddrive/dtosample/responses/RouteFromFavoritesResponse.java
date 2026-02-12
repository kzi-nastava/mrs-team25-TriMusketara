package com.example.clickanddrive.dtosample.responses;

import androidx.annotation.NonNull;

import com.example.clickanddrive.dtosample.LocationDTO;

public class RouteFromFavoritesResponse {
    private Long id;
    private LocationDTO origin;
    private LocationDTO destination;
    private double distance;
    private int duration;
    private int timesUsed;

    public RouteFromFavoritesResponse() {}

    public RouteFromFavoritesResponse(Long id, LocationDTO origin, LocationDTO destination, double distance, int duration, int timesUsed) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.duration = duration;
        this.timesUsed = timesUsed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocationDTO getOrigin() {
        return origin;
    }

    public void setOrigin(LocationDTO origin) {
        this.origin = origin;
    }

    public LocationDTO getDestination() {
        return destination;
    }

    public void setDestination(LocationDTO destination) {
        this.destination = destination;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }

    @NonNull
    @Override
    public String toString() {
        return "RouteFromFavoritesResponse{" +
                "id=" + id +
                ", origin=" + origin +
                ", destination=" + destination +
                ", distance=" + distance +
                ", duration=" + duration +
                ", timesUsed=" + timesUsed +
                '}';
    }
}
