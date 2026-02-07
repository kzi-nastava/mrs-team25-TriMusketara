package com.example.clickanddrive;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.dtosample.enumerations.VehicleType;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// Form for when a registered user wants to order a ride
public class NewRideUserFormFragment extends Fragment {

    // Form fields
    private EditText etOrigin;
    private EditText etDestination;
    private Spinner spinnerVehicleType;
    private ToggleButton toggleBabyFriendly;
    private ToggleButton togglePetFriendly;

    // Buttons
    private Button btnAdditionalStops;
    private Button btnLinkedPassengers;
    private Button btnSetTime;
    private Button btnScheduleRide;

    // Data storages
    private List<String> additionalStops = new ArrayList<>(); // here we load additional stops when creating ride order
    private List<String> linkedPassengers = new ArrayList<>(); // ...
    private Calendar selectedDateTime = Calendar.getInstance();

    // Vehicle types
    private static final VehicleType[] VEHICLE_TYPES = VehicleType.values();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_ride_user_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceBundle) {
        super.onViewCreated(view, savedInstanceBundle);

        initializeViews(view);
        setUpVehicleTypeSpinner();
        setUpButtonListeners();
    }

    // Initialize views (find by id)
    private void initializeViews(View view) {
        // Form fields
        etOrigin = view.findViewById(R.id.et_origin);
        etDestination = view.findViewById(R.id.et_destination);
        spinnerVehicleType = view.findViewById(R.id.spinner_vehicle_type);
        toggleBabyFriendly = view.findViewById(R.id.toggle_baby_friendly);
        togglePetFriendly = view.findViewById(R.id.toggle_pet_friendly);

        // Buttons
        btnAdditionalStops = view.findViewById(R.id.btn_additional_stops);
        btnLinkedPassengers = view.findViewById(R.id.btn_linked_passengers);
        btnSetTime = view.findViewById(R.id.btn_set_time);
        btnScheduleRide = view.findViewById(R.id.btn_schedule_ride);
    }

    private void setUpVehicleTypeSpinner() {
        String[] vehicleTypeNames = new String[VEHICLE_TYPES.length];
        for (int i = 0; i < VEHICLE_TYPES.length; i++) {
            vehicleTypeNames[i] = VEHICLE_TYPES[i].name();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item_vehicle_type,
                vehicleTypeNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicleType.setAdapter(adapter);
    }

    private void setUpButtonListeners() {
        btnAdditionalStops.setOnClickListener(v -> showAdditionalStopsDialog());
        btnLinkedPassengers.setOnClickListener(v -> showLinkedPassengersDialog());
        btnSetTime.setOnClickListener(v -> showDateTimePicker());
    }

    private void showAdditionalStopsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_additional_stops, null);
        builder.setView(dialogView);

        // Create alert dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Find elements by id
        TextInputEditText etStopsInput = dialogView.findViewById(R.id.et_stops_input);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel_stops);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm_stops);

//        // Pre-fill with existing stops
//        if (!additionalStops.isEmpty()) {
//            etStopsInput.setText(String.join("\n", additionalStops));
//        }

        dialog.show();

    }

    private void showLinkedPassengersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_linked_passengers, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextInputEditText etPassengersInput = dialogView.findViewById(R.id.et_passengers_input);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel_passengers);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm_passengers);

//        // Pre-fill with existing passengers
//        if (!linkedPassengers.isEmpty()) {
//            etPassengersInput.setText(String.join("\n", linkedPassengers));
//        }

        dialog.show();
    }

    private void showDateTimePicker() {
        // First show date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Then show time picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            requireContext(),
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                //updateSetTimeButton();
                            },
                            selectedDateTime.get(Calendar.HOUR_OF_DAY),
                            selectedDateTime.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );

        // Don't allow selecting past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }




}
