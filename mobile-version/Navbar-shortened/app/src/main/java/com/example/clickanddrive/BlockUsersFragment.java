package com.example.clickanddrive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clickanddrive.adapters.BlockUsersAdapter;
import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BlockUsersFragment extends Fragment implements BlockUsersAdapter.OnUserActionListener {

    private RecyclerView recyclerView;
    private BlockUsersAdapter adapter;
    private EditText searchBar;
    private Button btnDrivers, btnPassengers;

    private List<UserProfileResponse> allUsers = new ArrayList<>();
    private List<UserProfileResponse> filteredUsers = new ArrayList<>();

    private boolean showingDrivers = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_block_users, container, false);

        initializeViews(view);

        // Setup recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BlockUsersAdapter(filteredUsers, this);
        recyclerView.setAdapter(adapter);

        setUpToggleButtons();

        // Search functionality
        setUpSearch();

        loadDrivers();

        return view;

    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.users_recycler_view);
        searchBar = view.findViewById(R.id.search_bar);
        btnDrivers = view.findViewById(R.id.btn_drivers);
        btnPassengers = view.findViewById(R.id.btn_passengers);
    }

    private void setUpToggleButtons() {
        // Drivers button click
        btnDrivers.setOnClickListener(v -> {
            if (!showingDrivers) {
                showingDrivers = true;
                //...
                loadDrivers();
            }
        });

        // Passengers button click
        btnPassengers.setOnClickListener(v -> {
            if (showingDrivers) {
                showingDrivers = false;
                //...
                loadPassengers();
            }
        });
    }

    private void setUpSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
        });
    }

    private void filterUsers(String query) {
        filteredUsers.clear();

        if (query.isEmpty()) {
            filteredUsers.addAll(allUsers);
        } else {
            for (UserProfileResponse user : allUsers) {
                String fullName = (user.getName() + " " + user.getSurname()).toLowerCase();
                if (fullName.contains(query.toLowerCase())) {
                    filteredUsers.add(user);
                }
            }
        }

        adapter.updateList(filteredUsers);
    }

    private void loadDrivers() {
        Call<List<UserProfileResponse>> call = ClientUtils.adminService.getAllDrivers();

        call.enqueue(new Callback<List<UserProfileResponse>>() {
            @Override
            public void onResponse(Call<List<UserProfileResponse>> call, Response<List<UserProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allUsers.clear();
                    allUsers.addAll(response.body());

                    filteredUsers.clear();
                    filteredUsers.addAll(allUsers);

                    adapter.updateList(filteredUsers);
                } else {
                    Toast.makeText(getContext(), "Failed to load drivers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserProfileResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPassengers() {
        Call<List<UserProfileResponse>> call = ClientUtils.adminService.getAllPassengers();

        call.enqueue(new Callback<List<UserProfileResponse>>() {
            @Override
            public void onResponse(Call<List<UserProfileResponse>> call, Response<List<UserProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allUsers.clear();
                    allUsers.addAll(response.body());

                    filteredUsers.clear();
                    filteredUsers.addAll(allUsers);

                    adapter.updateList(filteredUsers);
                } else {
                    Toast.makeText(getContext(), "Failed to load passengers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserProfileResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load passengers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserClick(UserProfileResponse user) {
        BlockAUserBottomSheet sheet = BlockAUserBottomSheet.newInstance(user);
        sheet.show(getChildFragmentManager(), "admin_block_sheet");
    }
}
