package com.example.clickanddrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.requests.ReportRequest;
import com.example.clickanddrive.dtosample.responses.DailyStats;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;
import com.example.clickanddrive.dtosample.responses.ReportResponse;
import com.example.clickanddrive.dtosample.responses.SummaryStats;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsFragment extends Fragment {

    // --- Views ---
    private Button btnDateFrom, btnDateTo, btnGenerate;
    private LinearLayout layoutAdminOptions, layoutResults;
    private Spinner spinnerReportType, spinnerSpecificUser;
    private ProgressBar progressBar;

    private TextView tvTotalRides, tvTotalKm, tvTotalMoney;
    private TextView tvAvgRides, tvAvgKm, tvAvgMoney;
    private BarChartView barChartView;

    // --- State ---
    private LocalDateTime selectedDateFrom = null;
    private LocalDateTime selectedDateTo   = null;
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String[] REPORT_TYPES = {
            "All drivers", "All passengers", "Specific driver", "Specific passenger"
    };
    private String selectedReportType = "ALL_DRIVERS";

    private List<UserProfileResponse> specificUserList = new ArrayList<>();
    private UserProfileResponse selectedUser = null;
    private boolean specificModeActive = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        bindViews(view);
        setupDatePickers();
        setupAdminOptions();
        setupGenerateButton();

        // Admin gets to choose for who he wants to generate a report
        if (SessionManager.currentUserType == SessionManager.ADMIN) {
            layoutAdminOptions.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void bindViews(View v) {
        btnDateFrom        = v.findViewById(R.id.btn_pick_date_from);
        btnDateTo          = v.findViewById(R.id.btn_pick_date_to);
        btnGenerate        = v.findViewById(R.id.btn_generate_report);
        progressBar        = v.findViewById(R.id.progress_bar_report);
        layoutAdminOptions = v.findViewById(R.id.layout_admin_options);
        layoutResults      = v.findViewById(R.id.layout_results);
        spinnerReportType  = v.findViewById(R.id.spinner_report_type);
        spinnerSpecificUser= v.findViewById(R.id.spinner_specific_user);

        tvTotalRides = v.findViewById(R.id.tv_total_rides);
        tvTotalKm    = v.findViewById(R.id.tv_total_km);
        tvTotalMoney = v.findViewById(R.id.tv_total_money);
        tvAvgRides   = v.findViewById(R.id.tv_avg_rides);
        tvAvgKm      = v.findViewById(R.id.tv_avg_km);
        tvAvgMoney   = v.findViewById(R.id.tv_avg_money);
        barChartView = v.findViewById(R.id.bar_chart_view);
    }

    private void setupDatePickers() {
        btnDateFrom.setOnClickListener(v -> showDatePicker(true));
        btnDateTo.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupAdminOptions() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                REPORT_TYPES
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReportType.setAdapter(typeAdapter);

        spinnerReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos) {
                    case 0:
                        selectedReportType = "ALL_DRIVERS";
                        specificModeActive = false;
                        spinnerSpecificUser.setVisibility(View.GONE);
                        break;
                    case 1:
                        selectedReportType = "ALL_PASSENGERS";
                        specificModeActive = false;
                        spinnerSpecificUser.setVisibility(View.GONE);
                        break;
                    case 2:
                        selectedReportType = "DRIVER";
                        specificModeActive = true;
                        spinnerSpecificUser.setVisibility(View.VISIBLE);
                        loadUsersForSpinner("DRIVER");
                        break;
                    case 3:
                        selectedReportType = "PASSENGER";
                        specificModeActive = true;
                        spinnerSpecificUser.setVisibility(View.VISIBLE);
                        loadUsersForSpinner("PASSENGER");
                        break;
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        spinnerSpecificUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (!specificUserList.isEmpty() && pos < specificUserList.size()) {
                    selectedUser = specificUserList.get(pos);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void loadUsersForSpinner(String role) {
        specificUserList.clear();
        selectedUser = null;

        Callback<List<UserProfileResponse>> callback = new Callback<List<UserProfileResponse>>() {
            @Override
            public void onResponse(Call<List<UserProfileResponse>> call,
                                   Response<List<UserProfileResponse>> response) {
                if (!isAdded() || response.body() == null) return;
                specificUserList = response.body();
                populateUserSpinner();
            }
            @Override
            public void onFailure(Call<List<UserProfileResponse>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Could not load users.", Toast.LENGTH_SHORT).show();
            }
        };

        if ("DRIVER".equals(role)) {
            ClientUtils.adminService.getAllDrivers().enqueue(callback);
        } else {
            ClientUtils.adminService.getAllPassengers().enqueue(callback);
        }
    }

    private void populateUserSpinner() {
        List<String> names = new ArrayList<>();
        for (UserProfileResponse u : specificUserList) {
            names.add(u.getName() + " " + u.getSurname() + " — " + u.getEmail());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecificUser.setAdapter(adapter);
        if (!specificUserList.isEmpty()) selectedUser = specificUserList.get(0);
    }

    private void setupGenerateButton() {
        btnGenerate.setOnClickListener(v -> generateReport());
    }

    private void showDatePicker(boolean isFrom) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isFrom ? "Select start date" : "Select end date")
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            LocalDateTime dt = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();

            if (isFrom) {
                selectedDateFrom = dt.toLocalDate().atStartOfDay();
                btnDateFrom.setText("From: " + dt.format(DISPLAY_FMT));
            } else {
                selectedDateTo = dt.toLocalDate().atTime(23, 59, 59);
                btnDateTo.setText("To: " + dt.format(DISPLAY_FMT));
            }
        });

        picker.show(getChildFragmentManager(), "DATE_PICKER");
    }

    private void generateReport() {
        if (selectedDateFrom == null || selectedDateTo == null) {
            Toast.makeText(getContext(), "Please select both dates.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDateFrom.isAfter(selectedDateTo)) {
            Toast.makeText(getContext(), "Start date cannot be after end date.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validacija za "specific" mode
        if (specificModeActive && selectedUser == null) {
            Toast.makeText(getContext(), "Please select a user.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        layoutResults.setVisibility(View.GONE);

        String userType;
        Long userId = null;

        if (SessionManager.currentUserType == SessionManager.ADMIN) {
            userType = selectedReportType;
            if (specificModeActive && selectedUser != null) {
                userId = selectedUser.getId();
            }
        } else if (SessionManager.currentUserType == SessionManager.DRIVER) {
            userType = "DRIVER";
            userId   = SessionManager.userId;
        } else {
            // Passenger
            userType = "PASSENGER";
            userId   = SessionManager.userId;
        }

        ReportRequest request = new ReportRequest(selectedDateFrom, selectedDateTo, userId, userType);

        ClientUtils.reportService.generateReport(request).enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    displayReport(response.body());
                } else {
                    Toast.makeText(getContext(), "Error generating report. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayReport(ReportResponse report) {
        SummaryStats s = report.getSummary();
        if (s == null) {
            Toast.makeText(getContext(), "No data for the selected period.", Toast.LENGTH_SHORT).show();
            return;
        }

        // TOTALS
        tvTotalRides.setText(String.valueOf(s.getTotalRides()));
        tvTotalKm.setText(String.format("%.1f", s.getTotalKilometers()));

        boolean isEarnings = (SessionManager.currentUserType == SessionManager.DRIVER)
                || (SessionManager.currentUserType == SessionManager.ADMIN
                && (selectedReportType.contains("DRIVER")));
        String moneyPrefix = isEarnings ? "+" : "-";
        tvTotalMoney.setText(moneyPrefix + String.format("%.0f", Math.abs(s.getTotalMoney())));

        tvAvgRides.setText(String.format("%.1f", s.getAvgRidesPerDay()));
        tvAvgKm.setText(String.format("%.1f km", s.getAvgKilometersPerDay()));
        tvAvgMoney.setText(String.format("%.0f RSD", Math.abs(s.getAvgMoneyPerDay())));

        // Chart
        if (report.getDailyStats() != null) {
            barChartView.setData(report.getDailyStats());
        }

        layoutResults.setVisibility(View.VISIBLE);
    }
}
