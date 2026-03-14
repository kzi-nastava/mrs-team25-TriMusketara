package com.example.clickanddrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        btnStopRide.setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Stop ride UI is ready, functionality can be connected later",
                        Toast.LENGTH_SHORT).show());

        btnFinishRide.setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Finish ride UI is ready, functionality can be connected later",
                        Toast.LENGTH_SHORT).show());

        btnPanic.setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Panic button pressed (functionality will be implemented later)",
                        Toast.LENGTH_LONG).show()
        );

        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(19.8423, 45.2543))
                .zoom(12.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);
        mapView.getMapboxMap().loadStyleUri(Style.DARK, style -> drawRoute());
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