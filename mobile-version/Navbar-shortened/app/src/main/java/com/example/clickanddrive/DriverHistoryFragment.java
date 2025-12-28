package com.example.clickanddrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.dtosample.DriverHistorySampleDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DriverHistoryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_history, container, false);

        RecyclerView rv = view.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // TEST DATA
        List<DriverHistorySampleDTO> historyList = new ArrayList<>();


        historyList.add(new DriverHistorySampleDTO(
                1L, LocalDateTime.now().minusHours(2), "Bulevar oslobođenja 4", "Futoška 12", 1250.0, false
        ));


        historyList.add(new DriverHistorySampleDTO(
                2L, LocalDateTime.now().minusDays(1).minusHours(5), "Subotička 1", "Trg Slobode 3", 800.0, true
        ));


        historyList.add(new DriverHistorySampleDTO(
                3L, LocalDateTime.now().minusDays(2).withHour(23).withMinute(15), "Liman 4", "Petrovaradin", 1550.0, false
        ));


        historyList.add(new DriverHistorySampleDTO(
                4L, LocalDateTime.now().minusDays(3).withHour(10).withMinute(30), "Sremska 2", "Železnička stanica", 450.0, false
        ));


        historyList.add(new DriverHistorySampleDTO(
                5L, LocalDateTime.now().minusWeeks(1).withDayOfMonth(20), "Veternik", "Centar", 2100.0, false
        ));


        historyList.add(new DriverHistorySampleDTO(
                6L, LocalDateTime.now().minusWeeks(1).minusDays(1), "Futoški put 10", "Telep", 950.0, true
        ));


        historyList.add(new DriverHistorySampleDTO(
                7L, LocalDateTime.now().minusWeeks(2), "Dunavska 1", "Promenada", 700.0, false
        ));

        // Here goes that list into adapter
        RideAdapter adapter = new RideAdapter(historyList);
        rv.setAdapter(adapter);

        return view;
    }
}