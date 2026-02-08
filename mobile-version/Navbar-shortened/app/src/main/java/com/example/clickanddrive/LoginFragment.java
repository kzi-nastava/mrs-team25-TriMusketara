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
import com.example.clickanddrive.dtosample.requests.LoginRequestDTO;
import com.example.clickanddrive.dtosample.responses.LoginResponseDTO;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    private UserService userService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login_submit);

        userService = ClientUtils.userService;

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        btnLogin.setOnClickListener(v -> attemptLogin());

        return view;
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password required");
            return;
        }

        progressDialog.show();

        LoginRequestDTO request = new LoginRequestDTO(email, password);

        Call<LoginResponseDTO> call = userService.login(request);
        call.enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseDTO loginResponse = response.body();

                    SessionManager.login(loginResponse.getRole(),
                            loginResponse.getToken(),
                            loginResponse.getUserId());

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).refreshBottomNavigation();
                    }

                    Toast.makeText(getContext(), "Login successful as " + loginResponse.getRole(),
                            Toast.LENGTH_SHORT).show();

                    HomeFragment homeFragment = new HomeFragment();

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, homeFragment)
                            .addToBackStack(null)
                            .commit();

                } else {
                    Toast.makeText(getContext(), "Login failed: Invalid credentials",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Login failed: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}