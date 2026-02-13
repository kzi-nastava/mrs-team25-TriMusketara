package com.example.clickanddrive.map;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;

import com.example.clickanddrive.R;
import com.example.clickanddrive.dtosample.LocationDTO;
import com.mapbox.bindgen.Value;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapHelper {

    private static final String TAG = "MapHelper";

    private final MapView mapView;
    private final Handler mainHandler;

    private static final String ROUTE_SOURCE_ID = "route-source";
    private static final String ROUTE_LAYER_ID = "route-layer";
    private static final String ROUTE_COLOR = "#F5CB5C";
    private static final float ROUTE_WIDTH = 5.0f;
    private static final String ICON_ORIGIN = "icon-origin";
    private static final String ICON_STOP = "icon-stop";
    private static final String ICON_DEST = "icon-dest";
    private com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager pointAnnotationManager;


    private CircleAnnotationManager circleAnnotationManager;

    public MapHelper(MapView mapView) {
        this.mapView = mapView;
        this.mainHandler = new Handler(Looper.getMainLooper());
        initializeAnnotationManager();
    }

    private void initializeAnnotationManager() {
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        pointAnnotationManager = com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt.createPointAnnotationManager(
                annotationPlugin,
                new AnnotationConfig()
        );
    }

    public void drawPreCalculatedRoute(
            List<MapboxDirections.Coordinate> routeCoordinates,
            double originLng,
            double originLat,
            double destLng,
            double destLat,
            List<LocationDTO> stopLocations) {

        mainHandler.post(() -> {
            // Draw route line
            drawRouteLine(routeCoordinates);

            // Markers
            List<MapboxDirections.Coordinate> markerCoords = new ArrayList<>();
            markerCoords.add(new MapboxDirections.Coordinate(originLng, originLat));

            if (stopLocations != null) {
                for (LocationDTO stop : stopLocations) {
                    markerCoords.add(new MapboxDirections.Coordinate(
                            stop.getLongitude(),
                            stop.getLatitude()
                    ));
                }
            }

            markerCoords.add(new MapboxDirections.Coordinate(destLng, destLat));
            addRouteMarkers(markerCoords);

            fitCameraToRoute(routeCoordinates);
        });
    }

    private void drawRouteLine(List<MapboxDirections.Coordinate> routeCoordinates) {
        List<Point> points = new ArrayList<>();
        for (MapboxDirections.Coordinate coord : routeCoordinates) {
            points.add(Point.fromLngLat(coord.lng, coord.lat));
        }

        LineString lineString = LineString.fromLngLats(points);
        Feature feature = Feature.fromGeometry(lineString);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);

        mapView.getMapboxMap().getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                try {
                    if (style.styleLayerExists(ROUTE_LAYER_ID)) {
                        style.removeStyleLayer(ROUTE_LAYER_ID);
                    }
                    if (style.styleSourceExists(ROUTE_SOURCE_ID)) {
                        style.removeStyleSource(ROUTE_SOURCE_ID);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "No existing route to remove");
                }

                try {
                    String sourceJson = String.format(
                            "{\"type\":\"geojson\",\"data\":%s}",
                            featureCollection.toJson()
                    );

                    JSONObject sourceObject = new JSONObject(sourceJson);
                    Value sourceValue = Value.fromJson(sourceObject.toString()).getValue();

                    style.addStyleSource(ROUTE_SOURCE_ID, sourceValue);

                    String layerJson = String.format(
                            "{\"id\":\"%s\",\"type\":\"line\",\"source\":\"%s\"," +
                                    "\"paint\":{\"line-color\":\"%s\",\"line-width\":%f," +
                                    "\"line-join\":\"round\",\"line-cap\":\"round\"}}",
                            ROUTE_LAYER_ID, ROUTE_SOURCE_ID, ROUTE_COLOR, ROUTE_WIDTH
                    );

                    JSONObject layerObject = new JSONObject(layerJson);
                    Value layerValue = Value.fromJson(layerObject.toString()).getValue();

                    style.addStyleLayer(layerValue, null);

                    Log.d(TAG, "Route line drawn successfully on map");

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to create route: " + e.getMessage());
                }
            }
        });
    }

    private void addRouteMarkers(List<MapboxDirections.Coordinate> coordinates) {
        pointAnnotationManager.deleteAll();

        mapView.getMapboxMap().getStyle(style -> {
            style.addImage(ICON_ORIGIN, drawableToBitmap(R.drawable.taxi_map_svg));
            style.addImage(ICON_STOP, drawableToBitmap(R.drawable.stop_map_svg));
            style.addImage(ICON_DEST, drawableToBitmap(R.drawable.destination_map_svg));

            addMarker(coordinates.get(0).lng, coordinates.get(0).lat, ICON_ORIGIN);

            for (int i = 1; i < coordinates.size() - 1; i++) {
                addMarker(coordinates.get(i).lng, coordinates.get(i).lat, ICON_STOP);
            }

            addMarker(coordinates.get(coordinates.size() - 1).lng, coordinates.get(coordinates.size() - 1).lat, ICON_DEST);
        });
    }

    private android.graphics.Bitmap drawableToBitmap(int drawableId) {
        android.graphics.drawable.Drawable drawable = androidx.core.content.ContextCompat.getDrawable(mapView.getContext(), drawableId);
        if (drawable == null) return null;
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void addMarker(double lng, double lat, String iconId) {
        com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions options =
                new com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(lng, lat))
                        .withIconImage(iconId)
                        .withIconSize(1.2);

        pointAnnotationManager.create(options);
    }

    private void fitCameraToRoute(List<MapboxDirections.Coordinate> routeCoordinates) {
        if (routeCoordinates == null || routeCoordinates.isEmpty()) return;

        List<Point> points = new ArrayList<>();
        for (MapboxDirections.Coordinate c : routeCoordinates) {
            points.add(Point.fromLngLat(c.lng, c.lat));
        }

        com.mapbox.maps.CameraOptions cameraOptions = mapView.getMapboxMap().cameraForCoordinates(
                points,
                new com.mapbox.maps.EdgeInsets(200.0, 100.0, 200.0, 100.0), // Padding: top, left, bottom, right
                null,
                null
        );

        mapView.getMapboxMap().setCamera(cameraOptions);
    }
}