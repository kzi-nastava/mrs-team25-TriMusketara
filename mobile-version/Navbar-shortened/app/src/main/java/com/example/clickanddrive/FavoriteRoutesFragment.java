package com.example.clickanddrive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clickanddrive.adapters.FavoriteRoutesAdapter;
import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.responses.RouteFromFavoritesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteRoutesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteRoutesAdapter adapter;
    private EditText searchBar;

    // List of all routes
    private List<RouteFromFavoritesResponse> allRoutes = new ArrayList<>();
    // List of currently displayed
    private List<RouteFromFavoritesResponse> displayedRoutes = new ArrayList<>();

    public FavoriteRoutesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favorite_routes, container, false);

        recyclerView = view.findViewById(R.id.favorite_routes);
        searchBar = view.findViewById(R.id.search_bar);

        // Setup recyclerView
        setUpRecyclerView();

        setUpSearch();

        loadFavoriteRoutes();

        return view;
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new FavoriteRoutesAdapter(
                displayedRoutes,
                // Listener for route click
                this::onRouteSelected,
                // Listener for favorite toggle
                this::onFavoriteToggle
        );

        recyclerView.setAdapter(adapter);
    }

    private void loadFavoriteRoutes() {
        Long userId = SessionManager.userId;
        System.out.println("CURRENT USER TYPE: " + SessionManager.currentUserType);
        Call<List<RouteFromFavoritesResponse>> call = ClientUtils.passengerService.getFavoriteRoutes(userId);

        call.enqueue(new Callback<List<RouteFromFavoritesResponse>>() {
            @Override
            public void onResponse(Call<List<RouteFromFavoritesResponse>> call, Response<List<RouteFromFavoritesResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Loaded routes
                    List<RouteFromFavoritesResponse> routes = response.body();

                    allRoutes.clear();
                    allRoutes.addAll(routes);

                    displayedRoutes.clear();
                    displayedRoutes.addAll(routes);

                    adapter.notifyDataSetChanged();

                    // If the user has no favorite routes
                    if (routes.isEmpty()) {
                        Toast.makeText(requireContext(), "You have no routes added to favorites.\n Feel free to add some", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Error loading favorite routes" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RouteFromFavoritesResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onFavoriteToggle(Long routeId, int position) {
        Long userId = SessionManager.userId;
        Call<Void> call = ClientUtils.passengerService.removeFromFavorites(userId, routeId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Removed

                    RouteFromFavoritesResponse removedRoute = displayedRoutes.get(position);
                    // Remove from list
                    allRoutes.remove(removedRoute);
                    adapter.removeItem(position);

                    Toast.makeText(requireContext(), "Route removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Error removing route", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setUpSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRoutes(s.toString());
            }
        });
    }

    private void filterRoutes(String query) {
        displayedRoutes.clear();

        if (query.isEmpty()) {
            displayedRoutes.addAll(allRoutes);
        } else {
            String lowerQuery = query.toLowerCase();

            for (RouteFromFavoritesResponse route : allRoutes) {
                String origin = route.getOrigin().getAddress().toLowerCase();
                String destination = route.getDestination().getAddress().toLowerCase();

                if (origin.contains(lowerQuery) || destination.contains(lowerQuery)) {
                    displayedRoutes.add(route);
                }
            }
        }
    }

    private void onRouteSelected(RouteFromFavoritesResponse route) {
        // Open bottom sheet for the selected card
        FavoriteRouteBottomSheet sheet = FavoriteRouteBottomSheet.newInstance(route);
        sheet.show(getParentFragmentManager(), "route_bottom_sheet");
    }
}