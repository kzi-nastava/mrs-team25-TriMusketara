package com.example.clickanddrive;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewRideFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Fragment formFragment;

        if (SessionManager.currentUserType == SessionManager.GUEST) {
            // Fragment that displays new ride form for GUEST
            formFragment = null;
        } else {
            formFragment = new NewRideUserFormFragment();
        }

        getChildFragmentManager().beginTransaction().replace(R.id.new_ride_container, formFragment).commit();
    }

}