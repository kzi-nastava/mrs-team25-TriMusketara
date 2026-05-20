package com.example.clickanddrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.requests.ReviewRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateRideFragment extends Fragment {

    private static final String ARG_RIDE_ID = "ride_id";

    private Long rideId;

    private RatingBar rbDriverRating;
    private RatingBar rbVehicleRating;
    private EditText etComment;
    private Button btnSubmitRating;
    private Button btnCancelRating;

    public static RateRideFragment newInstance(Long rideId) {
        RateRideFragment fragment = new RateRideFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rate_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }

        rbDriverRating = view.findViewById(R.id.rbDriverRating);
        rbVehicleRating = view.findViewById(R.id.rbVehicleRating);
        etComment = view.findViewById(R.id.etComment);
        btnSubmitRating = view.findViewById(R.id.btnSubmitRating);
        btnCancelRating = view.findViewById(R.id.btnCancelRating);

        btnSubmitRating.setOnClickListener(v -> submitRating());
        btnCancelRating.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void submitRating() {
        if (rideId == null) {
            Toast.makeText(getContext(), "Ride id is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        if (SessionManager.userId == null) {
            Toast.makeText(getContext(), "Passenger is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        int driverRating = Math.round(rbDriverRating.getRating());
        int vehicleRating = Math.round(rbVehicleRating.getRating());

        if (driverRating <= 0) {
            Toast.makeText(getContext(), "Please rate the driver", Toast.LENGTH_SHORT).show();
            return;
        }

        if (vehicleRating <= 0) {
            Toast.makeText(getContext(), "Please rate the vehicle", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = etComment.getText() == null
                ? ""
                : etComment.getText().toString().trim();

        ReviewRequest request = new ReviewRequest(
                rideId,
                SessionManager.userId,
                driverRating,
                vehicleRating,
                comment
        );

        btnSubmitRating.setEnabled(false);
        btnCancelRating.setEnabled(false);

        ClientUtils.rideService.rateRide(rideId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                btnSubmitRating.setEnabled(true);
                btnCancelRating.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ride rated successfully", Toast.LENGTH_SHORT).show();

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, PassengerRideDetailsFragment.newInstance(rideId))
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Failed to submit rating", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;

                btnSubmitRating.setEnabled(true);
                btnCancelRating.setEnabled(true);

                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}