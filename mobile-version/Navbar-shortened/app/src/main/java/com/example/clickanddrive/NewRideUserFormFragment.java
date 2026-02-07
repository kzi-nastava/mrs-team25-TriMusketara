package com.example.clickanddrive;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.LocationDTO;
import com.example.clickanddrive.dtosample.enumerations.RideStatus;
import com.example.clickanddrive.dtosample.enumerations.VehicleType;
import com.example.clickanddrive.dtosample.requests.CreateRideRequest;
import com.example.clickanddrive.dtosample.responses.RideResponse;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private Calendar selectedTime = Calendar.getInstance();
    private boolean timeSelected = false;

    // Vehicle types
    private static final VehicleType[] VEHICLE_TYPES = VehicleType.values();

    // HARDcODED VALUES FOR NOW
    private static final Long TEMP_PASSENGER_ID = 2L;
    private static final double TEMP_DISTANCE_KM = 10.5;
    private static final int TEMP_DURATION_MINUTES = 25;
    private static final double TEMP_LONGITUDE = 20.44897;
    private static final double TEMP_LATITUDE = 44.7866;

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

        // Pre-fill with existing stops
        if (!additionalStops.isEmpty()) {
            etStopsInput.setText(String.join("\n", additionalStops));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String stopsText = etStopsInput.getText().toString().trim();
            if (!stopsText.isEmpty()) {
                additionalStops.clear();
                String[] stops = stopsText.split("\n");
                for (String stop : stops) {
                    String trimmedStop = stop.trim();
                    if (!trimmedStop.isEmpty()) {
                        additionalStops.add(trimmedStop);
                    }
                }
                updateAdditionalStops();
            } else {
                additionalStops.clear();
                updateAdditionalStops();
            }
            dialog.dismiss();
        });

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

        // Pre-fill with existing passengers
        if (!linkedPassengers.isEmpty()) {
            etPassengersInput.setText(String.join("\n", linkedPassengers));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String passengersText = etPassengersInput.getText().toString().trim();
            if (!passengersText.isEmpty()) {
                linkedPassengers.clear();
                String[] emails = passengersText.split("\n");
                for (String email : emails) {
                    String trimmedEmail = email.trim();
                    if (!trimmedEmail.isEmpty()) {
                        // Email validation...
                        linkedPassengers.add(trimmedEmail);
                    }
                }
                updateLinkedPassengers();
            } else {
                linkedPassengers.clear();
                updateLinkedPassengers();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showDateTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                (view, hourOfDay, minute) -> {
                    // Set the selected time
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute);
                    selectedTime.set(Calendar.SECOND, 0);

                    // Keep today's date
                    Calendar now = Calendar.getInstance();
                    selectedTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
                    selectedTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
                    selectedTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

                    timeSelected = true;
                    updateSetTime();
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                true // 24-hour format
        );

        timePickerDialog.show();
    }

    private void updateAdditionalStops() {
        if (additionalStops.isEmpty()) {
            btnAdditionalStops.setText("Add stops");
        } else {
            btnAdditionalStops.setText("Stops (" + additionalStops.size() + ")");
        }
    }
    private void updateLinkedPassengers() {
        if (linkedPassengers.isEmpty()) {
            btnLinkedPassengers.setText("Add passengers");
        } else {
            btnLinkedPassengers.setText("Passengers (" + linkedPassengers.size() + ")");
        }
    }
    private void updateSetTime() {
        String timeText = String.format("%02d:%02d",
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE)
        );
        btnSetTime.setText(timeText);
    }

    private void handleSchedulingRide() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Build request
        CreateRideRequest request = buildCreateRideRequest();

        // Send to backend
        createRide(request);
    }

    // Validations
    private boolean validateInput() {
        // Origin validation
        String origin = etOrigin.getText().toString().trim();
        if (TextUtils.isEmpty(origin)) {
            etOrigin.setError("Origin is required");
            etOrigin.requestFocus();
            return false;
        }

        // Destination validation
        String destination = etDestination.getText().toString().trim();
        if (TextUtils.isEmpty(destination)) {
            etDestination.setError("Destination is required");
            etDestination.requestFocus();
            return false;
        }

        // Check if origin and destination are the same
        if (origin.equalsIgnoreCase(destination)) {
            Toast.makeText(getContext(), "Origin and destination cannot be the same", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Time validation
        if (!timeSelected) {
            Toast.makeText(getContext(), "Please select pickup time", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if selected time is in the past
        Calendar now = Calendar.getInstance();
        if (selectedTime.before(now)) {
            Toast.makeText(getContext(), "Cannot schedule rides in the past", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if ride is more than 5 hours in advance
        Calendar maxTime = (Calendar) now.clone();
        maxTime.add(Calendar.HOUR_OF_DAY, 5);
        if (selectedTime.after(maxTime)) {
            Toast.makeText(getContext(), "Cannot schedule rides more than 5 hours in advance", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Creating ride ordering request
    private CreateRideRequest buildCreateRideRequest() {
        // Get data from the form
        String origin = etOrigin.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        int selectedVehicleTypeIndex = spinnerVehicleType.getSelectedItemPosition();
        VehicleType vehicleType = VEHICLE_TYPES[selectedVehicleTypeIndex];
        boolean babyFriendly = toggleBabyFriendly.isChecked();
        boolean petFriendly = togglePetFriendly.isChecked();

        // LocationDTOs
        LocationDTO originLocation = new LocationDTO(
                TEMP_LONGITUDE,
                TEMP_LATITUDE,
                origin
        );

        LocationDTO destinationLocation = new LocationDTO(
                TEMP_LONGITUDE + 0.01,
                TEMP_LATITUDE + 0.01,
                destination
        );

        // Create LocationDTOs for additional stops
        List<LocationDTO> stops = new ArrayList<>();
        for (String stopAddress : additionalStops) {
            LocationDTO stop = new LocationDTO(
                    TEMP_LONGITUDE + 0.005,
                    TEMP_LATITUDE + 0.005,
                    stopAddress
            );
            stops.add(stop);
        }

        // Convert Calendar to LocalDateTime
        LocalDateTime scheduledTime = LocalDateTime.of(
                selectedTime.get(Calendar.YEAR),
                selectedTime.get(Calendar.MONTH) + 1, // Calendar months are 0-indexed
                selectedTime.get(Calendar.DAY_OF_MONTH),
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE)
        );

        // Build and return request
        return new CreateRideRequest(
                TEMP_PASSENGER_ID,
                originLocation,
                destinationLocation,
                stops,
                linkedPassengers,
                vehicleType,
                scheduledTime,
                babyFriendly,
                petFriendly,
                TEMP_DURATION_MINUTES,
                TEMP_DISTANCE_KM
        );
    }

    private void createRide(CreateRideRequest request) {
        Call<RideResponse> call = ClientUtils.rideService.createRide(request);

        call.enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RideResponse rideResponse = response.body();

                    handleRideResponse(rideResponse);
                } else {
                    Toast.makeText(getContext(), "Failed to create ride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Navigate to map and draw out the route
    private void handleRideResponse(RideResponse response) {
        if (response.getStatus() == RideStatus.SCHEDULED) { // Available driver found
            String message = String.format("Ride scheduled! Price: %.2f RSD", response.getPrice());
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

            // Clear form
            clearForm();

            // Navigate to main draw out the route
            // ...
        } else if (response.getStatus() == RideStatus.FAILED) {
            // No available driver
            Toast.makeText(getContext(),
                    "No available driver at the moment. Please try again later.",
                    Toast.LENGTH_LONG).show();
        } else {
            // Other status
            Toast.makeText(getContext(),
                    "Ride status: " + response.getStatus(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etOrigin.setText("");
        etDestination.setText("");
        spinnerVehicleType.setSelection(0);
        toggleBabyFriendly.setChecked(false);
        togglePetFriendly.setChecked(false);
        additionalStops.clear();
        linkedPassengers.clear();
        timeSelected = false;

        updateAdditionalStops();
        updateLinkedPassengers();
        btnSetTime.setText("Select Time");
    }
}
