package com.example.clickanddrive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.LocationDTO;
import com.example.clickanddrive.dtosample.responses.PassengerRideDetailsResponse;
import com.example.clickanddrive.map.MapHelper;
import com.example.clickanddrive.map.MapboxDirections;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideInProgressFragment extends Fragment {

    private static final String ARG_RIDE_ID = "ride_id";
    private static final long REFRESH_INTERVAL_MS = 5_000L;

    private Long rideId;

    private MapView mapView;

    private TextView tvRideStatus;
    private TextView tvEta;
    private TextView tvDriverName;
    private TextView tvDriverEmail;
    private TextView tvRoute;
    private TextView tvPrice;
    private TextView tvPetBaby;

    private Button btnRateRide;
    private Button btnBackToHome;

    private boolean mapStyleLoaded = false;
    private boolean routeDrawn = false;

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

        btnRateRide.setVisibility(View.GONE);
        btnRateRide.setOnClickListener(v -> openRateRideScreen());

        btnBackToHome.setOnClickListener(v -> openHomeScreen());

        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(19.8423, 45.2543))
                .zoom(11.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);

        mapView.getMapboxMap().loadStyleUri(Style.DARK, style -> {
            mapStyleLoaded = true;
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

    private void bindData(PassengerRideDetailsResponse details) {
        String status = safe(details.getStatus());

        tvRideStatus.setText("Status: " + status);
        tvEta.setText("ETA: " + getEtaText(status));

        tvDriverName.setText("Driver: " + safe(details.getDriverName()));
        tvDriverEmail.setText("Email: " + safe(details.getDriverEmail()));

        String origin = details.getOrigin() != null ? safe(details.getOrigin().getAddress()) : "-";
        String destination = details.getDestination() != null ? safe(details.getDestination().getAddress()) : "-";

        tvRoute.setText("Route: " + origin + " → " + destination);

        tvPrice.setText("Price: " + (details.getTotalPrice() == null ? "-" : details.getTotalPrice() + " RSD"));

        tvPetBaby.setText("Pet friendly: " + yesNo(details.isPetFriendly())
                + " | Baby friendly: " + yesNo(details.isBabyFriendly()));

        boolean alreadyRated = isAlreadyRated(details);
        boolean canRate = isFinished(status) && !alreadyRated;

        btnRateRide.setVisibility(canRate ? View.VISIBLE : View.GONE);
        btnRateRide.setEnabled(canRate);

        if (isFinished(status)) {
            refreshHandler.removeCallbacks(refreshRunnable);
            tvEta.setText("ETA: Ride finished");
        }
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
                    MapHelper mapHelper = new MapHelper(mapView);
                    mapHelper.drawPreCalculatedRoute(
                            result.routeCoordinates,
                            origin.getLongitude(),
                            origin.getLatitude(),
                            destination.getLongitude(),
                            destination.getLatitude(),
                            null
                    );

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
        mapView = null;
    }
}