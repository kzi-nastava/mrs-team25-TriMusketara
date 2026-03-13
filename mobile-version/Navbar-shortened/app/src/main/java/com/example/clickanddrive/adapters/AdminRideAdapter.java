package com.example.clickanddrive.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;
import com.example.clickanddrive.AdminRideDetailsFragment;
import com.example.clickanddrive.R;
import com.example.clickanddrive.dtosample.responses.AdminRideHistoryResponse;

import java.util.List;

public class AdminRideAdapter extends RecyclerView.Adapter<AdminRideAdapter.RideViewHolder> {

    private final List<AdminRideHistoryResponse> rideList;

    public AdminRideAdapter(List<AdminRideHistoryResponse> rideList) {
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        AdminRideHistoryResponse ride = rideList.get(position);

        holder.tvDate.setText(ride.getFormattedDateRange());

        String origin = ride.getOrigin() != null ? safe(ride.getOrigin().getAddress()) : "-";
        String destination = ride.getDestination() != null ? safe(ride.getDestination().getAddress()) : "-";
        holder.tvRoute.setText(origin + " -> " + destination);

        holder.tvPrice.setText(ride.getTotalPrice() + " RSD");
        holder.tvPanic.setVisibility(ride.isPanicPressed() ? View.VISIBLE : View.GONE);

        String driver = "Driver: " + safe(ride.getDriverEmail());
        String passengers = "Passengers: " + joinEmails(ride.getPassengerEmails());
        String status = "Status: " + safe(ride.getStatus());
        String cancellation = "Cancelled: " + (ride.isCancelled() ? "Yes" : "No");
        String cancelledBy = "Cancelled by: " + safe(ride.getCancelledBy());

        holder.tvDriver.setText(driver);
        holder.tvPassengers.setText(passengers);
        holder.tvStatus.setText(status);
        holder.tvCancelled.setText(cancellation);
        holder.tvCancelledBy.setText(cancelledBy);

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
                    .replace(R.id.flFragment, AdminRideDetailsFragment.newInstance(ride.getId()))
                    .addToBackStack(null)
                    .commit();
        });
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String joinEmails(List<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return "-";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return String.join(", ", emails);
        }

        StringBuilder sb = new StringBuilder();
        for (String email : emails) {
            sb.append(email).append(", ");
        }

        String result = sb.toString();
        return result.endsWith(", ") ? result.substring(0, result.length() - 2) : result;
    }

    @Override
    public int getItemCount() {
        return rideList == null ? 0 : rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvRoute, tvPrice, tvPanic;
        TextView tvDriver, tvPassengers, tvStatus, tvCancelled, tvCancelledBy;
        View layoutDetails;
        ImageView ivArrow;
        Button btnDetails;
        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPanic = itemView.findViewById(R.id.tvPanic);
            tvDriver = itemView.findViewById(R.id.tvDriver);
            tvPassengers = itemView.findViewById(R.id.tvPassengers);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCancelled = itemView.findViewById(R.id.tvCancelled);
            tvCancelledBy = itemView.findViewById(R.id.tvCancelledBy);
            layoutDetails = itemView.findViewById(R.id.layoutDetails);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}