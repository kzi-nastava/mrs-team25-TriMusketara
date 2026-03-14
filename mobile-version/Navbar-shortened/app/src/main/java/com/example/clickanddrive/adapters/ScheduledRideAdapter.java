package com.example.clickanddrive.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.R;
import com.example.clickanddrive.dtosample.responses.ScheduledRideResponse;

import java.util.List;

public class ScheduledRideAdapter extends RecyclerView.Adapter<ScheduledRideAdapter.ViewHolder> {

    public interface ScheduledRideActionListener {
        void onBeginRide(ScheduledRideResponse ride);
        void onCancelRide(ScheduledRideResponse ride, String reason);
    }

    private final List<ScheduledRideResponse> rides;
    private final ScheduledRideActionListener listener;

    public ScheduledRideAdapter(List<ScheduledRideResponse> rides,
                                ScheduledRideActionListener listener) {
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scheduled_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduledRideResponse ride = rides.get(position);

        holder.tvRoute.setText(ride.getOrigin() + " -> " + ride.getDestination());
        holder.tvTime.setText("Scheduled: " + ride.getFormattedScheduledTime());

        holder.btnBegin.setOnClickListener(v -> listener.onBeginRide(ride));

        holder.btnCancel.setOnClickListener(v -> {
            EditText input = new EditText(v.getContext());
            input.setHint("Reason for cancellation");

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Cancel ride")
                    .setView(input)
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        String reason = input.getText().toString().trim();
                        listener.onCancelRide(ride, reason);
                    })
                    .setNegativeButton("Close", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return rides == null ? 0 : rides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvTime;
        Button btnBegin, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnBegin = itemView.findViewById(R.id.btnBegin);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}