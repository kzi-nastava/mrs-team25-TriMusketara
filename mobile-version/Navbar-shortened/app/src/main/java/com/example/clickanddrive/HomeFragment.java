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

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.LocationDTO;
import com.example.clickanddrive.map.MapHelper;
import com.example.clickanddrive.map.MapboxDirections;
import com.example.clickanddrive.map.MapboxGeocoder;
import com.example.clickanddrive.models.RouteData;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MapView mapView;

    private View guestRidePanel;
    private TextView tvEtaValue;
    private Button btnCancelGuestRide;

    private RouteData routeData;
    private boolean isGuestRide = false;
    private Long guestRideId = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mapView = view.findViewById(R.id.mapView);
        guestRidePanel = view.findViewById(R.id.guestRidePanel);
        tvEtaValue = view.findViewById(R.id.tvEtaValue);
        btnCancelGuestRide = view.findViewById(R.id.btnCancelGuestRide);

        readArguments();
        setupGuestRideUi();

        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(19.8423, 45.2543))
                .zoom(12.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);

        mapView.getMapboxMap().loadStyleUri(Style.DARK, style -> checkAndDrawIncomingRoute());

        btnCancelGuestRide.setOnClickListener(v -> cancelGuestRide());

        return view;
    }

    private void readArguments() {
        Bundle args = getArguments();
        if (args == null) return;

        if (args.containsKey("ROUTE_DATA")) {
            routeData = (RouteData) args.getSerializable("ROUTE_DATA");
        }

        isGuestRide = args.getBoolean("IS_GUEST_RIDE", false);

        if (args.containsKey("GUEST_RIDE_ID")) {
            guestRideId = args.getLong("GUEST_RIDE_ID");
        }
    }

    private void setupGuestRideUi() {
        if (isGuestRide && guestRideId != null && routeData != null) {
            guestRidePanel.setVisibility(View.VISIBLE);

            int eta = routeData.getDurationMinutes();
            if (eta > 0) {
                tvEtaValue.setText(eta + " min");
            } else {
                tvEtaValue.setText("-");
            }
        } else {
            guestRidePanel.setVisibility(View.GONE);
        }
    }

    private void checkAndDrawIncomingRoute() {
        if (routeData == null) return;

        MapHelper mapHelper = new MapHelper(mapView);

        if (routeData.getRouteCoordinates() != null && !routeData.getRouteCoordinates().isEmpty()) {
            mapHelper.drawPreCalculatedRoute(
                    routeData.getRouteCoordinates(),
                    routeData.getOriginLng(),
                    routeData.getOriginLat(),
                    routeData.getDestinationLng(),
                    routeData.getDestinationLat(),
                    routeData.getStopLocations()
            );
        } else {
            recalculateRouteFromAddresses();
        }
    }

    private void recalculateRouteFromAddresses() {
        if (routeData == null) return;

        List<String> addresses = new ArrayList<>();

        if (routeData.getOrigin() != null && !routeData.getOrigin().trim().isEmpty()) {
            addresses.add(routeData.getOrigin().trim());
        }

        if (routeData.getStops() != null) {
            for (String stop : routeData.getStops()) {
                if (stop != null && !stop.trim().isEmpty()) {
                    addresses.add(stop.trim());
                }
            }
        }

        if (routeData.getDestination() != null && !routeData.getDestination().trim().isEmpty()) {
            addresses.add(routeData.getDestination().trim());
        }

        if (addresses.size() < 2) {
            Toast.makeText(getContext(), "Not enough route data to draw route", Toast.LENGTH_SHORT).show();
            return;
        }

        geocodeAddressesSequentially(addresses, 0, new ArrayList<>());
    }

    private void geocodeAddressesSequentially(List<String> addresses,
                                              int index,
                                              List<MapboxDirections.Coordinate> coordinates) {
        if (index >= addresses.size()) {
            requestDirections(addresses, coordinates);
            return;
        }

        String address = addresses.get(index);

        new MapboxGeocoder().geocode(address, new MapboxGeocoder.GeocodeCallback() {
            @Override
            public void onSuccess(double lng, double lat) {
                coordinates.add(new MapboxDirections.Coordinate(lng, lat));
                geocodeAddressesSequentially(addresses, index + 1, coordinates);
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(),
                                "Geocoding failed for: " + address,
                                Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void requestDirections(List<String> addresses, List<MapboxDirections.Coordinate> waypoints) {
        if (waypoints.size() < 2) {
            if (!isAdded()) return;

            requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Unable to calculate route", Toast.LENGTH_SHORT).show());
            return;
        }

        new MapboxDirections().getRoute(waypoints, new MapboxDirections.DirectionsCallback() {
            @Override
            public void onSuccess(MapboxDirections.RouteResult result) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    routeData.setRouteCoordinates(result.routeCoordinates);
                    routeData.setDistanceKm(result.distanceKm);

                    if (routeData.getDurationMinutes() <= 0) {
                        routeData.setDurationMinutes(result.durationMinutes);
                        if (isGuestRide) {
                            tvEtaValue.setText(result.durationMinutes + " min");
                        }
                    }

                    routeData.setOriginLng(waypoints.get(0).lng);
                    routeData.setOriginLat(waypoints.get(0).lat);

                    routeData.setDestinationLng(waypoints.get(waypoints.size() - 1).lng);
                    routeData.setDestinationLat(waypoints.get(waypoints.size() - 1).lat);

                    List<LocationDTO> stopLocations = new ArrayList<>();
                    if (waypoints.size() > 2) {
                        for (int i = 1; i < waypoints.size() - 1; i++) {
                            stopLocations.add(new LocationDTO(
                                    waypoints.get(i).lng,
                                    waypoints.get(i).lat,
                                    addresses.get(i)
                            ));
                        }
                    }
                    routeData.setStopLocations(stopLocations);

                    MapHelper mapHelper = new MapHelper(mapView);
                    mapHelper.drawPreCalculatedRoute(
                            result.routeCoordinates,
                            routeData.getOriginLng(),
                            routeData.getOriginLat(),
                            routeData.getDestinationLng(),
                            routeData.getDestinationLat(),
                            stopLocations
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

    private void cancelGuestRide() {
        if (guestRideId == null) {
            Toast.makeText(getContext(), "No guest ride to cancel", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCancelGuestRide.setEnabled(false);

        ClientUtils.guestRideService.cancelGuestRide(guestRideId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                btnCancelGuestRide.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ride cancelled", Toast.LENGTH_SHORT).show();

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, new HomeFragment())
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Failed to cancel ride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;

                btnCancelGuestRide.setEnabled(true);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
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