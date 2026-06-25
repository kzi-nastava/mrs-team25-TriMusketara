package com.example.clickanddrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.VehiclePriceDTO;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPricesFragment extends Fragment {

    private EditText etStandardPrice;
    private EditText etLuxuryPrice;
    private EditText etVanPrice;
    private EditText etPerKmPrice;
    private TextView tvPriceError;
    private Button btnSavePrices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_prices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etStandardPrice = view.findViewById(R.id.etStandardPrice);
        etLuxuryPrice = view.findViewById(R.id.etLuxuryPrice);
        etVanPrice = view.findViewById(R.id.etVanPrice);
        etPerKmPrice = view.findViewById(R.id.etPerKmPrice);
        tvPriceError = view.findViewById(R.id.tvPriceError);
        btnSavePrices = view.findViewById(R.id.btnSavePrices);

        btnSavePrices.setOnClickListener(v -> savePrices());
        loadPrices();
    }

    private void loadPrices() {
        btnSavePrices.setEnabled(false);

        ClientUtils.adminService.getVehiclePrices().enqueue(new Callback<VehiclePriceDTO>() {
            @Override
            public void onResponse(Call<VehiclePriceDTO> call, Response<VehiclePriceDTO> response) {
                if (!isAdded()) return;

                btnSavePrices.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    bindPrices(response.body());
                } else {
                    showError("Failed to load prices");
                }
            }

            @Override
            public void onFailure(Call<VehiclePriceDTO> call, Throwable t) {
                if (!isAdded()) return;

                btnSavePrices.setEnabled(true);
                showError("Network error while loading prices");
            }
        });
    }

    private void bindPrices(VehiclePriceDTO prices) {
        etStandardPrice.setText(formatPrice(prices.getStandardBasePrice()));
        etLuxuryPrice.setText(formatPrice(prices.getLuxuryBasePrice()));
        etVanPrice.setText(formatPrice(prices.getVanBasePrice()));
        etPerKmPrice.setText(formatPrice(prices.getPricePerKm()));
        hideError();
    }

    private void savePrices() {
        Double standard = readPositiveDouble(etStandardPrice, "Standard base price");
        Double luxury = readPositiveDouble(etLuxuryPrice, "Luxury base price");
        Double van = readPositiveDouble(etVanPrice, "Van base price");
        Double perKm = readPositiveDouble(etPerKmPrice, "Price per km");

        if (standard == null || luxury == null || van == null || perKm == null) {
            return;
        }

        VehiclePriceDTO request = new VehiclePriceDTO(standard, luxury, van, perKm);
        btnSavePrices.setEnabled(false);
        hideError();

        ClientUtils.adminService.updateVehiclePrices(request).enqueue(new Callback<VehiclePriceDTO>() {
            @Override
            public void onResponse(Call<VehiclePriceDTO> call, Response<VehiclePriceDTO> response) {
                if (!isAdded()) return;

                btnSavePrices.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    bindPrices(response.body());
                    Toast.makeText(getContext(), "Prices updated", Toast.LENGTH_SHORT).show();
                } else {
                    showError("Failed to update prices");
                }
            }

            @Override
            public void onFailure(Call<VehiclePriceDTO> call, Throwable t) {
                if (!isAdded()) return;

                btnSavePrices.setEnabled(true);
                showError("Network error while updating prices");
            }
        });
    }

    private Double readPositiveDouble(EditText input, String label) {
        String value = input.getText().toString().trim();

        if (value.isEmpty()) {
            showError(label + " is required");
            return null;
        }

        try {
            double number = Double.parseDouble(value);

            if (number < 0) {
                showError(label + " cannot be negative");
                return null;
            }

            return number;
        } catch (NumberFormatException e) {
            showError(label + " must be a valid number");
            return null;
        }
    }

    private String formatPrice(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private void showError(String message) {
        tvPriceError.setText(message);
        tvPriceError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvPriceError.setVisibility(View.GONE);
    }
}