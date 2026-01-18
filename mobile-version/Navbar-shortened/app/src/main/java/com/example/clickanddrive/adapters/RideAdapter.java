package com.example.clickanddrive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.clickanddrive.R;
import com.example.clickanddrive.dtosample.DriverHistorySampleDTO;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<DriverHistorySampleDTO> rideList;

    public RideAdapter(List<DriverHistorySampleDTO> rideList) {
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Here is used ride item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        DriverHistorySampleDTO ride = rideList.get(position);

        // Base data
        holder.tvDate.setText(ride.getFormattedDate());
        holder.tvRoute.setText(ride.getDepartureAddress() + " -> " + ride.getDestinationAddress());
        holder.tvPrice.setText(ride.getTotalPrice() + " RSD");

        // Panic
        holder.tvPanic.setVisibility(ride.isPanicPressed() ? View.VISIBLE : View.GONE);

        // Passangers
        if (ride.getPassengerEmails() != null && !ride.getPassengerEmails().isEmpty()) {
            // String.join radi na API 26+, ako dobiješ grešku, koristi for-petlju
            String emails = String.join(", ", ride.getPassengerEmails());
            holder.tvPassengers.setText(emails);
        } else {
            holder.tvPassengers.setText("No passengers info.");
        }

        // Managing expansion
        boolean isExpanded = ride.isExpanded();
        holder.layoutDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivArrow.setRotation(isExpanded ? 180f : 0f);

        // Click changes everything
        holder.itemView.setOnClickListener(v -> {
            ride.setExpanded(!ride.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
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