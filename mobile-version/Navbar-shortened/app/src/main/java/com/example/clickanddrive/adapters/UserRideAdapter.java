package com.example.clickanddrive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.PassengerRideDetailsFragment;
import com.example.clickanddrive.R;
import com.example.clickanddrive.dtosample.responses.PassengerRideHistoryResponse;

import java.util.List;

public class UserRideAdapter extends RecyclerView.Adapter<UserRideAdapter.RideViewHolder> {

    private final List<PassengerRideHistoryResponse> rideList;

    public UserRideAdapter(List<PassengerRideHistoryResponse> rideList) {
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        PassengerRideHistoryResponse ride = rideList.get(position);

        holder.tvDate.setText(ride.getFormattedDateRange());

        if (ride.getOrigin() != null && ride.getDestination() != null) {
            holder.tvRoute.setText(ride.getOrigin().getAddress() + " -> " + ride.getDestination().getAddress());
        } else {
            holder.tvRoute.setText("Unknown route");
        }

        holder.tvPrice.setText(ride.getTotalPrice() + " RSD");
        holder.tvPanic.setVisibility(View.GONE);

        String detailsText =
                "Driver: " + safe(ride.getDriverEmail()) + "\n" +
                        "Status: " + safe(ride.getStatus());

        holder.tvPassengers.setText(detailsText);

        boolean isExpanded = ride.isExpanded();
        holder.layoutDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivArrow.setRotation(isExpanded ? 180f : 0f);

        holder.itemView.setOnClickListener(v -> {
            ride.setExpanded(!ride.isExpanded());
            notifyItemChanged(position);
        });

        holder.btnDetails.setOnClickListener(v -> {
            FragmentActivity activity = (FragmentActivity) v.getContext();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, PassengerRideDetailsFragment.newInstance(ride.getId()))
                    .addToBackStack(null)
                    .commit();
        });
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }

    @Override
    public int getItemCount() {
        return rideList == null ? 0 : rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvRoute, tvPrice, tvPanic, tvPassengers;
        View layoutDetails;
        ImageView ivArrow;
        Button btnDetails;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPanic = itemView.findViewById(R.id.tvPanic);
            tvPassengers = itemView.findViewById(R.id.tvPassengers);
            layoutDetails = itemView.findViewById(R.id.layoutDetails);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}