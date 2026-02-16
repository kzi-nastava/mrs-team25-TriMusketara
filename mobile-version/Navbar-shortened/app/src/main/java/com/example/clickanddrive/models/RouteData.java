package com.example.clickanddrive.models;

import com.example.clickanddrive.dtosample.LocationDTO;
import com.example.clickanddrive.map.MapboxDirections;

import java.io.Serializable;
import java.util.List;

// Class model for keeping data about a route
// Serializable so we can share through fragments
public class RouteData implements Serializable {
    private String origin;
    private String destination;
    private List<String> stops;

    private double originLng;
    private double originLat;
    private double destinationLng;
    private double destinationLat;
    private double distanceKm;
    private int durationMinutes;
    private List<LocationDTO> stopLocations;
    private List<MapboxDirections.Coordinate> routeCoordinates;

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

    public double getOriginLng() {
        return originLng;
    }

    public void setOriginLng(double originLng) {
        this.originLng = originLng;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public List<LocationDTO> getStopLocations() {
        return stopLocations;
    }

    public void setStopLocations(List<LocationDTO> stopLocations) {
        this.stopLocations = stopLocations;
    }

    public List<MapboxDirections.Coordinate> getRouteCoordinates() {
        return routeCoordinates;
    }

    public void setRouteCoordinates(List<MapboxDirections.Coordinate> routeCoordinates) {
        this.routeCoordinates = routeCoordinates;
    }
}
