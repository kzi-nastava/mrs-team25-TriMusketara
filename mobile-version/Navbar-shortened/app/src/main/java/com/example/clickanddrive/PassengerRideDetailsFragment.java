package com.example.clickanddrive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.clients.services.PassengerService;
import com.example.clickanddrive.dtosample.LocationDTO;
import com.example.clickanddrive.dtosample.responses.PassengerRideDetailsResponse;
import com.example.clickanddrive.dtosample.responses.RouteFromFavoritesResponse;
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

public class PassengerRideDetailsFragment extends Fragment {

    private static final String TAG = "PassengerRideDetails";
    private static final String ARG_RIDE_ID = "ride_id";

    private Long rideId;
    private MapView mapView;

    private TextView tvDriverName;
    private TextView tvDriverEmail;
    private TextView tvStatus;
    private TextView tvPrice;
    private TextView tvTime;
    private TextView tvRoute;
    private TextView tvRatings;
    private TextView tvReports;
    private TextView tvPetBaby;
    private Button btnRateRide;
    private ImageView ivFavoriteHeart;

    private Long currentRouteId = null;
    private boolean isFavorite = false;

    private boolean routeDrawn = false;
    private boolean mapStyleLoaded = false;

    public static PassengerRideDetailsFragment newInstance(Long rideId) {
        PassengerRideDetailsFragment fragment = new PassengerRideDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_passenger_ride_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }

        mapView = view.findViewById(R.id.mapView);
        tvDriverName = view.findViewById(R.id.tvDriverName);
        tvDriverEmail = view.findViewById(R.id.tvDriverEmail);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvTime = view.findViewById(R.id.tvTime);
        tvRoute = view.findViewById(R.id.tvRoute);
        tvRatings = view.findViewById(R.id.tvRatings);
        tvReports = view.findViewById(R.id.tvReports);
        tvPetBaby = view.findViewById(R.id.tvPetBaby);
        btnRateRide = view.findViewById(R.id.btnRateRide);
        ivFavoriteHeart = view.findViewById(R.id.ivFavoriteHeart);

        if (btnRateRide != null) {
            btnRateRide.setVisibility(View.GONE);
            btnRateRide.setOnClickListener(v -> openRateRideScreen());
        } else {
            Log.e(TAG, "btnRateRide is missing from fragment_passenger_ride_details.xml");
        }

