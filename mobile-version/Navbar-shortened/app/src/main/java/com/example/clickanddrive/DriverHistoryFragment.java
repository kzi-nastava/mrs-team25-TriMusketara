package com.example.clickanddrive;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.adapters.RideAdapter;
import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.responses.DriverRideHistoryResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHistoryFragment extends Fragment {

    private final List<DriverRideHistoryResponse> allRides = new ArrayList<>();
    private final List<DriverRideHistoryResponse> filteredRides = new ArrayList<>();

    private RideAdapter adapter;
    private EditText etFrom, etTo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_history, container, false);

        etFrom = view.findViewById(R.id.etFromDate);
        etTo = view.findViewById(R.id.etToDate);
        RecyclerView rv = view.findViewById(R.id.rvHistory);

        adapter = new RideAdapter(filteredRides);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        view.findViewById(R.id.btnSearch).setOnClickListener(v -> filterData());

        view.findViewById(R.id.btnClearFilters).setOnClickListener(v -> {
            etFrom.setText("");
            etTo.setText("");
            filteredRides.clear();
            filteredRides.addAll(allRides);
            adapter.notifyDataSetChanged();
        });

        etFrom.setOnClickListener(v -> showDatePicker(etFrom));
        etTo.setOnClickListener(v -> showDatePicker(etTo));

        fetchHistory(SessionManager.userId);

        return view;
    }

    private void fetchHistory(Long driverId) {
        if (driverId == null) {
            Log.e("DRIVER_HISTORY", "Driver ID is null");
            Toast.makeText(getContext(), "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<DriverRideHistoryResponse>> call = ClientUtils.driverService.getDriverHistory(driverId);

        call.enqueue(new Callback<List<DriverRideHistoryResponse>>() {
            @Override
            public void onResponse(Call<List<DriverRideHistoryResponse>> call, Response<List<DriverRideHistoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allRides.clear();
                    allRides.addAll(response.body());

                    filteredRides.clear();
                    filteredRides.addAll(allRides);

                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("DRIVER_HISTORY", "Response code: " + response.code());
                    Toast.makeText(getContext(), "Failed to load ride history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DriverRideHistoryResponse>> call, Throwable t) {
                Log.e("DRIVER_HISTORY", "Network/server error: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterData() {
        String fromStr = etFrom.getText().toString().trim();
        String toStr = etTo.getText().toString().trim();

        LocalDate fromDate = fromStr.isEmpty() ? LocalDate.MIN : LocalDate.parse(fromStr);
        LocalDate toDate = toStr.isEmpty() ? LocalDate.MAX : LocalDate.parse(toStr);

        filteredRides.clear();

        for (DriverRideHistoryResponse ride : allRides) {
            try {
                LocalDate rideDate = LocalDateTime.parse(ride.getStartTime()).toLocalDate();

                if (!rideDate.isBefore(fromDate) && !rideDate.isAfter(toDate)) {
                    filteredRides.add(ride);
                }
            } catch (Exception e) {
                Log.e("DRIVER_HISTORY", "Date parse error for ride " + ride.getId(), e);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void showDatePicker(final EditText editText) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(
                getContext(),
                (view, year, month, day) -> {
                    String date = year + "-" +
                            String.format("%02d", (month + 1)) + "-" +
                            String.format("%02d", day);
                    editText.setText(date);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}