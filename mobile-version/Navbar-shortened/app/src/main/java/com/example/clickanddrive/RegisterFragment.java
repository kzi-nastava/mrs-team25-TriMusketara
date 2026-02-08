package com.example.clickanddrive;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.clients.services.UserService;
import com.example.clickanddrive.dtosample.requests.UserRegistrationRequestDTO;
import com.example.clickanddrive.dtosample.responses.UserProfileResponseDTO;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private TextInputEditText etName, etLastName, etEmail, etPassword, etConfirmPassword, etAddress, etPhone;
    private Button btnRegister;
    private ProgressDialog progressDialog;
    private UserService userService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etName = view.findViewById(R.id.et_name);
        etLastName = view.findViewById(R.id.et_last_name);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        etAddress = view.findViewById(R.id.et_address);
        etPhone = view.findViewById(R.id.et_phone);
        btnRegister = view.findViewById(R.id.btn_register_submit);

        userService = ClientUtils.userService;

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);

        btnRegister.setOnClickListener(v -> attemptRegister());

        return view;
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Simple validation
        if (TextUtils.isEmpty(name)) { etName.setError("Required"); return; }
        if (TextUtils.isEmpty(lastName)) { etLastName.setError("Required"); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Required"); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Required"); return; }
        if (!password.equals(confirmPassword)) { etConfirmPassword.setError("Passwords do not match"); return; }
        if (TextUtils.isEmpty(address)) { etAddress.setError("Required"); return; }
        if (TextUtils.isEmpty(phone)) { etPhone.setError("Required"); return; }

        progressDialog.show();

        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO(
                name, lastName, email, password, confirmPassword, address, phone, true
        );

        userService.registerPassenger(request).enqueue(new Callback<UserProfileResponseDTO>() {
            @Override
            public void onResponse(Call<UserProfileResponseDTO> call, Response<UserProfileResponseDTO> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Registration successful! Check your email to activate account.", Toast.LENGTH_LONG).show();
                    // Go to login fragment
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, new LoginFragment())
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Registration failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponseDTO> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}