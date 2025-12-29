package com.example.clickanddrive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


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

        // Setting data
        holder.tvDate.setText(ride.getFormattedDate());
        holder.tvRoute.setText(ride.getDepartureAddress() + " -> " + ride.getDestinationAddress());
        holder.tvPrice.setText(ride.getTotalPrice() + " RSD");

        if (ride.isPanicPressed()) {
            holder.tvPanic.setVisibility(View.VISIBLE);
        } else {
            holder.tvPanic.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvRoute, tvPrice, tvPanic;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPanic = itemView.findViewById(R.id.tvPanic);
        }
    }
}