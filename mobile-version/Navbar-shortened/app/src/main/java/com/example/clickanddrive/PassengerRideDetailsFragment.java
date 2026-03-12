package com.example.clickanddrive;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideDetailsFragment extends Fragment {

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

    public static PassengerRideDetailsFragment newInstance(Long rideId) {
        PassengerRideDetailsFragment fragment = new PassengerRideDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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

        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(19.8423, 45.2543))
                .zoom(11.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);

        mapView.getMapboxMap().loadStyleUri(Style.DARK, style -> fetchRideDetails());
    }

    private void fetchRideDetails() {
        if (rideId == null) {
            Toast.makeText(getContext(), "Ride id is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<PassengerRideDetailsResponse> call =
                ClientUtils.passengerService.getRideDetails(rideId);

        call.enqueue(new Callback<PassengerRideDetailsResponse>() {
            @Override
            public void onResponse(Call<PassengerRideDetailsResponse> call,
                                   Response<PassengerRideDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PassengerRideDetailsResponse details = response.body();
                    bindData(details);
                    drawRoute(details);
                } else {
                    Log.e("PASSENGER_DETAILS", "Response code: " + response.code());
                    Toast.makeText(getContext(), "Failed to load ride details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PassengerRideDetailsResponse> call, Throwable t) {
                Log.e("PASSENGER_DETAILS", "Error: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindData(PassengerRideDetailsResponse details) {
        tvDriverName.setText("Driver: " + safe(details.getDriverName()));
        tvDriverEmail.setText("Email: " + safe(details.getDriverEmail()));
        tvStatus.setText("Status: " + safe(details.getStatus()));
        tvPrice.setText("Price: " + (details.getTotalPrice() == null ? "-" : details.getTotalPrice() + " RSD"));
        tvTime.setText("Time: " + safe(details.getStartTime()) + " - " + safe(details.getEndTime()));

        String origin = details.getOrigin() != null ? safe(details.getOrigin().getAddress()) : "-";
        String destination = details.getDestination() != null ? safe(details.getDestination().getAddress()) : "-";
        tvRoute.setText("Route: " + origin + " -> " + destination);

        if (details.getDriverRating() > 0 || details.getVehicleRating() > 0) {
            tvRatings.setText("Ratings: Driver " + details.getDriverRating() + "/5, Vehicle " + details.getVehicleRating() + "/5");
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

        tvPetBaby.setText("Pet friendly: " + yesNo(details.isPetFriendly()) +
                " | Baby friendly: " + yesNo(details.isBabyFriendly()));
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

        MapboxDirections directions = new MapboxDirections();
        directions.getRoute(waypoints, new MapboxDirections.DirectionsCallback() {
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
        return value == null ? "-" : value;
    }

    private String yesNo(boolean value) {
        return value ? "Yes" : "No";
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null) mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) mapView.onDestroy();
    }
}