package com.example.clickanddrive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public class DriverRegistrationFragment extends Fragment {

    // Cards
    private View cardDriver;
    private View cardVehicle;

    // Buttons
    private Button btnNext, btnBack, btnSubmit;

    // Driver inputs
    private TextInputEditText driverName, driverLName, driverEmail,
            driverPassword, driverAddress, driverPhone;

    // Vehicle inputs
    private TextInputEditText vehicleModel, vehiclePlateNum, vehicleSeats;

    public DriverRegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_registration, container, false);

        // Functions
        findByIds(view);
        setListeners();

        return view;
    }

    // findByIds initializes variables from above with their corresponding ids in the .xml file
    private void findByIds(View view) {
        // Cards
        cardDriver = view.findViewById(R.id.card_driver);
        cardVehicle = view.findViewById(R.id.card_vehicle);

        // Buttons
        btnNext = view.findViewById(R.id.btn_next);
        btnBack = view.findViewById(R.id.btn_back);
        btnSubmit = view.findViewById(R.id.btn_submit);

        // Driver fields
        driverName = view.findViewById(R.id.driver_name_input);
        driverLName = view.findViewById(R.id.driver_lname_input);
        driverEmail = view.findViewById(R.id.driver_email_input);
        driverPassword = view.findViewById(R.id.driver_password_input);
        driverAddress = view.findViewById(R.id.driver_address_input);
        driverPhone = view.findViewById(R.id.driver_phone_input);

        // Vehicle fields
        vehicleModel = view.findViewById(R.id.vehicle_model_input);
        vehiclePlateNum = view.findViewById(R.id.vehicle_plate_input);

        // Fill the dropdown when picking vehicle type
        MaterialAutoCompleteTextView vehicleTypeDropdown =
                view.findViewById(R.id.vehicle_type_input);

        String[] vehicleTypes = {"Luxury", "Standard", "Van"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                vehicleTypes
        );

        vehicleTypeDropdown.setAdapter(adapter);

        vehicleSeats = view.findViewById(R.id.vehicle_seats_input);
    }

    // Setting listeners to buttons for view visibility, and form submission
    private void setListeners() {
        btnNext.setOnClickListener(v -> {
            if(validateDriverForm()) {
                showVehicleCard();
            }
        });

        btnBack.setOnClickListener(v -> showDriverCard());
        btnSubmit.setOnClickListener(v -> {});
    }

    // Control card visibility
    private void showDriverCard() {
        cardDriver.setVisibility(View.VISIBLE);
        cardVehicle.setVisibility(View.GONE);
    }
    private void showVehicleCard() {
        cardDriver.setVisibility(View.GONE);
        cardVehicle.setVisibility(View.VISIBLE);
    }

    // Minimal validation, will be expanded later
    private boolean validateDriverForm() {
        if (isEmpty(driverName)) {
            driverName.setError("Required");
            return false;
        }

        if (isEmpty(driverLName)) {
            driverLName.setError("Required");
            return false;
        }

        if (isEmpty(driverEmail)) {
            driverEmail.setError("Required");
            return false;
        }

        if (isEmpty(driverPassword)) {
            driverPassword.setError("Required");
            return false;
        }

        if (isEmpty(driverAddress)) {
            driverAddress.setError("Required");
            return false;
        }

        if (isEmpty(driverPhone)) {
            driverPhone.setError("Required");
            return false;
        }

        return true;
    }

    // Helper function
    private boolean isEmpty(TextInputEditText editText) {
        return editText.getText() == null ||
                editText.getText().toString().trim().isEmpty();
    }

}