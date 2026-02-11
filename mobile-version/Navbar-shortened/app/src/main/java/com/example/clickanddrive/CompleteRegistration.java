package com.example.clickanddrive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.requests.CompleteRegistrationRequest;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteRegistration extends AppCompatActivity {

    private TextInputEditText inputPassword, inputConfirmPassword;
    private Button btnComplete;
    private ProgressBar progressBar;
    private TextView tvWelcome;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complete_registration);

        initializeViews();
        extractToken();
        setListeners();
    }

    private void initializeViews() {
        inputPassword = findViewById(R.id.input_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);
        btnComplete = findViewById(R.id.btn_complete);
        progressBar = findViewById(R.id.progress_bar);
        tvWelcome = findViewById(R.id.tv_welcome);
    }

    private void extractToken() {
        // Extract session token from deep link
        Uri data = getIntent().getData();

        if (data != null) {
            token = data.getQueryParameter("token");
        }

        // No token means invalid link
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Invalid or expired link", Toast.LENGTH_SHORT).show();
            btnComplete.setEnabled(false);
        }
    }

    private void setListeners() {
        btnComplete.setOnClickListener(V -> {
            if (validateForm()) {
                completeRegistration();
            }
        });
    }

    private boolean validateForm() {
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (password.isEmpty()) {
            inputPassword.setError("Password is required");
            return false;
        }

        if (password.length() < 8) {
            inputPassword.setError("Password must have at least 8 characters");
            return false;
        }

        String passwordPattern = "^(?=.*[A-Z])(?=.*[0-9]).+$";
        if (!password.matches(passwordPattern)) {
            inputPassword.setError("Password must contain at least one uppercase letter and one number");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            inputConfirmPassword.setError("Confirm password is required");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            inputConfirmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    // Complete registration process
    private void completeRegistration() {
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // Create request
        CompleteRegistrationRequest request = new CompleteRegistrationRequest(token, password, confirmPassword);

        setLoading(true);

        Call<Map<String, String>> call = ClientUtils.driverService.completeRegistration(request);

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                setLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(CompleteRegistration.this,
                            "Registration complete! You can now log in.",
                            Toast.LENGTH_LONG).show();

                    // Redirect to login after success
                    navigateToLogin();
                } else {
                    Toast.makeText(CompleteRegistration.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(CompleteRegistration.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Navigate to log in when completing registering
    private void navigateToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("OPEN_LOGIN", true);
        startActivity(intent);
        finish();
    }

    public void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnComplete.setEnabled(!isLoading);
    }
}
