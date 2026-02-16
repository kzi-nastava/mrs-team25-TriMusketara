package com.example.clickanddrive;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class HomeFragment extends Fragment {

    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mapView = view.findViewById(R.id.mapView);

        // Set initial camera position
        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(19.8423, 45.2543))
                .zoom(12.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);

        mapView.getMapboxMap().loadStyleUri(Style.DARK, style -> {
            checkAndDrawIncomingRoute();
        });

        return view;
    }

    private void checkAndDrawIncomingRoute() {
        if (getArguments() != null && getArguments().containsKey("ROUTE_DATA")) {
            RouteData routeData = (RouteData) getArguments().getSerializable("ROUTE_DATA");

            if (routeData != null) {
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
                    Toast.makeText(getContext(), "Recalculating route (coordinates missing", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }
}