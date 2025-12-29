package com.example.clickanddrive;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ChangeInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_info, container, false);

        LinearLayout driverLayout = view.findViewById(R.id.driver_part);
        // Which user is currently logged in
        int userType = SessionManager.currentUserType;

        // When showing account information, should you show the vehicle information too
        // Only if the user is a driver, if not hide it
        if (userType == SessionManager.DRIVER) {
            driverLayout.setVisibility(View.VISIBLE);
        }
        else {
            driverLayout.setVisibility(View.GONE);
        }

        return view;
    }
}