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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FavoriteRouteBottomSheet extends BottomSheetDialogFragment {

    public static FavoriteRouteBottomSheet newInstance(String from, String to, String distance, String duration,
                                                       int timesUsed) {

        FavoriteRouteBottomSheet sheet = new FavoriteRouteBottomSheet();
        Bundle args = new Bundle();

        args.putString("from", from);
        args.putString("to", to);
        args.putString("distance", distance);
        args.putString("duration", duration);
        args.putInt("timesUsed", timesUsed);

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
            String from = getArguments().getString("from");
            String to = getArguments().getString("to");
            String distance = getArguments().getString("distance");
            String duration = getArguments().getString("duration");
            int usageCount = getArguments().getInt("timesUsed");

            routeTitle.setText(from + " - " + to);
            distanceTv.setText(distance);
            durationTv.setText(duration);
            usageTv.setText(usageCount + "x");
        }

        // Order route again...
        orderAgainBtn.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }
}

