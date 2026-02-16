package com.example.clickanddrive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DriverNotesFragment extends Fragment {

    private TextView tvBlockReason;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_notes, container, false);

        tvBlockReason = view.findViewById(R.id.tv_block_reason);

        // Check if the driver is blocked, and if he is show him the reason in notes
        if (SessionManager.isUserBlocked()) {
            String reason = SessionManager.getBlockReason();
            if (reason != null && !reason.isEmpty()) {
                tvBlockReason.setText(reason);
            } else {
                tvBlockReason.setText("No specific reason provided");
            }
        }

        return view;
    }
}