package com.example.clickanddrive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clickanddrive.adapters.FavoriteRoutesAdapter;
import com.example.clickanddrive.dtosample.FavoriteRouteSampleDTO;

import java.util.ArrayList;
import java.util.List;

public class FavoriteRoutesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteRoutesAdapter adapter;

    public FavoriteRoutesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favorite_routes, container, false);

        recyclerView = view.findViewById(R.id.favorite_routes);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<FavoriteRouteSampleDTO> routes = new ArrayList<>();
        routes.add(new FavoriteRouteSampleDTO("Home", "Faculty", "30km", "27min", 15, true));
        routes.add(new FavoriteRouteSampleDTO("Home", "Gym", "10km", "17min", 19, true));
        routes.add(new FavoriteRouteSampleDTO("Faculty", "Train station", "20km", "23min", 17, true));

        adapter = new FavoriteRoutesAdapter(routes, route -> {
            onRouteSelected(route);
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void onRouteSelected(FavoriteRouteSampleDTO route) {
        // Open bottom sheet for the selected card
        FavoriteRouteBottomSheet sheet = FavoriteRouteBottomSheet.newInstance(
                route.getFrom(),
                route.getTo(),
                route.getDistance(),
                route.getDuration(),
                route.getTimesUsed()
        );
        sheet.show(getParentFragmentManager(), "route_bottom_sheet");
    }
}