        if (ivFavoriteHeart != null) {
            ivFavoriteHeart.setOnClickListener(v -> toggleFavorite());
        }

        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(19.8423, 45.2543))
                .zoom(11.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);

        mapView.getMapboxMap().loadStyleUri(Style.DARK, style -> {
            mapStyleLoaded = true;
            fetchRideDetails(true);
        });
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void fetchRideDetails(boolean allowRouteDraw) {
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

                            Log.d(TAG, "Ride details refreshed. status="
                                    + details.getStatus()
                                    + ", driverRating=" + details.getDriverRating()
                                    + ", vehicleRating=" + details.getVehicleRating());

                            bindData(details);

                            if (allowRouteDraw && mapStyleLoaded && !routeDrawn) {
                                drawRoute(details);
                            }
                        } else {
                            Log.e(TAG, "Failed to load ride details. Response code: " + response.code());
                            Toast.makeText(getContext(), "Failed to load ride details", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PassengerRideDetailsResponse> call, Throwable t) {
                        if (!isAdded()) return;

                        Log.e(TAG, "Network error while loading ride details", t);
                        Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bindData(PassengerRideDetailsResponse details) {
        currentRouteId = details.getRouteId();

        if (ivFavoriteHeart != null) {
            boolean hasRoute = currentRouteId != null;
            ivFavoriteHeart.setVisibility(hasRoute ? View.VISIBLE : View.GONE);
            View label = getView() != null ? getView().findViewById(R.id.tvFavoriteLabel) : null;
            if (label != null) label.setVisibility(hasRoute ? View.VISIBLE : View.GONE);

            // Check if route already in favorites, if is color the heart
            if (hasRoute) checkIfFavoriteAndUpdateHeart(currentRouteId);
        }

        tvDriverName.setText("Driver: " + safe(details.getDriverName()));
        tvDriverEmail.setText("Email: " + safe(details.getDriverEmail()));
        tvStatus.setText("Status: " + safe(details.getStatus()));
        tvPrice.setText("Price: " + (details.getTotalPrice() == null ? "-" : details.getTotalPrice() + " RSD"));
        tvTime.setText("Time: " + safe(details.getStartTime()) + " - " + safe(details.getEndTime()));

        String origin = details.getOrigin() != null ? safe(details.getOrigin().getAddress()) : "-";
        String destination = details.getDestination() != null ? safe(details.getDestination().getAddress()) : "-";
        tvRoute.setText("Route: " + origin + " -> " + destination);

        boolean alreadyRated = isAlreadyRated(details);

        if (alreadyRated) {
            tvRatings.setText("Ratings: Driver "
                    + details.getDriverRating()
                    + "/5, Vehicle "
                    + details.getVehicleRating()
                    + "/5");
        } else {
            tvRatings.setText("Ratings: no ratings");
        }

        if (details.getInconsistencyReports() != null && !details.getInconsistencyReports().isEmpty()) {
            StringBuilder sb = new StringBuilder();

            for (String report : details.getInconsistencyReports()) {
                sb.append("• ").append(report).append("\n");
            }

            tvReports.setText("Reports:\n" + sb.toString().trim());
        } else {
            tvReports.setText("Reports: none");
        }

        tvPetBaby.setText("Pet friendly: " + yesNo(details.isPetFriendly())
                + " | Baby friendly: " + yesNo(details.isBabyFriendly()));

        boolean canRate = isFinished(details.getStatus()) && !alreadyRated;

        Log.d(TAG, "canRate=" + canRate
                + ", isFinished=" + isFinished(details.getStatus())
                + ", alreadyRated=" + alreadyRated);

        if (btnRateRide != null) {
            btnRateRide.setVisibility(canRate ? View.VISIBLE : View.GONE);
            btnRateRide.setEnabled(canRate);
        }
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

    private void toggleFavorite() {
        if (currentRouteId == null) {
            Toast.makeText(getContext(), "Route data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = SessionManager.userId;

        if (isFavorite) {
            // Remove from favorites
            ClientUtils.passengerService.removeFromFavorites(userId, currentRouteId)
                    .enqueue(new Callback<Void>() {

                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!isAdded()) return;
                            if (response.isSuccessful()) {
                                isFavorite = false;
                                ivFavoriteHeart.setImageResource(R.drawable.heart);
                                Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(), "Error removing from favorites", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            if (!isAdded()) return;
                            Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Add to favorites
            ClientUtils.passengerService.addToFavorites(userId, currentRouteId)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!isAdded()) return;
                            if (response.isSuccessful()) {
                                isFavorite = true;
                                ivFavoriteHeart.setImageResource(R.drawable.heart_full_red);
                                Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error adding to favorites", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            if (!isAdded()) return;
                            Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void checkIfFavoriteAndUpdateHeart(Long routeId) {
        ClientUtils.passengerService.getFavoriteRoutes(SessionManager.userId, 0, 10000)
                .enqueue(new Callback<PassengerService.PageResponse<RouteFromFavoritesResponse>>() {
                    @Override
                    public void onResponse(Call<PassengerService.PageResponse<RouteFromFavoritesResponse>> call, Response<PassengerService.PageResponse<RouteFromFavoritesResponse>> response) {
                        if (!isAdded() || response.body() == null) return;

                        boolean found = response.body().getContent().stream()
                                .anyMatch(r -> routeId.equals(r.getId()));

                        isFavorite = found;
                        requireActivity().runOnUiThread(() -> ivFavoriteHeart.setImageResource(
                                found ? R.drawable.heart_full_red : R.drawable.heart
                        ));
                    }

                    @Override
                    public void onFailure(Call<PassengerService.PageResponse<RouteFromFavoritesResponse>> call, Throwable t) {
                        Log.w(TAG, "Could not check favorites status: " + t.getMessage());
                    }
                });
    }

    private void openRateRideScreen() {
        if (rideId == null) {
            Toast.makeText(getContext(), "Ride id is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, RateRideFragment.newInstance(rideId))
                .addToBackStack(null)
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

        mapStyleLoaded = false;
        routeDrawn = false;
        mapView = null;
    }
}
