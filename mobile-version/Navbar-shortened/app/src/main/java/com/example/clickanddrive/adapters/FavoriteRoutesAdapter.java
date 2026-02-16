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
import com.example.clickanddrive.dtosample.responses.RouteFromFavoritesResponse;

import java.util.List;

public class FavoriteRoutesAdapter extends RecyclerView.Adapter<FavoriteRoutesAdapter.RouteViewHolder> {

    // List of users favorite routes
    private List<RouteFromFavoritesResponse> favRoutes;
    private OnRouteClickListener listener;
    private OnFavoriteToggleListener favoriteToggleListener;

    public FavoriteRoutesAdapter(List<RouteFromFavoritesResponse> routes, OnRouteClickListener listener, OnFavoriteToggleListener favoriteToggleListener) {
        this.favRoutes = routes;
        this.listener = listener;
        this.favoriteToggleListener = favoriteToggleListener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        RouteFromFavoritesResponse route = favRoutes.get(position);

        String routeTitle = route.getOrigin().getAddress() + " - " + route.getDestination().getAddress();
        holder.title.setText(routeTitle);

        holder.distance.setText(String.format("%.1f km", route.getDistance()));
        holder.duration.setText(String.format("%d min", route.getDuration()));
        holder.used.setText(route.getTimesUsed() + "x");
        holder.favoriteBtn.setImageResource(R.drawable.heart_full_red);

        // Button listeners
        holder.favoriteBtn.setOnClickListener(v -> {
            if (favoriteToggleListener != null) {
                // Send routeId and position
                favoriteToggleListener.onFavoriteToggle(route.getId(), position);
            }
        });

        // Listener for bottom sheet
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRouteClick(route);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favRoutes.size();
    }

    // For removing a route from favorites
    // Called upon after successful API call to backend
    public void removeItem(int position) {
        favRoutes.remove(position);
        notifyItemRemoved(position);
        // Refresh
        notifyItemRangeChanged(position, favRoutes.size());
    }

    public void updateRoutes(List<RouteFromFavoritesResponse> newRoutes) {
        this.favRoutes = newRoutes;
        notifyDataSetChanged();
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

    public interface OnFavoriteToggleListener {
        void onFavoriteToggle(Long routeId, int position);
    }

}
