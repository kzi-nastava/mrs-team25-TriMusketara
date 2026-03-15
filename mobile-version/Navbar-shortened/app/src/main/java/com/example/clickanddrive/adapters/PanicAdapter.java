package com.example.clickanddrive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.R;
import com.example.clickanddrive.dtosample.responses.PanicResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanicAdapter extends RecyclerView.Adapter<PanicAdapter.PanicViewHolder> {

    public interface PanicActionListener {
        void onResolve(PanicResponse panic);
    }

    private final List<PanicResponse> panicList;
    private final PanicActionListener listener;

    public PanicAdapter(List<PanicResponse> panicList, PanicActionListener listener) {
        this.panicList = panicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PanicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_panic_notification, parent, false);
        return new PanicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PanicViewHolder holder, int position) {
        PanicResponse panic = panicList.get(position);

        holder.tvTriggeredBy.setText("Triggered by: " + panic.getTriggeredByName() + " (" + panic.getTriggeredByEmail() + ")");
        holder.tvRideId.setText("Ride ID: " + panic.getRideId() + (panic.isGuest() ? " (Guest)" : ""));
        holder.tvRoute.setText("Route: " + panic.getOriginAddress() + " → " + panic.getDestinationAddress());

        if (panic.getCreatedAt() != null && !panic.getCreatedAt().isEmpty()) {
            holder.tvTime.setText("Time: " + panic.getCreatedAt().replace("T", " "));
        } else {
            holder.tvTime.setText("Time: -");
        }

        holder.btnResolve.setOnClickListener(v -> listener.onResolve(panic));
    }

    @Override
    public int getItemCount() {
        return panicList == null ? 0 : panicList.size();
    }

    static class PanicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTriggeredBy, tvRideId, tvRoute, tvTime;
        Button btnResolve;

        public PanicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTriggeredBy = itemView.findViewById(R.id.tvTriggeredBy);
            tvRideId = itemView.findViewById(R.id.tvRideId);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnResolve = itemView.findViewById(R.id.btnResolve);
        }
    }
}