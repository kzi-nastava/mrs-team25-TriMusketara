package com.example.clickanddrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.clickanddrive.dtosample.responses.RouteFromFavoritesResponse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FavoriteRouteBottomSheet extends BottomSheetDialogFragment {

    public static FavoriteRouteBottomSheet newInstance(RouteFromFavoritesResponse route) {

        FavoriteRouteBottomSheet sheet = new FavoriteRouteBottomSheet();
        Bundle args = new Bundle();

        args.putLong("routeId", route.getId());
        args.putString("origin", route.getOrigin().getAddress());
        args.putString("destination", route.getDestination().getAddress());
        args.putDouble("distance", route.getDistance());
        args.putInt("duration", route.getDuration());
        args.putInt("timesUsed", route.getTimesUsed());

        // Save and coords for later use
        args.putDouble("originLat", route.getOrigin().getLatitude());
        args.putDouble("originLng", route.getOrigin().getLongitude());
        args.putDouble("destLat", route.getDestination().getLatitude());
        args.putDouble("destLng", route.getDestination().getLongitude());

        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.bottom_sheet_favorite_route,
                container,
                false
        );

        // Find all by ids
        TextView routeTitle = view.findViewById(R.id.route_title);
        TextView distanceTv = view.findViewById(R.id.route_distance);
        TextView durationTv = view.findViewById(R.id.route_duration);
        TextView usageTv = view.findViewById(R.id.route_used);
        Button orderAgainBtn = view.findViewById(R.id.btn_order_again);

        if (getArguments() != null) {
            Long routeId = getArguments().getLong("routeId");
            String from = getArguments().getString("origin");
            String to = getArguments().getString("destination");
            double distance = getArguments().getDouble("distance");
            int duration = getArguments().getInt("duration");
            int usageCount = getArguments().getInt("timesUsed");

            routeTitle.setText(from + " - " + to);
            distanceTv.setText(String.format("%.1f", distance));
            durationTv.setText(String.format("%d min", duration));
            usageTv.setText(usageCount + "x");
        }

        // Order route again...
        orderAgainBtn.setOnClickListener(v -> {
            handleOrderAgain();
        });

        return view;
    }

    private void handleOrderAgain() {
        if (getArguments() == null) {
            return;
        }

        // Get every data from arguments
        String origin = getArguments().getString("origin");
        String destination = getArguments().getString("destination");
        double originLat = getArguments().getDouble("originLat");
        double originLng = getArguments().getDouble("originLng");
        double destLat = getArguments().getDouble("destLat");
        double destLng = getArguments().getDouble("destLng");
        Long routeId = getArguments().getLong("routeId");

        // Bundle creation
        Bundle bundle = new Bundle();
        bundle.putString("PREFILL_ORIGIN", origin);
        bundle.putString("PREFILL_ORIGIN", origin);
        bundle.putString("PREFILL_DESTINATION", destination);
        bundle.putDouble("PREFILL_ORIGIN_LAT", originLat);
        bundle.putDouble("PREFILL_ORIGIN_LNG", originLng);
        bundle.putDouble("PREFILL_DEST_LAT", destLat);
        bundle.putDouble("PREFILL_DEST_LNG", destLng);
        bundle.putLong("FAVORITE_ROUTE_ID", routeId);

        NewRideUserFormFragment formFragment = new NewRideUserFormFragment();
        formFragment.setArguments(bundle);

        // Navigate to fragment
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, formFragment).addToBackStack(null).commit();
        }

        dismiss();
    }

}

