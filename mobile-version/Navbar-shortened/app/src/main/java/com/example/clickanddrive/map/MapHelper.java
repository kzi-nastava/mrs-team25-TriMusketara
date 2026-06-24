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
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

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

    private PointAnnotationManager pointAnnotationManager;
    private PointAnnotation vehicleAnnotation;

    public MapHelper(MapView mapView) {
        this.mapView = mapView;
        this.mainHandler = new Handler(Looper.getMainLooper());
        mapView.getMapboxMap().getStyle(style -> initializeAnnotationManager());
    }

    private void initializeAnnotationManager() {
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(
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
            drawRouteLine(routeCoordinates);

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
        if (pointAnnotationManager == null) {
            initializeAnnotationManager();
        }

        pointAnnotationManager.deleteAll();
        vehicleAnnotation = null;

        mapView.getMapboxMap().getStyle(style -> {
            try {
                style.addImage(ICON_ORIGIN, drawableToBitmap(R.drawable.taxi_map_svg));
                style.addImage(ICON_STOP, drawableToBitmap(R.drawable.stop_map_svg));
                style.addImage(ICON_DEST, drawableToBitmap(R.drawable.destination_map_svg));
            } catch (Exception ignored) {
            }

            addMarker(coordinates.get(0).lng, coordinates.get(0).lat, ICON_ORIGIN);

            for (int i = 1; i < coordinates.size() - 1; i++) {
                addMarker(coordinates.get(i).lng, coordinates.get(i).lat, ICON_STOP);
            }

            addMarker(coordinates.get(coordinates.size() - 1).lng, coordinates.get(coordinates.size() - 1).lat, ICON_DEST);
        });
    }

    public void updateVehicleMarker(double lng, double lat) {
        updateVehicleMarker(lng, lat, 999);
    }

    public void updateVehicleMarker(double lng, double lat, int etaMinutes) {
        mainHandler.post(() -> {
            if (pointAnnotationManager == null) {
                initializeAnnotationManager();
            }

            Point point = Point.fromLngLat(lng, lat);

            if (vehicleAnnotation == null) {
                PointAnnotationOptions options = new PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(ICON_ORIGIN)
                        .withIconSize(1.4);

                vehicleAnnotation = pointAnnotationManager.create(options);
            } else {
                vehicleAnnotation.setPoint(point);
                pointAnnotationManager.update(vehicleAnnotation);
            }

            double zoom = etaMinutes <= 5 ? 14.0 : 12.0;

            mapView.getMapboxMap().setCamera(
                    new com.mapbox.maps.CameraOptions.Builder()
                            .center(point)
                            .zoom(zoom)
                            .build()
            );
        });
    }

    private android.graphics.Bitmap drawableToBitmap(int drawableId) {
        android.graphics.drawable.Drawable drawable = androidx.core.content.ContextCompat.getDrawable(mapView.getContext(), drawableId);
        if (drawable == null) return null;

        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                android.graphics.Bitmap.Config.ARGB_8888
        );

        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void addMarker(double lng, double lat, String iconId) {
        PointAnnotationOptions options =
                new PointAnnotationOptions()
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
                new com.mapbox.maps.EdgeInsets(200.0, 100.0, 200.0, 100.0),
                null,
                null
        );

        mapView.getMapboxMap().setCamera(cameraOptions);
    }
}