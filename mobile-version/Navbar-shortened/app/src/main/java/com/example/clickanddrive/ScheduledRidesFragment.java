package com.example.clickanddrive;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.adapters.ScheduledRideAdapter;
import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.clients.services.RideService;
import com.example.clickanddrive.dtosample.requests.RideCancellationRequest;
import com.example.clickanddrive.dtosample.responses.ScheduledRideResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduledRidesFragment extends Fragment implements ScheduledRideAdapter.ScheduledRideActionListener {

    private final List<ScheduledRideResponse> rides = new ArrayList<>();
    private ScheduledRideAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scheduled_rides, container, false);

        RecyclerView rvScheduledRides = view.findViewById(R.id.rvScheduledRides);
        rvScheduledRides.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ScheduledRideAdapter(rides, this);
        rvScheduledRides.setAdapter(adapter);

        fetchScheduledRides();

        return view;
    }

    private void fetchScheduledRides() {
        if (SessionManager.userId == null) {
            Toast.makeText(getContext(), "Driver is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<RideService.PageResponse<ScheduledRideResponse>> call =
                ClientUtils.rideService.getScheduledRides(SessionManager.userId);

        call.enqueue(new Callback<RideService.PageResponse<ScheduledRideResponse>>() {
            @Override
            public void onResponse(Call<RideService.PageResponse<ScheduledRideResponse>> call,
                                   Response<RideService.PageResponse<ScheduledRideResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rides.clear();
                    if (response.body().getContent() != null) {
                        rides.addAll(response.body().getContent());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load scheduled rides", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RideService.PageResponse<ScheduledRideResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBeginRide(ScheduledRideResponse ride) {
        Map<String, Boolean> body = new HashMap<>();
        body.put("isGuest", ride.isGuest());

        ClientUtils.rideService.startRide(ride.getId(), body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    DriverDriveInProgressFragment fragment = DriverDriveInProgressFragment.newInstance(ride);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Cannot start this ride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancelRide(ScheduledRideResponse ride, String reason) {
        if (TextUtils.isEmpty(reason)) {
            Toast.makeText(getContext(), "Reason is required", Toast.LENGTH_SHORT).show();
            return;
        }

        RideCancellationRequest request =
                new RideCancellationRequest(SessionManager.userId, reason, ride.isGuest());

        ClientUtils.rideService.cancelRide(ride.getId(), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    rides.remove(ride);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Ride cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to cancel ride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}