package com.example.clickanddrive;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.dtosample.DriverHistorySampleDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

public class DriverHistoryFragment extends Fragment {
    private List<DriverHistorySampleDTO> allRides;
    private List<DriverHistorySampleDTO> filteredRides;
    private RideAdapter adapter;
    private EditText etFrom, etTo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver_history, container, false);

        // connecting XML IDs
        etFrom = view.findViewById(R.id.etFromDate);
        etTo = view.findViewById(R.id.etToDate);

        View btnSearch = view.findViewById(R.id.btnSearch);

        View btnClear = view.findViewById(R.id.btnClearFilters);
        btnClear.setOnClickListener(v -> {
            etFrom.setText("");
            etTo.setText("");
            filterData();
        });

        RecyclerView rv = view.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // TEST DATA
        allRides = new ArrayList<>();


        allRides.add(new DriverHistorySampleDTO(
                1L, LocalDateTime.now().minusHours(2), "Bulevar oslobođenja 4", "Futoška 12", 1250.0, false
        ));


        allRides.add(new DriverHistorySampleDTO(
                2L, LocalDateTime.now().minusDays(1).minusHours(5), "Subotička 1", "Trg Slobode 3", 800.0, true
        ));


        allRides.add(new DriverHistorySampleDTO(
                3L, LocalDateTime.now().minusDays(2).withHour(23).withMinute(15), "Liman 4", "Petrovaradin", 1550.0, false
        ));


        allRides.add(new DriverHistorySampleDTO(
                4L, LocalDateTime.now().minusDays(3).withHour(10).withMinute(30), "Sremska 2", "Železnička stanica", 450.0, false
        ));


        allRides.add(new DriverHistorySampleDTO(
                5L, LocalDateTime.now().minusWeeks(1).withDayOfMonth(20), "Veternik", "Centar", 2100.0, false
        ));


        allRides.add(new DriverHistorySampleDTO(
                6L, LocalDateTime.now().minusWeeks(1).minusDays(1), "Futoški put 10", "Telep", 950.0, true
        ));


        allRides.add(new DriverHistorySampleDTO(
                7L, LocalDateTime.now().minusWeeks(2), "Dunavska 1", "Promenada", 700.0, false
        ));


        filteredRides = new ArrayList<>(allRides);
        adapter = new RideAdapter(filteredRides);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        // Click opens calendar
        etFrom.setOnClickListener(v -> showDatePicker(etFrom));
        etTo.setOnClickListener(v -> showDatePicker(etTo));


        btnSearch.setOnClickListener(v -> filterData());

        return view;
    }

    private void showDatePicker(final EditText editText) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, day) -> {
            String date = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
            editText.setText(date);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void filterData() {
        String fromStr = etFrom.getText().toString();
        String toStr = etTo.getText().toString();

        LocalDate fromDate = LocalDate.MIN;
        LocalDate toDate = LocalDate.MAX;

        if (!fromStr.isEmpty()) {
            fromDate = LocalDate.parse(fromStr);
        }

        if (!toStr.isEmpty()) {
            toDate = LocalDate.parse(toStr);
        }

        filteredRides.clear();
        for (DriverHistorySampleDTO ride : allRides) {
            LocalDate rideDate = ride.getStartTime().toLocalDate();

            if (!rideDate.isBefore(fromDate) && !rideDate.isAfter(toDate)) {
                filteredRides.add(ride);
            }
        }
        adapter.notifyDataSetChanged();
    }
}