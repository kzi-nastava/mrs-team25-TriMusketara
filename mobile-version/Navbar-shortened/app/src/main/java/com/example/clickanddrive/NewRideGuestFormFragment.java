package com.example.clickanddrive;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.LocationDTO;
import com.example.clickanddrive.dtosample.requests.CreateGuestRideRequest;
import com.example.clickanddrive.dtosample.responses.GuestRideResponseDTO;
import com.example.clickanddrive.models.RouteData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRideGuestFormFragment extends Fragment {

    private TextInputEditText etOrigin;
    private TextInputEditText etDestination;
    private Button btnShowRoute;

    // privremene koordinate dok se ne poveže unos sa realnim map klikom / geolokacijom
    private static final double TEMP_LONGITUDE = 20.44897;
    private static final double TEMP_LATITUDE = 44.7866;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_ride_guest_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etOrigin = view.findViewById(R.id.et_origin);
        etDestination = view.findViewById(R.id.et_destination);
        btnShowRoute = view.findViewById(R.id.btn_estimate_ride);

        btnShowRoute.setOnClickListener(v -> handleShowRoute());
    }

    private void handleShowRoute() {
        String originText = etOrigin.getText() != null ? etOrigin.getText().toString().trim() : "";
        String destinationText = etDestination.getText() != null ? etDestination.getText().toString().trim() : "";

        if (!validateInput(originText, destinationText)) return;

        CreateGuestRideRequest request = buildRequest(originText, destinationText);
        createGuestRide(request, originText, destinationText);
    }

    private boolean validateInput(String origin, String destination) {
        if (TextUtils.isEmpty(origin)) {
            etOrigin.setError("Origin is required");
            return false;
        }

        if (TextUtils.isEmpty(destination)) {
            etDestination.setError("Destination is required");
            return false;
        }

        if (origin.equalsIgnoreCase(destination)) {
            Toast.makeText(getContext(),
                    "Origin and destination cannot be the same",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private CreateGuestRideRequest buildRequest(String originText, String destinationText) {
        LocationDTO origin = new LocationDTO(
                TEMP_LONGITUDE,
                TEMP_LATITUDE,
                originText
        );

        LocationDTO destination = new LocationDTO(
                TEMP_LONGITUDE + 0.01,
                TEMP_LATITUDE + 0.01,
                destinationText
        );

        return new CreateGuestRideRequest(origin, destination);
    }

    private void createGuestRide(CreateGuestRideRequest request, String originText, String destinationText) {
        Call<GuestRideResponseDTO> call = ClientUtils.guestRideService.createGuestRide(request);

        call.enqueue(new Callback<GuestRideResponseDTO>() {
            @Override
            public void onResponse(Call<GuestRideResponseDTO> call,
                                   Response<GuestRideResponseDTO> response) {

                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    GuestRideResponseDTO rideResponse = response.body();

                    Toast.makeText(getContext(),
                            "Ride created!",
                            Toast.LENGTH_LONG).show();

                    navigateToHomeWithRoute(
                            originText,
                            destinationText,
                            rideResponse.getEstimatedTimeMinutes(),
                            rideResponse.getId()
                    );
                } else {
                    Toast.makeText(getContext(),
                            "Failed to create ride",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GuestRideResponseDTO> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToHomeWithRoute(String origin, String destination, int etaMinutes, Long guestRideId) {
        RouteData routeData = new RouteData();
        routeData.setOrigin(origin);
        routeData.setDestination(destination);
        routeData.setStops(new ArrayList<>());
        routeData.setDurationMinutes(etaMinutes);

        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ROUTE_DATA", routeData);
        bundle.putBoolean("IS_GUEST_RIDE", true);
        bundle.putLong("GUEST_RIDE_ID", guestRideId);
        homeFragment.setArguments(bundle);

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, homeFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}