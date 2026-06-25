package com.example.clickanddrive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.responses.AdminRideStateResponse;
import com.example.clickanddrive.dtosample.responses.RideTrackingResponse;
import com.example.clickanddrive.map.MapHelper;
import com.example.clickanddrive.map.MapboxDirections;
import com.example.clickanddrive.map.MapboxGeocoder;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActiveRidesFragment extends Fragment {

    private static final long REFRESH_INTERVAL_MS = 1_000L;

    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private final List<AdminRideStateResponse> activeRides = new ArrayList<>();

    private MapView mapView;
    private MapHelper mapHelper;
    private Spinner spActiveRides;
    private TextView tvRideStatus;
    private TextView tvEta;
    private TextView tvDriver;
    private TextView tvPassengers;
    private TextView tvRoute;
    private TextView tvEmptyState;
    private ArrayAdapter<AdminRideStateResponse> rideAdapter;

    private Long selectedRideId;
    private Long drawnRouteRideId;
    private boolean mapStyleLoaded = false;

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadActiveRides(false);
            refreshHandler.postDelayed(this, REFRESH_INTERVAL_MS);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_active_rides, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        spActiveRides = view.findViewById(R.id.spActiveRides);
        tvRideStatus = view.findViewById(R.id.tvRideStatus);
        tvEta = view.findViewById(R.id.tvEta);
        tvDriver = view.findViewById(R.id.tvDriver);
        tvPassengers = view.findViewById(R.id.tvPassengers);
        tvRoute = view.findViewById(R.id.tvRoute);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        rideAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                activeRides
        );
        rideAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActiveRides.setAdapter(rideAdapter);

        spActiveRides.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AdminRideStateResponse ride = activeRides.get(position);
                selectedRideId = ride.getRideId();
                bindRideInfo(ride);
                drawRouteIfNeeded(ride);
                fetchTracking();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(19.8423, 45.2543))
                .zoom(12.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);
        mapView.getMapboxMap().loadStyleUri(Style.DARK, style -> {
            mapStyleLoaded = true;
            mapHelper = new MapHelper(mapView);
            loadActiveRides(true);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshHandler.removeCallbacks(refreshRunnable);
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL_MS);
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    private void loadActiveRides(boolean showErrors) {
        ClientUtils.adminService.getActiveRides().enqueue(new Callback<List<AdminRideStateResponse>>() {
            @Override
            public void onResponse(Call<List<AdminRideStateResponse>> call,
                                   Response<List<AdminRideStateResponse>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    Long previousSelection = selectedRideId;
                    activeRides.clear();
                    activeRides.addAll(response.body());
                    rideAdapter.notifyDataSetChanged();
                    updateEmptyState();
                    restoreSelection(previousSelection);
                    fetchTracking();
                } else if (showErrors) {
                    Toast.makeText(getContext(), "Failed to load active rides", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminRideStateResponse>> call, Throwable t) {
                if (!isAdded()) return;

                if (showErrors) {
                    Toast.makeText(getContext(), "Network error while loading active rides", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void restoreSelection(Long previousSelection) {
        if (activeRides.isEmpty()) {
            selectedRideId = null;
            drawnRouteRideId = null;
            bindNoRide();
            return;
        }

        int selectedIndex = 0;
        if (previousSelection != null) {
            for (int i = 0; i < activeRides.size(); i++) {
                if (previousSelection.equals(activeRides.get(i).getRideId())) {
                    selectedIndex = i;
                    break;
                }
            }
        }

        spActiveRides.setSelection(selectedIndex);

        AdminRideStateResponse selectedRide = activeRides.get(selectedIndex);
        selectedRideId = selectedRide.getRideId();
        bindRideInfo(selectedRide);
        drawRouteIfNeeded(selectedRide);
    }

    private void updateEmptyState() {
        boolean empty = activeRides.isEmpty();
        tvEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        spActiveRides.setEnabled(!empty);
    }

    private void bindNoRide() {
        tvRideStatus.setText("Status: -");
        tvEta.setText("ETA: -");
        tvDriver.setText("Driver: -");
        tvPassengers.setText("Passengers: -");
        tvRoute.setText("Route: -");
    }

    private void bindRideInfo(AdminRideStateResponse ride) {
        tvRideStatus.setText("Status: " + safe(ride.getStatus()));
        tvDriver.setText("Driver: " + safe(ride.getDriverEmail()));
        tvPassengers.setText("Passengers: " + formatPassengers(ride.getPassengerEmails()));
        tvRoute.setText("Route: " + safe(ride.getOriginAddress()) + " -> " + safe(ride.getDestinationAddress()));
    }

    private void fetchTracking() {
        if (selectedRideId == null || mapHelper == null) {
            return;
        }

        ClientUtils.rideService.getRideTracking(selectedRideId).enqueue(new Callback<RideTrackingResponse>() {
            @Override
            public void onResponse(Call<RideTrackingResponse> call, Response<RideTrackingResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    RideTrackingResponse tracking = response.body();

                    tvRideStatus.setText("Status: " + safe(tracking.getStatus()));
                    tvEta.setText("ETA: " + tracking.getEstimatedTimeInMinutes()
                            + " min (" + tracking.getProgressPercent() + "%)");

                    if (tracking.getVehicleLocation() != null) {
                        mapHelper.updateVehicleMarker(
                                tracking.getVehicleLocation().getLongitude(),
                                tracking.getVehicleLocation().getLatitude(),
                                tracking.getEstimatedTimeInMinutes()
                        );
                    }
                }
            }

            @Override
            public void onFailure(Call<RideTrackingResponse> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Tracking update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRouteIfNeeded(AdminRideStateResponse ride) {
        if (!mapStyleLoaded || ride == null || ride.getRideId() == null || ride.getRideId().equals(drawnRouteRideId)) {
            return;
        }

        String origin = ride.getOriginAddress();
        String destination = ride.getDestinationAddress();

        if (origin == null || origin.trim().isEmpty() || destination == null || destination.trim().isEmpty()) {
            return;
        }

        geocodeAddressPair(ride.getRideId(), origin, destination);
    }

    private void geocodeAddressPair(Long rideId, String origin, String destination) {
        new MapboxGeocoder().geocode(origin, new MapboxGeocoder.GeocodeCallback() {
            @Override
            public void onSuccess(double originLng, double originLat) {
                new MapboxGeocoder().geocode(destination, new MapboxGeocoder.GeocodeCallback() {
                    @Override
                    public void onSuccess(double destLng, double destLat) {
                        requestDirections(rideId, originLng, originLat, destLng, destLat);
                    }

                    @Override
                    public void onError(String error) {
                        showMapError("Destination geocoding failed");
                    }
                });
            }

            @Override
            public void onError(String error) {
                showMapError("Origin geocoding failed");
            }
        });
    }

    private void requestDirections(Long rideId, double originLng, double originLat, double destLng, double destLat) {
        List<MapboxDirections.Coordinate> waypoints = new ArrayList<>();
        waypoints.add(new MapboxDirections.Coordinate(originLng, originLat));
        waypoints.add(new MapboxDirections.Coordinate(destLng, destLat));

        new MapboxDirections().getRoute(waypoints, new MapboxDirections.DirectionsCallback() {
            @Override
            public void onSuccess(MapboxDirections.RouteResult result) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    if (mapHelper == null) {
                        mapHelper = new MapHelper(mapView);
                    }

                    mapHelper.drawPreCalculatedRoute(
                            result.routeCoordinates,
                            originLng,
                            originLat,
                            destLng,
                            destLat,
                            null
                    );

                    drawnRouteRideId = rideId;
                });
            }

            @Override
            public void onError(String error) {
                showMapError("Map route error");
            }
        });
    }

    private void showMapError(String message) {
        if (!isAdded()) return;

        requireActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
        );
    }

    private String formatPassengers(List<String> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            return "-";
        }

        return String.join(", ", passengers);
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        refreshHandler.removeCallbacks(refreshRunnable);
        activeRides.clear();
        selectedRideId = null;
        drawnRouteRideId = null;
        mapStyleLoaded = false;
        mapView = null;
        mapHelper = null;
    }
}