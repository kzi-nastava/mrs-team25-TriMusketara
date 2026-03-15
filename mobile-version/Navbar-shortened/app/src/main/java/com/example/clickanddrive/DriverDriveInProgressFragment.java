package com.example.clickanddrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.dtosample.requests.PanicRequest;
import com.example.clickanddrive.dtosample.responses.PanicResponse;
import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.requests.RideStopRequest;
import com.example.clickanddrive.dtosample.LocationDTO;
import com.example.clickanddrive.dtosample.responses.ScheduledRideResponse;
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

public class DriverDriveInProgressFragment extends Fragment {

    private static final String ARG_RIDE = "ARG_RIDE";

    private ScheduledRideResponse ride;
    private MapView mapView;
    private TextView tvOrigin, tvDestination, tvScheduledTime;
    private Button btnStopRide, btnFinishRide;
    private Button btnPanic;

    public static DriverDriveInProgressFragment newInstance(ScheduledRideResponse ride) {
        DriverDriveInProgressFragment fragment = new DriverDriveInProgressFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RIDE, ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_drive_in_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ride = (ScheduledRideResponse) getArguments().getSerializable(ARG_RIDE);

        mapView = view.findViewById(R.id.mapView);
        tvOrigin = view.findViewById(R.id.tvOrigin);
        tvDestination = view.findViewById(R.id.tvDestination);
        tvScheduledTime = view.findViewById(R.id.tvScheduledTime);
        btnStopRide = view.findViewById(R.id.btnStopRide);
        btnFinishRide = view.findViewById(R.id.btnFinishRide);
        btnPanic = view.findViewById(R.id.btnPanic);

        if (ride == null) {
            Toast.makeText(getContext(), "No active ride data", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        tvOrigin.setText("Origin: " + ride.getOrigin());
        tvDestination.setText("Destination: " + ride.getDestination());
        tvScheduledTime.setText("Scheduled: " + ride.getFormattedScheduledTime());

        btnStopRide.setOnClickListener(v -> onStopRide());

        btnFinishRide.setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Finish ride UI is ready, functionality can be connected later",
                        Toast.LENGTH_SHORT).show());

        btnPanic.setOnClickListener(v -> onPanic());

        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(19.8423, 45.2543))
                .zoom(12.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);
        mapView.getMapboxMap().loadStyleUri(Style.DARK, style -> drawRoute());
    }

    private void onStopRide() {
        if (ride == null) {
            Toast.makeText(getContext(), "No ride selected", Toast.LENGTH_SHORT).show();
            return;
        }

        Point center = mapView.getMapboxMap().getCameraState().getCenter();

        LocationDTO stopLocation = new LocationDTO(
                center.longitude(),
                center.latitude(),
                "Stopped at current location"
        );

        RideStopRequest request = new RideStopRequest(ride.isGuest(), stopLocation);

        btnStopRide.setEnabled(false);

        ClientUtils.rideService.stopRide(ride.getId(), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                btnStopRide.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ride stopped successfully", Toast.LENGTH_SHORT).show();

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, new HomeFragment())
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Failed to stop ride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;

                btnStopRide.setEnabled(true);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onPanic() {
        if (ride == null) {
            Toast.makeText(getContext(), "No active ride found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (SessionManager.userId == null) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Trigger PANIC")
                .setMessage("Are you sure you want to trigger PANIC?\n\nThis will notify administrators about a serious problem with the ride.")
                .setPositiveButton("Yes", (dialog, which) -> sendPanicRequest())
                .setNegativeButton("No", null)
                .show();
    }

    private void sendPanicRequest() {
        PanicRequest request = new PanicRequest(
                ride.getId(),
                ride.isGuest(),
                SessionManager.userId
        );

        btnPanic.setEnabled(false);

        ClientUtils.panicService.triggerPanic(request).enqueue(new Callback<PanicResponse>() {
            @Override
            public void onResponse(Call<PanicResponse> call, Response<PanicResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    btnPanic.setText("PANIC SENT");
                    btnPanic.setEnabled(false);

                    Toast.makeText(
                            getContext(),
                            "PANIC triggered successfully. Administrators have been notified.",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    btnPanic.setEnabled(true);
                    Toast.makeText(getContext(), "Failed to trigger PANIC", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PanicResponse> call, Throwable t) {
                if (!isAdded()) return;

                btnPanic.setEnabled(true);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute() {
        MapboxGeocoder geocoder = new MapboxGeocoder();
        MapboxDirections directions = new MapboxDirections();

        geocoder.geocode(ride.getOrigin(), new MapboxGeocoder.GeocodeCallback() {
            @Override
            public void onSuccess(double originLng, double originLat) {
                geocoder.geocode(ride.getDestination(), new MapboxGeocoder.GeocodeCallback() {
                    @Override
                    public void onSuccess(double destLng, double destLat) {
                        List<MapboxDirections.Coordinate> waypoints = new ArrayList<>();
                        waypoints.add(new MapboxDirections.Coordinate(originLng, originLat));
                        waypoints.add(new MapboxDirections.Coordinate(destLng, destLat));

                        directions.getRoute(waypoints, new MapboxDirections.DirectionsCallback() {
                            @Override
                            public void onSuccess(MapboxDirections.RouteResult result) {
                                if (!isAdded()) return;

                                requireActivity().runOnUiThread(() -> {
                                    MapHelper helper = new MapHelper(mapView);
                                    helper.drawPreCalculatedRoute(
                                            result.routeCoordinates,
                                            originLng,
                                            originLat,
                                            destLng,
                                            destLat,
                                            null
                                    );
                                });
                            }

                            @Override
                            public void onError(String error) {
                                if (!isAdded()) return;

                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(getContext(),
                                                "Failed to draw route",
                                                Toast.LENGTH_SHORT).show());
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        if (!isAdded()) return;

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(),
                                        "Destination geocoding failed",
                                        Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(),
                                "Origin geocoding failed",
                                Toast.LENGTH_SHORT).show());
            }
        });
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