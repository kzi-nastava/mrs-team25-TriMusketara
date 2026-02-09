package com.example.clickanddrive.map;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Conversion from String to (lng, lat)
// Mapbox Geocoding API
public class MapboxGeocoder {

    private static final String TAG = "MapboxGeocoder";

    // Mapbox access token
    private static final String MAPBOX_TOKEN = "pk.eyJ1IjoicmliaWNuaWtvbGEiLCJhIjoiY21qbTJvNHFlMmV6OTNncXhpOGNiaTVnayJ9.Bhzo0Euk2D923K3smmoVaQ";
    // Bounding box
    private static final String BBOX = "19.75,45.20,19.95,45.30";

    // HTTP client
    private final OkHttpClient client;

    // Initialize http client
    public MapboxGeocoder() {
        this.client = new OkHttpClient();
    }

    // Callback to accept geocoding result
    public interface GeocodeCallback {
        void onSuccess(double lng, double lat);

        // No success
        void onError(String error);
    }

    // Main method for converting address to coords
    public void geocode(String address, GeocodeCallback callback) {

        Log.d(TAG, "Geocoding address: " + address);

        // Url safe
        String encodedAddress = android.net.Uri.encode(address);

        // Constructing URL for Mapbox Geocoding API
        // Format: https://api.mapbox.com/geocoding/v5/mapbox.places/{query}.json
        String url = "https://api.mapbox.com/geocoding/v5/mapbox.places/" +
                    encodedAddress + ".json" +
                    "?access_token=" + MAPBOX_TOKEN +
                    "&limit=1" + // limit to one best result
                    "&bbox=" + BBOX; // box

        Log.d(TAG, "Request URL: " + url);

        // HTTP request
        Request request = new Request.Builder().url(url).build();

        // Async request
        client.newCall(request).enqueue(new Callback() {
            // Request failed
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Geocoding network error", e);
                callback.onError("Network error: " + e.getMessage());
            }

            // Got a response
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                Log.d(TAG, "Response code: " + response.code());

                String responseBody = response.body().string();
                Log.d(TAG, "Response body: " + responseBody);

                if (!response.isSuccessful()) {
                    Log.e(TAG, "Geocoding failed with code: " + response.code());
                    callback.onError("HTTP error: " + response.code());
                    return;
                }

                try {
                    // Reading JSON from response body
//                    String jsonResponse = response.body().string();

                    // Parse JSON to object
                    JSONObject json = new JSONObject(responseBody);

                    // Mapbox returns result in GeoJSON
                    JSONArray features = json.getJSONArray("features");

                    if (features.length() == 0) {
                        Log.w(TAG, "No results found for address: " + address);
                        callback.onError("Address not found: " + address);
                        return;
                    }

                    // First = best
                    JSONObject feature = features.getJSONObject(0);

                    JSONArray center = feature.getJSONArray("center");

                    // Getting longitude and latitude
                    double lng = center.getDouble(0);
                    double lat = center.getDouble(1);
                    Log.d(TAG, "Geocoded successfully: [" + lng + ", " + lat + "]");

                    callback.onSuccess(lng, lat);
                } catch (Exception e) {
                    callback.onError("Failed to parse response: " + e.getMessage());
                }
            }
        });

    }

}
