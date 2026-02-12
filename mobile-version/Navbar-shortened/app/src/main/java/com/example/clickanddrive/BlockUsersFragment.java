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
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


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

    @Override
    public void onUserClick(UserProfileResponse user) {
        BlockAUserBottomSheet sheet = BlockAUserBottomSheet.newInstance(
                user.getName(),
                user.getSurname(),
                user.getAddress(),
                user.getEmail(),
                user.getPhone()
        );
        sheet.show(getChildFragmentManager(), "admin_block_sheet");
    }

    private void loadDrivers() {
        // mock
        allUsers.clear();
        allUsers.add(new UserProfileResponse(1L, "driver1@test.com", "Marko", "Marković", "Beograd", "0601234567", null));
        allUsers.add(new UserProfileResponse(2L, "driver2@test.com", "Petar", "Petrović", "Novi Sad", "0607654321", null));
        allUsers.add(new UserProfileResponse(3L, "driver3@test.com", "Jovan", "Jovanović", "Niš", "0609876543", null));

        filteredUsers.clear();
        filteredUsers.addAll(allUsers);
        adapter.updateList(filteredUsers);
    }

    private void loadPassengers() {
        // mock
        allUsers.clear();
        allUsers.add(new UserProfileResponse(4L, "passenger1@test.com", "Ana", "Anić", "Beograd", "0611234567", null));
        allUsers.add(new UserProfileResponse(5L, "passenger2@test.com", "Milica", "Milić", "Subotica", "0617654321", null));

        filteredUsers.clear();
        filteredUsers.addAll(allUsers);
        adapter.updateList(filteredUsers);
    }
}