package com.example.clickanddrive;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.adapters.AdminRideAdapter;
import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.responses.AdminRideHistoryResponse;
import com.example.clickanddrive.dtosample.responses.AdminUserResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHistoryFragment extends Fragment {

    private final List<AdminRideHistoryResponse> allRides = new ArrayList<>();
    private final List<AdminRideHistoryResponse> filteredRides = new ArrayList<>();
    private final List<AdminUserResponse> allUsers = new ArrayList<>();
    private final List<AdminUserResponse> visibleUsers = new ArrayList<>();

    private AdminRideAdapter adapter;
    private Spinner spRole;
    private Spinner spUser;
    private Spinner spSort;
    private EditText etFrom;
    private EditText etTo;

    private ArrayAdapter<String> roleAdapter;
    private ArrayAdapter<AdminUserResponse> userAdapter;
    private ArrayAdapter<String> sortAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_history, container, false);

        spRole = view.findViewById(R.id.spRole);
        spUser = view.findViewById(R.id.spUser);
        spSort = view.findViewById(R.id.spSort);
        etFrom = view.findViewById(R.id.etFromDate);
        etTo = view.findViewById(R.id.etToDate);
        RecyclerView rv = view.findViewById(R.id.rvHistory);

        adapter = new AdminRideAdapter(filteredRides);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        setupSpinners();
        setupDatePickers();

        view.findViewById(R.id.btnSearch).setOnClickListener(v -> fetchRideHistory());
        view.findViewById(R.id.btnClearFilters).setOnClickListener(v -> clearFilters());

        loadUsers();

        return view;
    }

    private void setupSpinners() {
        roleAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"driver", "passenger"}
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        userAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                visibleUsers
        );
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUser.setAdapter(userAdapter);

        sortAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"date", "startTime", "endTime", "price", "status"}
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(sortAdapter);

        spRole.setOnItemSelectedListener(new SimpleItemSelectedListener(position -> filterUsersByRole()));
    }

    private void setupDatePickers() {
        etFrom.setOnClickListener(v -> showDatePicker(etFrom));
        etTo.setOnClickListener(v -> showDatePicker(etTo));
    }

    private void loadUsers() {
        ClientUtils.adminService.getAllUsers().enqueue(new Callback<List<AdminUserResponse>>() {
            @Override
            public void onResponse(Call<List<AdminUserResponse>> call, Response<List<AdminUserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allUsers.clear();
                    allUsers.addAll(response.body());
                    filterUsersByRole();
                } else {
                    Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminUserResponse>> call, Throwable t) {
                Log.e("ADMIN_HISTORY", "User load error: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Network error while loading users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUsersByRole() {
        String selectedRole = (String) spRole.getSelectedItem();
        if (selectedRole == null) return;

        visibleUsers.clear();
        for (AdminUserResponse user : allUsers) {
            if (selectedRole.equalsIgnoreCase("driver") && "DRIVER".equalsIgnoreCase(user.getRole())) {
                visibleUsers.add(user);
            } else if (selectedRole.equalsIgnoreCase("passenger") && "PASSENGER".equalsIgnoreCase(user.getRole())) {
                visibleUsers.add(user);
            }
        }
        userAdapter.notifyDataSetChanged();
    }

    private void fetchRideHistory() {
        if (spUser.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Select a user first", Toast.LENGTH_SHORT).show();
            return;
        }

        AdminUserResponse selectedUser = (AdminUserResponse) spUser.getSelectedItem();
        String role = ((String) spRole.getSelectedItem()).toLowerCase();
        String sortBy = (String) spSort.getSelectedItem();

        ClientUtils.adminService.getRideHistory(selectedUser.getId(), role, sortBy)
                .enqueue(new Callback<List<AdminRideHistoryResponse>>() {
                    @Override
                    public void onResponse(Call<List<AdminRideHistoryResponse>> call, Response<List<AdminRideHistoryResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allRides.clear();
                            allRides.addAll(response.body());
                            applyDateFilter();
                        } else {
                            Log.e("ADMIN_HISTORY", "Response code: " + response.code());
                            Toast.makeText(getContext(), "Failed to load ride history", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AdminRideHistoryResponse>> call, Throwable t) {
                        Log.e("ADMIN_HISTORY", "History load error: " + t.getMessage(), t);
                        Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyDateFilter() {
        String fromStr = etFrom.getText().toString().trim();
        String toStr = etTo.getText().toString().trim();

        LocalDate fromDate = fromStr.isEmpty() ? LocalDate.MIN : LocalDate.parse(fromStr);
        LocalDate toDate = toStr.isEmpty() ? LocalDate.MAX : LocalDate.parse(toStr);

        filteredRides.clear();

        for (AdminRideHistoryResponse ride : allRides) {
            try {
                LocalDate rideDate = LocalDateTime.parse(ride.getStartTime()).toLocalDate();
                if (!rideDate.isBefore(fromDate) && !rideDate.isAfter(toDate)) {
                    filteredRides.add(ride);
                }
            } catch (Exception e) {
                Log.e("ADMIN_HISTORY", "Date parse error for ride " + ride.getId(), e);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void clearFilters() {
        etFrom.setText("");
        etTo.setText("");
        filteredRides.clear();
        filteredRides.addAll(allRides);
        adapter.notifyDataSetChanged();
    }

    private void showDatePicker(final EditText editText) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(
                getContext(),
                (view, year, month, day) -> {
                    String date = year + "-" +
                            String.format("%02d", month + 1) + "-" +
                            String.format("%02d", day);
                    editText.setText(date);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}