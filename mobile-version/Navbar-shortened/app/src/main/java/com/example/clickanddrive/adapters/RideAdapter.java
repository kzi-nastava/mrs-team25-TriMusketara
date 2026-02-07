package com.example.clickanddrive.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.R;
import com.example.clickanddrive.dtosample.responses.DriverRideHistoryResponse;


import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {


    private List<DriverRideHistoryResponse> rideList;

    public RideAdapter(List<DriverRideHistoryResponse> rideList) {
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
        DriverRideHistoryResponse ride = rideList.get(position);

        // Date setting
        holder.tvDate.setText(ride.getFormattedDate());

        // Route setting
        if (ride.getOrigin() != null && ride.getDestination() != null) {
            String route = ride.getOrigin().getAddress() + " -> " + ride.getDestination().getAddress();
            holder.tvRoute.setText(route);
        } else {
            holder.tvRoute.setText("Not known route");
        }

        // Price
        holder.tvPrice.setText(ride.getTotalPrice() + " RSD");

        // Panic
        holder.tvPanic.setVisibility(ride.isPanicPressed() ? View.VISIBLE : View.GONE);

        // Passengers
        if (ride.getPassengerEmails() != null && !ride.getPassengerEmails().isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.tvPassengers.setText(String.join(", ", ride.getPassengerEmails()));
            } else {
                // Fallback
                StringBuilder sb = new StringBuilder();
                for (String email : ride.getPassengerEmails()) {
                    sb.append(email).append(", ");
                }
                String res = sb.toString();
                holder.tvPassengers.setText(res.isEmpty() ? "" : res.substring(0, res.length() - 2));
            }
        } else {
            holder.tvPassengers.setText("No passenger info.");
        }

        // setting expanding
        boolean isExpanded = ride.isExpanded();
        holder.layoutDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivArrow.setRotation(isExpanded ? 180f : 0f);

        holder.itemView.setOnClickListener(v -> {
            ride.setExpanded(!ride.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return rideList == null ? 0 : rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvRoute, tvPrice, tvPanic, tvPassengers;
        View layoutDetails;
        ImageView ivArrow;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPanic = itemView.findViewById(R.id.tvPanic);
            tvPassengers = itemView.findViewById(R.id.tvPassengers);
            layoutDetails = itemView.findViewById(R.id.layoutDetails);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }
    }
}