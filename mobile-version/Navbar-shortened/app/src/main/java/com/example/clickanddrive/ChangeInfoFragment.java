package com.example.clickanddrive;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.enumerations.VehicleType;
import com.example.clickanddrive.dtosample.requests.ChangePasswordRequest;
import com.example.clickanddrive.dtosample.requests.UpdateProfileRequest;
import com.example.clickanddrive.dtosample.requests.UpdateVehicleRequest;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;
import com.example.clickanddrive.dtosample.responses.VehicleResponse;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeInfoFragment extends Fragment {

    private static final String TAG = "ChangeInfoFragment";

    // Account information fields
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etAddress;
    private EditText etMobile;

    // Vehicle Information fields (only for drivers)
    private EditText etModel;
    private EditText etRegistration;
    private Spinner spinnerVehicleType;
    private ToggleButton toggleBabyFriendly;
    private ToggleButton togglePetFriendly;

    private Button btnConfirmChange;
    private Button btnChangePassword;
    private Button btnNextToVehicle;
    private Button btnBackToAccount;
    private LinearLayout accountInfoSection;
    private LinearLayout vehicleInfoSection;
    private LinearLayout navigationButtons;

    // Original data from backend (to compare changes)
    private UserProfileResponse originalUserData;

    private VehicleResponse originalVehicle;

    // Hardcoded user ID
    private static final Long TEMP_USER_ID = 2L;

    // Vehicle types
    private static final VehicleType[] VEHICLE_TYPES = VehicleType.values();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_info, container, false);

        // Initialize view
        initializeView(view);

        // Setup vehicle type spinner
        setUpVehicleType();

        // Setup driver layout
        setUpDriverLayout();

        // Load user data from backend
        loadUserProfile();

        if (SessionManager.currentUserType == SessionManager.DRIVER) {
            // Load drivers vehicle from backend
            loadVehicle();
        }

        // Setup button listeners
        setUpButtonListener();

        return view;
    }


    // Helper for initializing all views
    private void initializeView(View view) {
        // Account information
        etFirstName = view.findViewById(R.id.et_first_name);
        etLastName = view.findViewById(R.id.et_last_name);
        etEmail = view.findViewById(R.id.et_email);
        etAddress = view.findViewById(R.id.et_address);
        etMobile = view.findViewById(R.id.et_mobile);

        // Vehicle information
        etModel = view.findViewById(R.id.et_model);
        etRegistration = view.findViewById(R.id.et_registration);
        spinnerVehicleType = view.findViewById(R.id.vehicle_type);
        toggleBabyFriendly = view.findViewById(R.id.toggle_baby_friendly);
        togglePetFriendly = view.findViewById(R.id.toggle_pet_friendly);

        // Buttons
        btnConfirmChange = view.findViewById(R.id.btn_confirm_change); // confirming user info change
        btnChangePassword = view.findViewById(R.id.btn_change_password); // open change password dialog
        btnNextToVehicle = view.findViewById(R.id.btn_next_to_vehicle);
        btnBackToAccount = view.findViewById(R.id.btn_back_to_account);

        // Sections
        accountInfoSection = view.findViewById(R.id.account_info_section);
        vehicleInfoSection = view.findViewById(R.id.vehicle_info_section);
        navigationButtons = view.findViewById(R.id.navigation_buttons);
    }

    // Setting up vehicle type dropdown
    private void setUpVehicleType() {
        // Convert enum array to String array for display
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

    // Set up driver layout, for vehicle info display
    private void setUpDriverLayout() {
        // Which user is currently logged in
        int userType = SessionManager.currentUserType;
        // When showing account information, should you show the vehicle information too
        // Only if the user is a driver, if not hide it
        if (userType == SessionManager.DRIVER) {
            navigationButtons.setVisibility(View.VISIBLE);
        }
        else {
            navigationButtons.setVisibility(View.GONE);
        }
    }

    private void setUpButtonListener() {
        btnConfirmChange.setOnClickListener(v -> handleProfileUpdate());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnNextToVehicle.setOnClickListener(v -> showVehicleSection());
        btnBackToAccount.setOnClickListener(v -> showAccountSection());
    }

    // Change the visibility of each section
    private void showVehicleSection() {
        accountInfoSection.setVisibility(View.GONE);
        vehicleInfoSection.setVisibility(View.VISIBLE);
    }

    private void showAccountSection() {
        vehicleInfoSection.setVisibility(View.GONE);
        accountInfoSection.setVisibility(View.VISIBLE);
    }

    // Load user profile
    private void loadUserProfile() {
        Log.d(TAG, "Loading user profile for ID: " + TEMP_USER_ID);

        Long userId = SessionManager.userId;
        Call<UserProfileResponse> call = ClientUtils.userService.getUserProfile(userId);

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    originalUserData = response.body();
                    populateAccountForm(originalUserData); // Load users information
                    Log.d(TAG, "User profile loaded successfully" + originalUserData);
                } else {
                    Log.e(TAG, "Failed to load user profile. Code: " + response.code());
                    Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Error loading user profile", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load user profile
    private void loadVehicle() {
        Log.d(TAG, "Loading user profile for ID: " + TEMP_USER_ID);

        Long userId = SessionManager.userId;
        Call<VehicleResponse> call = ClientUtils.driverService.getVehicle(userId);

        call.enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    originalVehicle = response.body();
                    populateVehicleForm(originalVehicle); // Load users information
                    Log.d(TAG, "User vehicle loaded successfully" + originalVehicle);
                } else {
                    Log.e(TAG, "Failed to load VEHICLE. Code: " + response.code());
                    Toast.makeText(getContext(), "Failed to load VEHICLE", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                Log.e(TAG, "Error loading driver vehicle", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Populate the data form
    private void populateAccountForm(UserProfileResponse userData) {
        // Populate account information
        etFirstName.setText(userData.getName());
        etLastName.setText(userData.getSurname());
        etEmail.setText(userData.getEmail());
        etAddress.setText(userData.getAddress());
        etMobile.setText(userData.getPhone());
    }

    private void populateVehicleForm(VehicleResponse vehicleData) {
        // Populate account information
        etModel.setText(vehicleData.getModel());
        etRegistration.setText(vehicleData.getRegistration());
        setVehicleTypeSelection(vehicleData.getType());
        togglePetFriendly.setChecked(vehicleData.getPetFriendly());
        toggleBabyFriendly.setChecked(vehicleData.getBabyFriendly());
    }

    private void setVehicleTypeSelection(VehicleType vehicleType) {
        for (int i = 0; i < VEHICLE_TYPES.length; i++) {
            if (VEHICLE_TYPES[i] == vehicleType) {
                spinnerVehicleType.setSelection(i);
                break;
            }
        }
    }

    // Update profile handling
    // Validation
    private void handleProfileUpdate() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Check if any changes were made
        if (!hasChanges()) {
            Toast.makeText(getContext(), "No changes detected", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProfileRequest request = buildUpdateRequest();

        // Send updated profile to backend
        updateProfile(request);
    }

    private boolean validateInput() {
        // Account fields validation
        if (!validateAccountInput()) {
            return false;
        }

        // Driver vehicle validation
        if (SessionManager.currentUserType == SessionManager.DRIVER) {
            if (!validateVehicleInput()) {
                return false;
            }
        }

        return true;
    }

    private boolean validateAccountInput() {
        String firstName = etFirstName.getText().toString().trim();
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            showAccountSection();
            return false;
        }
        if (firstName.length() > 35) {
            etFirstName.setError("First name is too long (max 35 characters)");
            etFirstName.requestFocus();
            showAccountSection();
            return false;
        }

        String lastName = etLastName.getText().toString().trim();
        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            showAccountSection();
            return false;
        }
        if (lastName.length() > 40) {
            etLastName.setError("Last name is too long (max 40 characters)");
            etLastName.requestFocus();
            showAccountSection();
            return false;
        }

        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            showAccountSection();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            showAccountSection();
            return false;
        }

        String address = etAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Address is required");
            etAddress.requestFocus();
            showAccountSection();
            return false;
        }

        String phone = etMobile.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            etMobile.setError("Phone number is required");
            etMobile.requestFocus();
            showAccountSection();
            return false;
        }
        if (!phone.matches("^(\\+381|0)?[6-7]\\d{7,8}$")) {
            etMobile.setError("Please enter a valid phone number");
            etMobile.requestFocus();
            showAccountSection();
            return false;
        }

        return true;
    }

    private boolean validateVehicleInput() {
        String model = etModel.getText().toString().trim();
        String registration = etRegistration.getText().toString().trim();

        if (TextUtils.isEmpty(model)) {
            etModel.setError("Vehicle model is required");
            etModel.requestFocus();
            showVehicleSection(); // Switch to vehicle section to show error
            return false;
        }

        if (TextUtils.isEmpty(registration)) {
            etRegistration.setError("Registration plate is required");
            etRegistration.requestFocus();
            showVehicleSection();
            return false;
        }

        return true;
    }

    // Check if any data has changed
    // If a user enters this fragment,
    // but does not change any data and still pressed the button, do nothing, no changes detected
    private boolean hasChanges() {
        if (originalUserData == null) {
            return true;
        }
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etMobile.getText().toString().trim();

        // Check if any field has changed
        boolean accountChanged = !firstName.equals(originalUserData.getName()) ||
                !lastName.equals(originalUserData.getSurname()) ||
                !email.equals(originalUserData.getEmail()) ||
                !address.equals(originalUserData.getAddress()) ||
                !phone.equals(originalUserData.getPhone());

        // Check for vehicle changes if a user is driver
        boolean vehicleChanged = false;
        if (SessionManager.currentUserType == SessionManager.DRIVER) {
            vehicleChanged = hasVehicleChanged();
        }
        // Return if either info has been changed
        return accountChanged || vehicleChanged;
    }

    private boolean hasVehicleChanged() {
        if (originalVehicle == null) {
            return true;
        }
        String model = etModel.getText().toString().trim();
        String registration = etRegistration.getText().toString().trim();
        int selectedPosition = spinnerVehicleType.getSelectedItemPosition();
        VehicleType type = VEHICLE_TYPES[selectedPosition];
        boolean babyFriendly = toggleBabyFriendly.isChecked();
        boolean petFriendly = togglePetFriendly.isChecked();

        // Check if any field has changed
        return !model.equals(originalVehicle.getModel()) ||
                !registration.equals(originalVehicle.getRegistration()) ||
                type != originalVehicle.getType() ||
                babyFriendly != originalVehicle.getBabyFriendly() ||
                petFriendly != originalVehicle.getPetFriendly();
    }

    private UpdateProfileRequest buildUpdateRequest() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etMobile.getText().toString().trim();

        UpdateVehicleRequest vehicleRequest = null;

        if (SessionManager.currentUserType == SessionManager.DRIVER) {
            String model = etModel.getText().toString().trim();
            String registration = etRegistration.getText().toString().trim();
            int selected = spinnerVehicleType.getSelectedItemPosition();
            VehicleType type = VEHICLE_TYPES[selected];
            boolean babyFriendly = toggleBabyFriendly.isChecked();
            boolean petFriendly = togglePetFriendly.isChecked();

            vehicleRequest = new UpdateVehicleRequest(model, type, registration, babyFriendly, petFriendly);
            //...
        }

        return new UpdateProfileRequest(firstName, lastName, email, address, phone, vehicleRequest);
    }

    // Update changes
    private void updateProfile(UpdateProfileRequest request) {
        Log.d(TAG, "Updating profile:" + request.toString());

        Long userId = SessionManager.userId;
        Call<UserProfileResponse> call = ClientUtils.userService.updateUserProfile(userId, request);

        call.enqueue(new Callback<UserProfileResponse>() {

            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    originalUserData = response.body();
                    Log.d(TAG, "Profile updated successfully" + originalUserData.toString());
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Log.e(TAG, "Failed to update profile. Code: " + response.code());
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Error updating profile", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showChangePasswordDialog() {
        // Create dialog
        AlertDialog dialog = createChangePasswordDialog();
        dialog.show();
    }

    // Create the change password dialog
    private AlertDialog createChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow()
                .setBackgroundDrawableResource(android.R.color.transparent);

        TextInputEditText etCurrentPassword =
                dialogView.findViewById(R.id.et_current_password);
        TextInputEditText etNewPassword =
                dialogView.findViewById(R.id.et_new_password);
        TextInputEditText etConfirmPassword =
                dialogView.findViewById(R.id.et_confirm_password);

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm_password_change);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            if (validatePasswords(etCurrentPassword, etNewPassword, etConfirmPassword)) {
                // Build request for changing password
                Long userId = SessionManager.userId;
                ChangePasswordRequest request = new ChangePasswordRequest(
                                                        userId,
                                                        etCurrentPassword.getText().toString().trim(),
                                                        etNewPassword.getText().toString().trim(),
                                                        etConfirmPassword.getText().toString().trim());
                changePassword(request);
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private boolean validatePasswords(
            TextInputEditText current,
            TextInputEditText newPassword,
            TextInputEditText confirmPassword
    ) {
        String currentPass = current.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPass)) {
            current.setError("Current password is required");
            current.requestFocus();
        }

        if (TextUtils.isEmpty(newPass)) {
            newPassword.setError("New password is required");
            newPassword.requestFocus();
        }

        if (newPass.length() < 8) {
            newPassword.setError("Password must be at least 8 characters");
            return false;
        }

        if (TextUtils.isEmpty(confirmPass)) {
            confirmPassword.setError("Please confirm your password");
            return false;
        }

        if (!newPass.equals(confirmPass)) {
            confirmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    // Change password backend
    private void changePassword(ChangePasswordRequest request) {
        //...

        Call<Map<String, String>> call = ClientUtils.userService.changePassword(request);

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Password updated successfully");
                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to update password.");
                    Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e(TAG, "Error updating password", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}