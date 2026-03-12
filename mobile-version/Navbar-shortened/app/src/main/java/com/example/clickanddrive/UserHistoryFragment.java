package com.example.clickanddrive;

import android.app.DatePickerDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import com.example.clickanddrive.adapters.UserRideAdapter;
import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.responses.PassengerRideHistoryResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHistoryFragment extends Fragment implements SensorEventListener {

    private final List<PassengerRideHistoryResponse> allRides = new ArrayList<>();
    private final List<PassengerRideHistoryResponse> filteredRides = new ArrayList<>();

    private UserRideAdapter adapter;
    private EditText etFrom, etTo;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private boolean newestFirst = true;
    private long lastShakeTime = 0L;

    private static final float SHAKE_THRESHOLD = 12.0f;
    private static final int SHAKE_DELAY_MS = 800;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_history, container, false);

        etFrom = view.findViewById(R.id.etFromDate);
        etTo = view.findViewById(R.id.etToDate);
        RecyclerView rv = view.findViewById(R.id.rvHistory);

        adapter = new UserRideAdapter(filteredRides);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        view.findViewById(R.id.btnSearch).setOnClickListener(v -> filterData());

        view.findViewById(R.id.btnClearFilters).setOnClickListener(v -> {
            etFrom.setText("");
            etTo.setText("");
            filteredRides.clear();
            filteredRides.addAll(allRides);
            sortCurrentList();
            adapter.notifyDataSetChanged();
        });

        etFrom.setOnClickListener(v -> showDatePicker(etFrom));
        etTo.setOnClickListener(v -> showDatePicker(etTo));

        setupShakeSensor();
        fetchHistory(SessionManager.userId);

        return view;
    }

    private void fetchHistory(Long passengerId) {
        if (passengerId == null) {
            Toast.makeText(getContext(), "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<PassengerRideHistoryResponse>> call =
                ClientUtils.passengerService.getPassengerRideHistory(passengerId);

        call.enqueue(new Callback<List<PassengerRideHistoryResponse>>() {
            @Override
            public void onResponse(Call<List<PassengerRideHistoryResponse>> call,
                                   Response<List<PassengerRideHistoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allRides.clear();
                    allRides.addAll(response.body());

                    filteredRides.clear();
                    filteredRides.addAll(allRides);

                    sortCurrentList();
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("USER_HISTORY", "Response code: " + response.code());
                    Toast.makeText(getContext(), "Failed to load ride history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PassengerRideHistoryResponse>> call, Throwable t) {
                Log.e("USER_HISTORY", "Error: " + t.getMessage());
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

        for (PassengerRideHistoryResponse ride : allRides) {
            try {
                LocalDate rideDate = LocalDateTime.parse(ride.getStartTime()).toLocalDate();

                if (!rideDate.isBefore(fromDate) && !rideDate.isAfter(toDate)) {
                    filteredRides.add(ride);
                }
            } catch (Exception e) {
                Log.e("USER_HISTORY", "Date parse error for ride " + ride.getId());
            }
        }

        sortCurrentList();
        adapter.notifyDataSetChanged();
    }

    private void sortCurrentList() {
        filteredRides.sort((r1, r2) -> {
            try {
                LocalDateTime d1 = LocalDateTime.parse(r1.getStartTime());
                LocalDateTime d2 = LocalDateTime.parse(r2.getStartTime());
                return newestFirst ? d2.compareTo(d1) : d1.compareTo(d2);
            } catch (Exception e) {
                return 0;
            }
        });
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

    private void setupShakeSensor() {
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null || event.values == null || event.values.length < 3) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

        long now = System.currentTimeMillis();
        if (acceleration > SHAKE_THRESHOLD && now - lastShakeTime > SHAKE_DELAY_MS) {
            lastShakeTime = now;
            newestFirst = !newestFirst;
            sortCurrentList();
            adapter.notifyDataSetChanged();

            String message = newestFirst
                    ? "Sorted by newest first"
                    : "Sorted by oldest first";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}