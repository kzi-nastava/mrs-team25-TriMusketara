package com.example.clickanddrive.map;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Calculating route between points
public class MapboxDirections {
    private static final String TAG = "MapboxDirections";

    // Mapbox access token
    private static final String MAPBOX_TOKEN = "pk.eyJ1IjoicmliaWNuaWtvbGEiLCJhIjoiY21qbTJvNHFlMmV6OTNncXhpOGNiaTVnayJ9.Bhzo0Euk2D923K3smmoVaQ";

    // HTTP client
    private final OkHttpClient client;

    // Initialize client
    public MapboxDirections() {
        this.client = new OkHttpClient();
    }

    // Coordinate class
    public static class Coordinate {
        public double lng;
        public double lat;

        public Coordinate(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }

        @Override
        public String toString() {
            return lng + "," + lat;
        }
    }

    // Result directions API class
    public static class RouteResult {
        public List<Coordinate> routeCoordinates;
        public double distanceKm;
        public int durationMinutes;

        public RouteResult(List<Coordinate> routeCoordinates, double distanceKm, int durationMinutes) {
            this.routeCoordinates = routeCoordinates;
            this.distanceKm = distanceKm;
            this.durationMinutes = durationMinutes;
        }
    }

    public interface DirectionsCallback {
        void onSuccess(RouteResult result);
        void onError(String error);
    }

    // Main method for calculating route between points
    public void getRoute(List<Coordinate> coordinates, DirectionsCallback callback) {
        // Validation
        // At least two coordinates
        if (coordinates == null || coordinates.size() < 2) {
            callback.onError("At least 2 coordinates required");
            return;
        }

        Log.d(TAG, "Calculating route with " + coordinates.size() + " waypoints");

        // Format: "lng1,lat1;lng2,lat2;lng3,lat3"
        StringBuilder coordinatesString = new StringBuilder();
        for (int i = 0; i < coordinates.size(); i++) {
            coordinatesString.append(coordinates.get(i).toString());

            if (i < coordinates.size() - 1) {
                coordinatesString.append(";");
            }
        }

        String url = "https://api.mapbox.com/directions/v5/mapbox/driving/" +
                coordinatesString.toString() +
                "?geometries=geojson" +
                "&overview=full" +
                "&access_token=" + MAPBOX_TOKEN;

        Log.d(TAG, "Request URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Directions network error", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                Log.d(TAG, "Response code: " + response.code());

                String responseBody = response.body().string();
                Log.d(TAG, "Response body length: " + responseBody.length() + " bytes");

                if (!response.isSuccessful()) {
                    Log.e(TAG, "Directions failed with code: " + response.code());
                    callback.onError("HTTP error " + response.code() + ": " + responseBody);
                    return;
                }

                try {
                    JSONObject json = new JSONObject(responseBody);

                    JSONArray routes = json.getJSONArray("routes");

                    if (routes.length() == 0) {
                        Log.w(TAG, "No routes found");
                        callback.onError("No route found between these points");
                        return;
                    }

                    JSONObject route = routes.getJSONObject(0);

                    JSONObject geometry = route.getJSONObject("geometry");

                    JSONArray coordinatesArray = geometry.getJSONArray("coordinates");

                    List<Coordinate> routeCoordinates = new java.util.ArrayList<>();
                    for (int i = 0; i < coordinatesArray.length(); i++) {
                        JSONArray coord = coordinatesArray.getJSONArray(i);
                        double lng = coord.getDouble(0);
                        double lat = coord.getDouble(1);
                        routeCoordinates.add(new Coordinate(lng, lat));
                    }

                    double distanceMeters = route.getDouble("distance");
                    double distanceKm = Math.round((distanceMeters / 1000.0) * 100.0) / 100.0;

                    double durationSeconds = route.getDouble("duration");
                    int durationMinutes = (int) Math.round(durationSeconds / 60.0);

                    Log.d(TAG, "Route calculated successfully:");
                    Log.d(TAG, "  - Distance: " + distanceKm + " km");
                    Log.d(TAG, "  - Duration: " + durationMinutes + " minutes");
                    Log.d(TAG, "  - Route points: " + routeCoordinates.size());

                    RouteResult result = new RouteResult(routeCoordinates, distanceKm, durationMinutes);

                    callback.onSuccess(result);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing directions response", e);
                    callback.onError("Failed to parse response: " + e.getMessage());
                }
            }
        });
    }

}
