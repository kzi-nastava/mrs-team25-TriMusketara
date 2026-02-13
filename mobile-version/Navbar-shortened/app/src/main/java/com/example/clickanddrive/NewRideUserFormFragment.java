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
import com.example.clickanddrive.map.MapboxDirections;
import com.example.clickanddrive.map.MapboxGeocoder;
import com.example.clickanddrive.models.RouteData;
import com.example.clickanddrive.models.RouteCalculator;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Form for when a registered user wants to order a ride
public class NewRideUserFormFragment extends Fragment {

    // Form fields
    private TextInputEditText etOrigin;
    private TextInputEditText etDestination;
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

    // Service for route calculation
    private RouteCalculator routeCalculator;

    // Data from favored route if a user orders from favorites
    private boolean isFromFavorite = false;
    private Double prefilledOriginLat = null;
    private Double prefilledOriginLng = null;
    private Double prefilledDestLat = null;
    private Double prefilledDestLng = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_ride_user_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceBundle) {
        super.onViewCreated(view, savedInstanceBundle);

        routeCalculator = new RouteCalculator();

        initializeViews(view);
        setUpVehicleTypeSpinner();
        setUpButtonListeners();

        checkForPrefillData();
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

    private void checkForPrefillData() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        if (args.containsKey("PREFILL_ORIGIN")) {
            String origin = args.getString("PREFILL_ORIGIN");
            String destination = args.getString("PREFILL_DESTINATION");

            prefilledOriginLat = args.getDouble("PREFILL_ORIGIN_LAT", 0.0);
            prefilledOriginLng = args.getDouble("PREFILL_ORIGIN_LNG", 0.0);
            prefilledDestLat = args.getDouble("PREFILL_DEST_LAT", 0.0);
            prefilledDestLng = args.getDouble("PREFILL_DEST_LNG", 0.0);

            isFromFavorite = true;

            etOrigin.setText(origin);
            etDestination.setText(destination);
        }
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
        btnScheduleRide.setOnClickListener(v -> handleSchedulingRide());
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
        btnScheduleRide.setEnabled(false);
        btnScheduleRide.setText("Processing...");

        String origin = etOrigin.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();

        // Route calculation service
        routeCalculator.calculateRoute(origin,
                destination,
                additionalStops,
                prefilledOriginLng,
                prefilledOriginLat,
                prefilledDestLng,
                prefilledDestLat,
                new RouteCalculator.RouteCalculationCallback() {
                    @Override
                    public void onSuccess(RouteCalculator.RouteCalculationResult result) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                createAndSendRide(result);
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                                resetScheduleButton();
                            });
                        }
                    }
                });
    }

    private void createAndSendRide(RouteCalculator.RouteCalculationResult routeResult) {
        // Take data from form
        int selectedVehicleTypeIndex = spinnerVehicleType.getSelectedItemPosition();
        VehicleType vehicleType = VEHICLE_TYPES[selectedVehicleTypeIndex];
        boolean babyFriendly = toggleBabyFriendly.isChecked();
        boolean petFriendly = togglePetFriendly.isChecked();

        LocationDTO originLocation = new LocationDTO(
                routeResult.originLng,
                routeResult.originLat,
                routeResult.origin
        );

        LocationDTO destinationLocation = new LocationDTO(
                routeResult.destLng,
                routeResult.destLat,
                routeResult.destination
        );

        // Convert Calendar to LocalDateTime
        LocalDateTime scheduledTime = LocalDateTime.of(
                selectedTime.get(Calendar.YEAR),
                selectedTime.get(Calendar.MONTH) + 1,
                selectedTime.get(Calendar.DAY_OF_MONTH),
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE)
        );

        Long passengerId = SessionManager.userId;

        CreateRideRequest request = new CreateRideRequest(
                passengerId,
                originLocation,
                destinationLocation,
                routeResult.stopLocations,
                linkedPassengers,
                vehicleType,
                scheduledTime,
                babyFriendly,
                petFriendly,
                routeResult.durationMinutes,
                routeResult.distanceKm
        );

        sendRideToBackend(request, routeResult);
    }

    private void sendRideToBackend(CreateRideRequest request, RouteCalculator.RouteCalculationResult routeResult) {
        Call<RideResponse> call = ClientUtils.rideService.createRide(request);

        call.enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> response) {
                resetScheduleButton();

                if (response.isSuccessful() && response.body() != null) {
                    handleRideResponse(response.body(), routeResult);
                } else {
                    Toast.makeText(getContext(), "Failed to create ride: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                resetScheduleButton();
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleRideResponse(RideResponse response, RouteCalculator.RouteCalculationResult routeResult) {

        if (response.getStatus() == RideStatus.SCHEDULED) {
            Toast.makeText(getContext(), "Ride scheduled", Toast.LENGTH_LONG).show();

            // Create routedata with all inputs
            RouteData routeData = createRouteData(routeResult);

            // Navigate to map
            navigateToMap(routeData);

            clearForm();
        } else if (response.getStatus() == RideStatus.FAILED) {
            Toast.makeText(getContext(), "No available drivers at the moment", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Ride status: " + response.getStatus(), Toast.LENGTH_LONG).show();
        }
    }

    private RouteData createRouteData(RouteCalculator.RouteCalculationResult result) {
        RouteData routeData = new RouteData();
        routeData.setOrigin(result.origin);
        routeData.setDestination(result.destination);
        routeData.setStops(result.stops);
        routeData.setOriginLng(result.originLng);
        routeData.setOriginLat(result.originLat);
        routeData.setDestinationLng(result.destLng);
        routeData.setDestinationLat(result.destLat);
        routeData.setDistanceKm(result.distanceKm);
        routeData.setDurationMinutes(result.durationMinutes);
        routeData.setStopLocations(result.stopLocations);
        routeData.setRouteCoordinates(result.routeCoordinates);
        return routeData;
    }

    private void navigateToMap(RouteData routeData) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ROUTE_DATA", routeData);
        homeFragment.setArguments(bundle);

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, homeFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void resetScheduleButton() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                btnScheduleRide.setEnabled(true);
                btnScheduleRide.setText("Schedule Ride");
            });
        }
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
