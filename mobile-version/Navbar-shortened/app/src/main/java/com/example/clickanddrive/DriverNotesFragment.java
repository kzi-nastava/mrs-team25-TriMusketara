package com.example.clickanddrive;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverNotesFragment extends Fragment {

    private TextView tvBlockReason;
    private CardView blockCard;
    private TextView tvNoteStatus;
    private static final String TAG = "DRIVER_DEBUG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_notes, container, false);
        tvBlockReason = view.findViewById(R.id.tv_block_reason);
        blockCard = view.findViewById(R.id.blockCard);
        tvNoteStatus = view.findViewById(R.id.tv_note_status);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDriverStatus();
    }

    private void refreshDriverStatus() {
        if (SessionManager.userId == null) {
            return;
        }

        // Using userId from SessionManager to get fresh data
        ClientUtils.userService.getUserProfile(SessionManager.userId).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();

                    // Update sessionManager with new data
                    SessionManager.setIsBlocked(profile.isBlocked());
                    SessionManager.setBlockReason(profile.getBlockReason());

                    // Update UI
                    updateUI();
                } else {
                    Log.e(TAG, "API error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (SessionManager.isUserBlocked()) {
            blockCard.setVisibility(View.VISIBLE);

            String reason = SessionManager.getBlockReason();

            tvBlockReason.setText(reason != null && !reason.isEmpty() ? reason : "User is blocked");
            tvBlockReason.setVisibility(View.VISIBLE);

            tvNoteStatus.setText("Please contact support if you believe this is a mistake");
        } else {
            blockCard.setVisibility(View.GONE);

            tvBlockReason.setText("");
            tvBlockReason.setVisibility(View.GONE);

            tvNoteStatus.setText("No active notes");
        }
    }
}
