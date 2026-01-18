package com.example.clickanddrive.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.OnRouteClickListener;
import com.example.clickanddrive.R;
import com.example.clickanddrive.dtosample.FavoriteRouteSampleDTO;

import java.util.List;

public class FavoriteRoutesAdapter extends RecyclerView.Adapter<FavoriteRoutesAdapter.RouteViewHolder> {

    // List of users favorite routes
    private List<FavoriteRouteSampleDTO> favRoutes;
    private OnRouteClickListener listener;

    public FavoriteRoutesAdapter(List<FavoriteRouteSampleDTO> routes, OnRouteClickListener listener) {
        this.favRoutes = routes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        FavoriteRouteSampleDTO route = favRoutes.get(position);

        holder.title.setText(route.getRouteTitle());
        holder.distance.setText(route.getDistance());
        holder.duration.setText(route.getDuration());
        holder.used.setText(route.getTimesUsed() + "x");

        if(route.isFavorite()) {
            holder.favoriteBtn.setImageResource(R.drawable.heart_full_red);
        } else {
            holder.favoriteBtn.setImageResource(R.drawable.heart);
        }

        // Favorite button listener
        // For now only change icon style
        holder.favoriteBtn.setOnClickListener(v -> {
            route.setFavorite(!route.isFavorite()); // Change state
            notifyItemChanged(position); // Refresh only this card
        });

        holder.itemView.setOnClickListener(v -> { // itemView = card
            if (listener != null) {
                listener.onRouteClick(route);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favRoutes.size();
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView title, distance, duration, used;
        ImageButton favoriteBtn;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find by ids
            title = itemView.findViewById(R.id.route_title);
            distance = itemView.findViewById(R.id.route_distance);
            duration = itemView.findViewById(R.id.route_duration);
            used = itemView.findViewById(R.id.route_used);
            favoriteBtn = itemView.findViewById(R.id.btn_favorite);
        }
    }
}
