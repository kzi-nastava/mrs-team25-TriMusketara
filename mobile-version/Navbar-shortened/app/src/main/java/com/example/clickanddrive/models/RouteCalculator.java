package com.example.clickanddrive.models;

import android.util.Log;

import com.example.clickanddrive.dtosample.LocationDTO;
import com.example.clickanddrive.map.MapboxDirections;
import com.example.clickanddrive.map.MapboxGeocoder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RouteCalculator {
    private static final String TAG = "RouteCalculator";

    private final MapboxGeocoder geocoder;
    private final MapboxDirections directions;

    public RouteCalculator() {
        this.geocoder = new MapboxGeocoder();
        this.directions = new MapboxDirections();
    }

    // Result of ride ordering
    public static class RouteCalculationResult {
        public String origin;
        public String destination;
        public List<String> stops;

        public double originLng;
        public double originLat;
        public double destLng;
        public double destLat;

        public List<LocationDTO> stopLocations;

        public double distanceKm;
        public int durationMinutes;

        public List<MapboxDirections.Coordinate> routeCoordinates;

        public RouteCalculationResult() {
            this.stops = new ArrayList<>();
            this.stopLocations = new ArrayList<>();
        }
    }

    public interface RouteCalculationCallback {
        void onSuccess(RouteCalculationResult result);
        void onError(String error);
    }

    public void calculateRoute(
            String origin,
            String destination,
            List<String> stops,
            Double prefilledOriginLng,
            Double prefilledOriginLat,
            Double prefilledDestLng,
            Double prefilledDestLat,
            RouteCalculationCallback callback) {

        Log.d(TAG, "Origin: " + origin);
        Log.d(TAG, "Destination: " + destination);
        Log.d(TAG, "Stops: " + (stops != null ? stops.size() : 0));

        RouteCalculationResult result = new RouteCalculationResult();
        result.origin = origin;
        result.destination = destination;
        result.stops = stops != null ? new ArrayList<>(stops) : new ArrayList<>();

        // Check if we already have coords (from favorite routes)
        boolean hasPrefilledCoords =   prefilledOriginLng != null &&
                                        prefilledOriginLat != null &&
                                        prefilledDestLng != null &&
                                        prefilledDestLat != null &&
                                        prefilledOriginLng != 0.0 &&
                                        prefilledOriginLat != 0.0;

        if (hasPrefilledCoords) {
            Log.d(TAG, "Using prefilled coordinates from favorites");
            result.originLng = prefilledOriginLng;
            result.originLat = prefilledOriginLat;
            result.destLng = prefilledDestLng;
            result.destLat = prefilledDestLat;

            // Geocode additional stops
            geocodeStops(result, callback);
        } else {
            Log.d(TAG, "Geocoding origin and destination from scratch");
            // Geocode whole route
            geocodeOriginAndDestination(result, callback);
        }
    }

    // Geocode origin and destination
    private void geocodeOriginAndDestination(RouteCalculationResult result, RouteCalculationCallback callback) {
        AtomicInteger completed = new AtomicInteger(0);

        // Geocode origin
        geocoder.geocode(result.origin, new MapboxGeocoder.GeocodeCallback() {
            @Override
            public void onSuccess(double lng, double lat) {
                Log.d(TAG, "Origin geocoded: [" + lng + ", " + lat + "]");
                result.originLng = lng;
                result.originLat = lat;

                if (completed.incrementAndGet() == 2) {
                    geocodeStops(result, callback);
                }
            }

            @Override
            public void onError(String error) {
                callback.onError("Failed to geocode origin: " + error);
            }
        });

        // Geocode destination
        geocoder.geocode(result.destination, new MapboxGeocoder.GeocodeCallback() {
            @Override
            public void onSuccess(double lng, double lat) {
                Log.d(TAG, "Destination geocoded: [" + lng + ", " + lat + "]");
                result.destLng = lng;
                result.destLat = lat;

                if (completed.incrementAndGet() == 2) {
                    geocodeStops(result, callback);
                }
            }

            @Override
            public void onError(String error) {
                callback.onError("Failed to geocode destination: " + error);
            }
        });
    }

    // Geocode stops
    public void geocodeStops(RouteCalculationResult result, RouteCalculationCallback callback) {
        if (result.stops.isEmpty()) {
            calculateDirections(result, callback);
            return;
        }

        AtomicInteger completed = new AtomicInteger(0);
        int totalStops = result.stops.size();

        for (int i = 0; i < result.stops.size(); i++) {
            String stopAddress = result.stops.get(i);
            final int index = i;

            geocoder.geocode(stopAddress, new MapboxGeocoder.GeocodeCallback() {
                @Override
                public void onSuccess(double lng, double lat) {
                    synchronized (result.stopLocations) {
                        result.stopLocations.add(new LocationDTO(lng, lat, stopAddress));
                    }

                    if (completed.incrementAndGet() == totalStops) {
                        calculateDirections(result, callback);
                    }
                }

                @Override
                public void onError(String error) {
                    callback.onError("Failed to geocode stop '" + stopAddress + "': " + error);
                }
            });
        }
    }

    // Calculate route with Directions API
    public void calculateDirections(RouteCalculationResult result, RouteCalculationCallback callback) {

        // Create waypoints
        List<MapboxDirections.Coordinate> waypoints = new ArrayList<>();
        waypoints.add(new MapboxDirections.Coordinate(result.originLng, result.originLat));

        for (LocationDTO stop : result.stopLocations) {
            waypoints.add(new MapboxDirections.Coordinate(stop.getLongitude(), stop.getLatitude()));
        }

        waypoints.add(new MapboxDirections.Coordinate(result.destLng, result.destLat));

        // Call
        directions.getRoute(waypoints, new MapboxDirections.DirectionsCallback() {
            @Override
            public void onSuccess(MapboxDirections.RouteResult routeResult) {
                Log.d(TAG, "  - Distance: " + routeResult.distanceKm + " km");
                Log.d(TAG, "  - Duration: " + routeResult.durationMinutes + " min");

                result.distanceKm = routeResult.distanceKm;
                result.durationMinutes = routeResult.durationMinutes;
                result.routeCoordinates = routeResult.routeCoordinates;

                callback.onSuccess(result);
            }

            @Override
            public void onError(String error) {
                callback.onError("Failed to calculate route: " + error);
            }
        });
    }
}
