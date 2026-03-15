package com.example.clickanddrive;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.adapters.PanicAdapter;
import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.responses.PanicResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanicNotificationsFragment extends Fragment implements PanicAdapter.PanicActionListener {

    private final List<PanicResponse> panicList = new ArrayList<>();
    private PanicAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private int lastKnownCount = -1;

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadPanics(false);
            handler.postDelayed(this, 5000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panic_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvPanics = view.findViewById(R.id.rvPanics);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        adapter = new PanicAdapter(panicList, this);
        rvPanics.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPanics.setAdapter(adapter);

        loadPanics(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(refreshRunnable, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }

    private void loadPanics(boolean initialLoad) {
        if (initialLoad) {
            progressBar.setVisibility(View.VISIBLE);
        }

        ClientUtils.panicService.getUnresolvedPanics().enqueue(new Callback<List<PanicResponse>>() {
            @Override
            public void onResponse(Call<List<PanicResponse>> call, Response<List<PanicResponse>> response) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<PanicResponse> newData = response.body();

                    if (lastKnownCount != -1 && newData.size() > lastKnownCount) {
                        playSound();
                        Toast.makeText(getContext(), "New panic notification received", Toast.LENGTH_SHORT).show();
                    } else if (initialLoad && !newData.isEmpty()) {
                        playSound();
                    }

                    lastKnownCount = newData.size();

                    panicList.clear();
                    panicList.addAll(newData);
                    adapter.notifyDataSetChanged();

                    tvEmpty.setVisibility(panicList.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), "Failed to load panic notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PanicResponse>> call, Throwable t) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playSound() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 700);
    }

    @Override
    public void onResolve(PanicResponse panic) {
        ClientUtils.panicService.resolvePanic(panic.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    panicList.remove(panic);
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(panicList.isEmpty() ? View.VISIBLE : View.GONE);
                    Toast.makeText(getContext(), "Panic resolved", Toast.LENGTH_SHORT).show();
                    lastKnownCount = panicList.size();
                } else {
                    Toast.makeText(getContext(), "Failed to resolve panic", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}