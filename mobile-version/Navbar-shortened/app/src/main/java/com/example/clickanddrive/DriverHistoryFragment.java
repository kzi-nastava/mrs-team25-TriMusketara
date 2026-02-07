package com.example.clickanddrive;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.adapters.RideAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.responses.DriverRideHistoryResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHistoryFragment extends Fragment {
    // Koristiš pravi DTO sada
    private List<DriverRideHistoryResponse> allRides = new ArrayList<>();
    private List<DriverRideHistoryResponse> filteredRides = new ArrayList<>();
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

        // backend call
        fetchHistory(2L); // Privremeno hardkodovan ID drajvera

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

        return view;
    }

    private void fetchHistory(Long driverId) {
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
                    Log.e("API_ERROR", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<DriverRideHistoryResponse>> call, Throwable t) {
                Log.e("API_ERROR", "Mreža ili server: " + t.getMessage());
            }
        });
    }

    private void filterData() {
        String fromStr = etFrom.getText().toString();
        String toStr = etTo.getText().toString();

        LocalDate fromDate = fromStr.isEmpty() ? LocalDate.MIN : LocalDate.parse(fromStr);
        LocalDate toDate = toStr.isEmpty() ? LocalDate.MAX : LocalDate.parse(toStr);

        filteredRides.clear();
        for (DriverRideHistoryResponse ride : allRides) {
            // Parsiramo datum iz stringa koji je stigao sa backenda
            LocalDate rideDate = LocalDateTime.parse(ride.getStartTime()).toLocalDate();

            if (!rideDate.isBefore(fromDate) && !rideDate.isAfter(toDate)) {
                filteredRides.add(ride);
            }
        }
        adapter.notifyDataSetChanged();
    }


    private void showDatePicker(final EditText editText) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, day) -> {
            String date = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
            editText.setText(date);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}