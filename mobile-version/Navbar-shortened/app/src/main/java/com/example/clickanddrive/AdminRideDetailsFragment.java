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
import com.example.clickanddrive.dtosample.responses.AdminRideDetailsResponse;
import com.example.clickanddrive.dtosample.responses.UserProfileResponseDTO;
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

public class AdminRideDetailsFragment extends Fragment {

    private static final String ARG_RIDE_ID = "ride_id";

    private Long rideId;
    private MapView mapView;

    private TextView tvDriverName;
    private TextView tvStatus;
    private TextView tvPrice;
    private TextView tvTime;
    private TextView tvRoute;
    private TextView tvCancellation;
    private TextView tvPanic;
    private TextView tvPassengers;
    private TextView tvReview;

    public static AdminRideDetailsFragment newInstance(Long rideId) {
        AdminRideDetailsFragment fragment = new AdminRideDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_ride_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }

        mapView = view.findViewById(R.id.mapView);
        tvDriverName = view.findViewById(R.id.tvDriverName);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvTime = view.findViewById(R.id.tvTime);
        tvRoute = view.findViewById(R.id.tvRoute);
        tvCancellation = view.findViewById(R.id.tvCancellation);
        tvPanic = view.findViewById(R.id.tvPanic);
        tvPassengers = view.findViewById(R.id.tvPassengers);
        tvReview = view.findViewById(R.id.tvReview);

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

        ClientUtils.adminService.getRideDetails(rideId).enqueue(new Callback<AdminRideDetailsResponse>() {
            @Override
            public void onResponse(Call<AdminRideDetailsResponse> call, Response<AdminRideDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AdminRideDetailsResponse details = response.body();
                    bindData(details);
                    drawRoute(details);
                } else {
                    Log.e("ADMIN_DETAILS", "Response code: " + response.code());
                    Toast.makeText(getContext(), "Failed to load admin ride details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminRideDetailsResponse> call, Throwable t) {
                Log.e("ADMIN_DETAILS", "Error: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindData(AdminRideDetailsResponse details) {
        tvDriverName.setText("Driver: " + safe(details.getDriverName()));
        tvStatus.setText("Status: " + safe(details.getStatus()));
        tvPrice.setText("Price: " + details.getPrice() + " RSD");
        tvTime.setText("Time: " + safe(details.getStartTime()) + " - " + safe(details.getEndTime()));

        String origin = details.getOrigin() != null ? safe(details.getOrigin().getAddress()) : safe(details.getStartAddress());
        String destination = details.getDestination() != null ? safe(details.getDestination().getAddress()) : safe(details.getEndAddress());
        tvRoute.setText("Route: " + origin + " -> " + destination);

        String cancellationText = details.isCanceled()
                ? "Cancelled: Yes, by " + safe(details.getCanceledBy())
                : "Cancelled: No";
        tvCancellation.setText(cancellationText);

        tvPanic.setText("Panic pressed: " + (details.isPanicTriggered() ? "Yes" : "No"));

        if (details.getPassengers() != null && !details.getPassengers().isEmpty()) {
            StringBuilder sb = new StringBuilder("Passengers:\n");
            for (UserProfileResponseDTO passenger : details.getPassengers()) {
                String fullName = safe(passenger.getName()) + " " + safe(passenger.getSurname());
                sb.append("• ").append(fullName.trim())
                        .append(" (").append(safe(passenger.getEmail())).append(")")
                        .append("\n");
            }
            tvPassengers.setText(sb.toString().trim());
        } else {
            tvPassengers.setText("Passengers: none");
        }

        if (details.getRating() != null || details.getComment() != null) {
            String review = "Review: " +
                    (details.getRating() != null ? details.getRating() + "/5" : "no rating");
            if (details.getComment() != null && !details.getComment().trim().isEmpty()) {
                review += "\nComment: " + details.getComment();
            }
            tvReview.setText(review);
        } else {
            tvReview.setText("Review: none");
        }
    }

    private void drawRoute(AdminRideDetailsResponse details) {
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