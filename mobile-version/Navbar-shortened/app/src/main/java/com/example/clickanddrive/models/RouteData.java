package com.example.clickanddrive.models;

import java.io.Serializable;
import java.util.List;

// Class model for keeping data about a route
// Serializable so we can share through fragments
public class RouteData implements Serializable {
    private String origin;
    private String destination;
    private List<String> stops;

    public RouteData() {}

    public RouteData(String origin, String destination, List<String> stops) {
        this.origin = origin;
        this.destination = destination;
        this.stops = stops;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<String> getStops() {
        return stops;
    }

    public void setStops(List<String> stops) {
        this.stops = stops;
    }
}
