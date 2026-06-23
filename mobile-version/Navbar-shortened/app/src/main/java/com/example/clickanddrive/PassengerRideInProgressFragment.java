package com.example.clickanddrive;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.LocationDTO;
import com.example.clickanddrive.dtosample.requests.InconsistencyReportRequest;
import com.example.clickanddrive.dtosample.responses.PassengerRideDetailsResponse;
import com.example.clickanddrive.dtosample.responses.RideTrackingResponse;
import com.example.clickanddrive.map.MapHelper;
import com.example.clickanddrive.map.MapboxDirections;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideInProgressFragment extends Fragment {

    private static final String ARG_RIDE_ID = "ride_id";
    private static final long REFRESH_INTERVAL_MS = 1_000L;

    private Long rideId;

    private MapView mapView;
    private MapHelper mapHelper;

    private TextView tvRideStatus;
    private TextView tvEta;
    private TextView tvDriverName;
    private TextView tvDriverEmail;
    private TextView tvRoute;
    private TextView tvPrice;
    private TextView tvPetBaby;

    private Button btnRateRide;
    private Button btnBackToHome;
    private Button btnReportInconsistency;
    private Button btnPanic;

    private boolean mapStyleLoaded = false;
    private boolean routeDrawn = false;

    private final List<MapboxDirections.Coordinate> trackingRouteCoordinates = new ArrayList<>();
    private int mapboxDurationMinutes = 0;
    private final Handler refreshHandler = new Handler(Looper.getMainLooper());

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            fetchRideInProgress(false);
            refreshHandler.postDelayed(this, REFRESH_INTERVAL_MS);
        }
    };

    public static PassengerRideInProgressFragment newInstance(Long rideId) {
        PassengerRideInProgressFragment fragment = new PassengerRideInProgressFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_in_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }

        mapView = view.findViewById(R.id.mapView);

        tvRideStatus = view.findViewById(R.id.tvRideStatus);
        tvEta = view.findViewById(R.id.tvEta);
        tvDriverName = view.findViewById(R.id.tvDriverName);
        tvDriverEmail = view.findViewById(R.id.tvDriverEmail);
        tvRoute = view.findViewById(R.id.tvRoute);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvPetBaby = view.findViewById(R.id.tvPetBaby);

        btnRateRide = view.findViewById(R.id.btnRateRide);
        btnBackToHome = view.findViewById(R.id.btnBackToHome);
        btnReportInconsistency = view.findViewById(R.id.btnReportInconsistency);
        btnPanic = view.findViewById(R.id.btnPanic);

        btnBackToHome.setVisibility(View.GONE);
        btnRateRide.setVisibility(View.GONE);
        btnRateRide.setOnClickListener(v -> openRateRideScreen());

        btnBackToHome.setOnClickListener(v -> openHomeScreen());
        btnReportInconsistency.setOnClickListener(v -> showInconsistencyDialog());
        btnPanic.setOnClickListener(v -> showDummyPanicNotification());

        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(19.8423, 45.2543))
                .zoom(13.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);

        mapView.getMapboxMap().loadStyleUri(Style.DARK, style -> {
            mapStyleLoaded = true;
            mapHelper = new MapHelper(mapView);
            fetchRideInProgress(true);
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

    private void fetchRideInProgress(boolean allowRouteDraw) {
        if (rideId == null) {
            Toast.makeText(getContext(), "Ride id is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ClientUtils.passengerService.getRideDetails(rideId)
                .enqueue(new Callback<PassengerRideDetailsResponse>() {
                    @Override
                    public void onResponse(Call<PassengerRideDetailsResponse> call,
                                           Response<PassengerRideDetailsResponse> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            PassengerRideDetailsResponse details = response.body();

                            bindData(details);

                            if (allowRouteDraw && mapStyleLoaded && !routeDrawn) {
                                drawRoute(details);
                            }

                            fetchTracking();
                        } else {
                            Toast.makeText(getContext(), "Failed to load current ride", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PassengerRideDetailsResponse> call, Throwable t) {
                        if (!isAdded()) return;

                        Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchTracking() {
        if (rideId == null || mapHelper == null) {
            return;
        }

        ClientUtils.rideService.getRideTracking(rideId)
                .enqueue(new Callback<RideTrackingResponse>() {
                    @Override
                    public void onResponse(Call<RideTrackingResponse> call,
                                           Response<RideTrackingResponse> response) {
                        if (!isAdded()) {
                            return;
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            RideTrackingResponse tracking = response.body();

                            int etaFromMapbox = calculateEtaFromMapboxDuration(tracking.getProgressPercent());

                            tvEta.setText("ETA: "
                                    + etaFromMapbox
                                    + " min ("
                                    + tracking.getProgressPercent()
                                    + "%)");

                            if (!trackingRouteCoordinates.isEmpty()) {
                                updateVehicleMarkerOnRoute(
                                        tracking.getProgressPercent(),
                                        etaFromMapbox
                                );
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RideTrackingResponse> call, Throwable t) {
                        if (!isAdded()) {
                            return;
                        }

                        Toast.makeText(getContext(), "Tracking update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateVehicleMarkerOnRoute(double progressPercent, int etaMinutes) {
        if (mapHelper == null || trackingRouteCoordinates.isEmpty()) {
            return;
        }

        double normalizedProgress = Math.max(0.0, Math.min(100.0, progressPercent)) / 100.0;

        int lastIndex = trackingRouteCoordinates.size() - 1;
        int routeIndex = (int) Math.round(normalizedProgress * lastIndex);

        routeIndex = Math.max(0, Math.min(lastIndex, routeIndex));

        MapboxDirections.Coordinate coordinate = trackingRouteCoordinates.get(routeIndex);

        mapHelper.updateVehicleMarker(coordinate.lng, coordinate.lat, etaMinutes);
    }

    private int calculateEtaFromMapboxDuration(double progressPercent) {
        if (mapboxDurationMinutes <= 0) {
            return 0;
        }

        double normalizedProgress = Math.max(0.0, Math.min(100.0, progressPercent)) / 100.0;

        return Math.max(
                0,
                (int) Math.ceil(mapboxDurationMinutes * (1.0 - normalizedProgress))
        );
    }

    private void bindData(PassengerRideDetailsResponse details) {
        String status = safe(details.getStatus());

        tvRideStatus.setText("Status: " + status);
        tvEta.setText("ETA: " + getEtaText(status));

        tvDriverName.setText("Driver: " + safe(details.getDriverName()));
        tvDriverEmail.setText("Email: " + safe(details.getDriverEmail()));

        String origin = details.getOrigin() != null ? safe(details.getOrigin().getAddress()) : "-";
        String destination = details.getDestination() != null ? safe(details.getDestination().getAddress()) : "-";

        tvRoute.setText("Route: " + origin + " -> " + destination);

        tvPrice.setText("Price: " + (details.getTotalPrice() == null ? "-" : details.getTotalPrice() + " RSD"));

        tvPetBaby.setText("Pet friendly: " + yesNo(details.isPetFriendly())
                + " | Baby friendly: " + yesNo(details.isBabyFriendly()));

        boolean finished = isFinished(status);
        boolean alreadyRated = isAlreadyRated(details);
        boolean canRate = finished && !alreadyRated;

        btnReportInconsistency.setVisibility(finished ? View.GONE : View.VISIBLE);
        btnReportInconsistency.setEnabled(!finished);

        btnPanic.setVisibility(finished ? View.GONE : View.VISIBLE);
        btnPanic.setEnabled(!finished);

        btnRateRide.setVisibility(canRate ? View.VISIBLE : View.GONE);
        btnRateRide.setEnabled(canRate);

        btnBackToHome.setVisibility(finished ? View.VISIBLE : View.GONE);
        btnBackToHome.setEnabled(finished);

        if (finished) {
            refreshHandler.removeCallbacks(refreshRunnable);
            tvEta.setText("ETA: Ride finished");
        }
    }

    private void showDummyPanicNotification() {
        String[] messages = {
                "PANIC request sent. Support team has been notified.",
                "Emergency alert simulated successfully.",
                "PANIC pressed. This will be implemented by another team member."
        };

        int index = new Random().nextInt(messages.length);

        new AlertDialog.Builder(requireContext())
                .setTitle("PANIC")
                .setMessage(messages[index])
                .setPositiveButton("OK", null)
                .show();
    }

    private void showInconsistencyDialog() {
        if (rideId == null) {
            Toast.makeText(getContext(), "Ride id is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText input = new EditText(requireContext());
        input.setHint("Describe what is wrong with the route");

        new AlertDialog.Builder(requireContext())
                .setTitle("Report inconsistency")
                .setMessage("If the driver is taking an inappropriate route, describe the problem.")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String reason = input.getText().toString().trim();

                    if (reason.isEmpty()) {
                        Toast.makeText(getContext(), "Reason is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sendInconsistencyReport(reason);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendInconsistencyReport(String reason) {
        if (rideId == null) {
            Toast.makeText(getContext(), "Ride id is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        InconsistencyReportRequest request = new InconsistencyReportRequest(reason);

        btnReportInconsistency.setEnabled(false);

        ClientUtils.rideService.reportInconsistency(rideId, request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!isAdded()) {
                            return;
                        }

                        btnReportInconsistency.setEnabled(true);

                        if (response.isSuccessful()) {
                            Toast.makeText(
                                    getContext(),
                                    "Inconsistency report sent",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    getContext(),
                                    "Failed to send report: " + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (!isAdded()) {
                            return;
                        }

                        btnReportInconsistency.setEnabled(true);
                        Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getEtaText(String status) {
        if (status == null) {
            return "-";
        }

        String normalized = status.trim().toUpperCase(Locale.ROOT);

        if (normalized.equals("SCHEDULED")) {
            return "Waiting for driver";
        }

        if (normalized.equals("ACCEPTED")) {
            return "Driver is assigned";
        }

        if (normalized.equals("IN_PROGRESS") || normalized.equals("STARTED") || normalized.contains("PROGRESS")) {
            return "Ride in progress";
        }

        if (isFinished(status)) {
            return "Ride finished";
        }

        return "Calculating...";
    }

    private boolean isAlreadyRated(PassengerRideDetailsResponse details) {
        return details.getDriverRating() > 0 && details.getVehicleRating() > 0;
    }

    private boolean isFinished(String status) {
        if (status == null) {
            return false;
        }

        String normalized = status.trim().toUpperCase(Locale.ROOT);

        return normalized.equals("FINISHED")
                || normalized.equals("COMPLETED")
                || normalized.equals("DONE")
                || normalized.equals("ENDED")
                || normalized.contains("FINISH")
                || normalized.contains("COMPLETE");
    }

    private void openRateRideScreen() {
        if (rideId == null) {
            Toast.makeText(getContext(), "Ride id is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        refreshHandler.removeCallbacks(refreshRunnable);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, RateRideFragment.newInstance(rideId))
                .addToBackStack(null)
                .commit();
    }

    private void openHomeScreen() {
        refreshHandler.removeCallbacks(refreshRunnable);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, new HomeFragment())
                .commit();
    }

    private void drawRoute(PassengerRideDetailsResponse details) {
        LocationDTO origin = details.getOrigin();
        LocationDTO destination = details.getDestination();

        if (origin == null || destination == null) {
            Toast.makeText(getContext(), "Route coordinates are missing", Toast.LENGTH_SHORT).show();
            return;
        }

        List<MapboxDirections.Coordinate> waypoints = new ArrayList<>();
        waypoints.add(new MapboxDirections.Coordinate(origin.getLongitude(), origin.getLatitude()));
        waypoints.add(new MapboxDirections.Coordinate(destination.getLongitude(), destination.getLatitude()));

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
                            origin.getLongitude(),
                            origin.getLatitude(),
                            destination.getLongitude(),
                            destination.getLatitude(),
                            null
                    );

                    trackingRouteCoordinates.clear();
                    trackingRouteCoordinates.addAll(result.routeCoordinates);
                    mapboxDurationMinutes = result.durationMinutes;

                    routeDrawn = true;
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Map route error: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String yesNo(boolean value) {
        return value ? "Yes" : "No";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        refreshHandler.removeCallbacks(refreshRunnable);

        mapStyleLoaded = false;
        routeDrawn = false;
        trackingRouteCoordinates.clear();

        mapView = null;
        mapHelper = null;
    }
}