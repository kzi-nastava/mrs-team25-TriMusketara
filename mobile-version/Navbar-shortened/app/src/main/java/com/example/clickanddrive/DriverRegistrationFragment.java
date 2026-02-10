package com.example.clickanddrive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.enumerations.Gender;
import com.example.clickanddrive.dtosample.enumerations.VehicleType;
import com.example.clickanddrive.dtosample.requests.DriverRegistrationRequest;
import com.example.clickanddrive.dtosample.requests.VehicleRegistrationRequest;
import com.example.clickanddrive.dtosample.responses.DriverRegistrationResponse;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRegistrationFragment extends Fragment {

    // Cards
    private View cardDriver;
    private View cardVehicle;

    // Buttons
    private Button btnNext, btnBack, btnSubmit;

    // Driver inputs
    private TextInputEditText driverName, driverLName, driverEmail,
            driverPassword, driverAddress, driverPhone;
    private Spinner spinnerGender;

    // Vehicle inputs
    private TextInputEditText vehicleModel, vehiclePlateNum, vehicleSeats;
    private Spinner spinnerVehicleType;
    private ToggleButton toggleBabyFriendly;
    private ToggleButton togglePetFriendly;

    // Vehicle types
    private static final VehicleType[] VEHICLE_TYPES = VehicleType.values();
    private static final Gender[] GENDERS = Gender.values();

    public DriverRegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_registration, container, false);

        initializeViews(view);
        setUpVehicleTypeSpinner();
        setUpGenderSpinner();
        setListeners();

        return view;
    }

    // findByIds initializes variables from above with their corresponding ids in the .xml file
    private void initializeViews(View view) {
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
        spinnerGender = view.findViewById(R.id.spinner_gender);
        driverPhone = view.findViewById(R.id.driver_phone_input);

        // Vehicle fields
        vehicleModel = view.findViewById(R.id.vehicle_model_input);
        vehiclePlateNum = view.findViewById(R.id.vehicle_plate_input);
        vehicleSeats = view.findViewById(R.id.vehicle_seats_input);
        spinnerVehicleType = view.findViewById(R.id.spinner_vehicle_type);

        // Toggles...
        toggleBabyFriendly = view.findViewById(R.id.toggle_baby_friendly);
        togglePetFriendly = view.findViewById(R.id.toggle_pet_friendly);
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

    private void setUpGenderSpinner() {
        String[] genders = new String[GENDERS.length];
        for (int i = 0; i < GENDERS.length; i++) {
            genders[i] = GENDERS[i].name();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item_vehicle_type, //same as above
                genders
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    // Setting listeners to buttons for view visibility, and form submission
    private void setListeners() {
        btnNext.setOnClickListener(v -> {
            if(validateDriverForm()) {
                showVehicleCard();
            }
        });

        btnBack.setOnClickListener(v -> showDriverCard());
        btnSubmit.setOnClickListener(v -> {
            if (validateVehicleForm()) {
                submitRegistration();
            }
        });
    }

    private void submitRegistration() {
        VehicleRegistrationRequest vehicleReq = createVehicle();
        DriverRegistrationRequest driverReq = createDriver(vehicleReq);

        Call<DriverRegistrationResponse> call = ClientUtils.adminService.registerDriver(driverReq, "mobile");

        call.enqueue(new Callback<DriverRegistrationResponse>() {
            @Override
            public void onResponse(Call<DriverRegistrationResponse> call, Response<DriverRegistrationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DriverRegistrationResponse driver = response.body();
                    Toast.makeText(getContext(),
                            "Driver registered successfully! Email sent to " + driver.getEmail(),
                            Toast.LENGTH_LONG).show();
                    clearForms();
                    showDriverCard();
                } else {
                    Toast.makeText(getContext(), "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DriverRegistrationResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Create vehicle
    private VehicleRegistrationRequest createVehicle() {
        // Prepare vehicle
        VehicleRegistrationRequest vehicle = new VehicleRegistrationRequest();
        vehicle.setModel(vehicleModel.getText().toString().trim());
        int selectedVehicleTypeIndex = spinnerVehicleType.getSelectedItemPosition();
        VehicleType vehicleType = VEHICLE_TYPES[selectedVehicleTypeIndex];
        vehicle.setType(vehicleType);
        vehicle.setRegistration(vehiclePlateNum.getText().toString().trim());
        vehicle.setSeats(Integer.parseInt(vehicleSeats.getText().toString().trim()));
        vehicle.setBabyFriendly(toggleBabyFriendly.isChecked());
        vehicle.setPetFriendly(togglePetFriendly.isChecked());
        return vehicle;
    }

    // Create driver
    private DriverRegistrationRequest createDriver(VehicleRegistrationRequest vehicle) {

        // Prepare driver
        DriverRegistrationRequest driver = new DriverRegistrationRequest();
        driver.setName(driverName.getText().toString().trim());
        driver.setSurname(driverLName.getText().toString().trim());
        driver.setEmail(driverEmail.getText().toString().trim());
        driver.setGender(Gender.MALE); //
        driver.setAddress(driverAddress.getText().toString().trim());
        driver.setPhone(driverPhone.getText().toString().trim());
        driver.setVehicle(vehicle);
        return driver;
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

    private boolean validateVehicleForm() {
        if (isEmpty(vehicleModel)) {
            vehicleModel.setError("Vehicle model is required");
            return false;
        }

        if (isEmpty(vehiclePlateNum)) {
            vehiclePlateNum.setError("Registration table is required");
        }

        if (isEmpty(vehicleSeats)) {
            vehicleSeats.setError("Vehicle seats are required");
            return false;
        }

        int seats = Integer.parseInt(vehicleSeats.getText().toString().trim());
        if (seats < 4 || seats > 12) {
            vehicleSeats.setError("Seats are between 4 and 12");
            return false;
        }

        return true;
    }


    // Helper function
    private boolean isEmpty(TextInputEditText editText) {
        return editText.getText() == null ||
                editText.getText().toString().trim().isEmpty();
    }

    private void clearForms() {
        // Clear driver fields
        driverName.setText("");
        driverLName.setText("");
        driverEmail.setText("");
        driverPassword.setText("");
        driverAddress.setText("");
        driverPhone.setText("");

        // Clear vehicle fields
        vehicleModel.setText("");
        vehiclePlateNum.setText("");
        vehicleSeats.setText("");
        toggleBabyFriendly.setChecked(false);
        togglePetFriendly.setChecked(false);
    }
}