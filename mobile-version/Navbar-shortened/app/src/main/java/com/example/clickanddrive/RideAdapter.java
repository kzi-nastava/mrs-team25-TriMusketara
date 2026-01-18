package com.example.clickanddrive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Dodato
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.dtosample.DriverHistorySampleDTO;
import com.example.clickanddrive.R;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<DriverHistorySampleDTO> rideList;

    public RideAdapter(List<DriverHistorySampleDTO> rideList) {
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
        DriverHistorySampleDTO ride = rideList.get(position);

        // Osnovni podaci
        holder.tvDate.setText(ride.getFormattedDate());
        holder.tvRoute.setText(ride.getDepartureAddress() + " -> " + ride.getDestinationAddress());
        holder.tvPrice.setText(ride.getTotalPrice() + " RSD");

        // Panic dugme/text
        holder.tvPanic.setVisibility(ride.isPanicPressed() ? View.VISIBLE : View.GONE);

        // Prikaz putnika
        if (ride.getPassengerEmails() != null && !ride.getPassengerEmails().isEmpty()) {
            // String.join radi na API 26+, ako dobiješ grešku, koristi for-petlju
            String emails = String.join(", ", ride.getPassengerEmails());
            holder.tvPassengers.setText(emails);
        } else {
            holder.tvPassengers.setText("No passengers info.");
        }

        // Upravljanje proširenjem (vidljivost layout-a)
        boolean isExpanded = ride.isExpanded();
        holder.layoutDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // Rotacija strelice: 0 stepeni (dole), 180 stepeni (gore)
        holder.ivArrow.setRotation(isExpanded ? 180f : 0f);

        // Klik na celu stavku menja stanje
        holder.itemView.setOnClickListener(v -> {
            ride.setExpanded(!ride.isExpanded());
            notifyItemChanged(position); // Osvežava samo tu stavku sa animacijom
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvRoute, tvPrice, tvPanic, tvPassengers;
        View layoutDetails; // Može i LinearLayout layoutDetails;
        ImageView ivArrow;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPanic = itemView.findViewById(R.id.tvPanic);
            tvPassengers = itemView.findViewById(R.id.tvPassengers);

            // Povezivanje detalja koji se crvene
            layoutDetails = itemView.findViewById(R.id.layoutDetails);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }
    }
}