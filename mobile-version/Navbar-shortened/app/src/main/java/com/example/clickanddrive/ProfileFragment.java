package com.example.clickanddrive;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private CardView cardGuest, cardMyAccount, cardApplication;
    private LinearLayout containerApplicationButtons;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find by id all of the cards defined above
        cardGuest = view.findViewById(R.id.card_guest);
        cardMyAccount = view.findViewById(R.id.card_my_account);
        cardApplication = view.findViewById(R.id.card_application);
        containerApplicationButtons = view.findViewById(R.id.container_application_buttons);

        // All cards have no visibility
        cardGuest.setVisibility(View.GONE);
        cardMyAccount.setVisibility(View.GONE);
        cardApplication.setVisibility(View.GONE);

        // Button actions
        // ...
        Button btnLogin = view.findViewById(R.id.btn_login);
        Button btnRegister = view.findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(v -> openLoginFragment());
        btnRegister.setOnClickListener(v -> openRegisterFragment());

        // Getting button for changing information by its id
        Button btnChangeInfo = view.findViewById(R.id.btn_change_info);
        btnChangeInfo.setOnClickListener(v -> openProfileChangeInfoFragment());  


        switch (SessionManager.currentUserType) {
            case SessionManager.GUEST:
                cardGuest.setVisibility(View.VISIBLE);
                break;
            case SessionManager.USER:
            case SessionManager.DRIVER:
            case SessionManager.ADMIN:
                cardMyAccount.setVisibility(View.VISIBLE);
                cardApplication.setVisibility(View.VISIBLE);

                // Add different buttons based on the role
                addApplicationButtons(SessionManager.currentUserType);
                break;
        }

        return view;
    }

    private void openLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, loginFragment) 
                .addToBackStack(null) 
                .commit();
    }

    private void openRegisterFragment() {
        RegisterFragment registerFragment = new RegisterFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, registerFragment)
                .addToBackStack(null)
                .commit();
    }

    // Opening a new fragment with user info, along side ability to change info
    private void openProfileChangeInfoFragment() {
        ChangeInfoFragment fragment = new ChangeInfoFragment();
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).addToBackStack(null).commit();
    }

    private void addApplicationButtons(int userType) {
        containerApplicationButtons.removeAllViews();

        switch(userType) {
            case SessionManager.USER:
                addButton("Favorite routes", R.drawable.heart ,() -> {});
                addButton("Ride history", R.drawable.history  ,() -> {});
                addButton("Reports", R.drawable.report,  () -> {});
                addButton("Notes", R.drawable.notes, () -> {});
                addButton("Support", R.drawable.support, () -> {});
                addButton("Log out", R.drawable.logout,  () -> {});
                break;

            case SessionManager.DRIVER:
                addButton("Ride history", R.drawable.history,  () -> openDriverHistoryFragment());
                addButton("Reports", R.drawable.report,  () -> {});
                addButton("Notes", R.drawable.notes, () -> {});
                addButton("Support", R.drawable.support, () -> {});
                addButton("Log out", R.drawable.logout, () -> {});
                break;

            case SessionManager.ADMIN:
                addButton("Register new driver", 0, () -> openDriverRegistrationFragment());
                addButton("Check current rides", 0, () -> {});
                addButton("Change prices", R.drawable.price, () -> {});
                addButton("Ride history", R.drawable.history, () -> {});
                addButton("Requests", 0,  () -> {});
                addButton("Reports", R.drawable.report,  () -> {});
                addButton("Notes", R.drawable.notes,  () -> {});
                addButton("Log out", R.drawable.logout,  () -> {});
                break;
        }
    }

    private View createDivider() {
        View divider = new View(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
        params.setMargins(0,8,0,8);

        divider.setLayoutParams(params);

        divider.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.divider_color)
        );

        return divider;
    }

    /*private void addButton(String text, Runnable action) {
        Button button = new Button(new ContextThemeWrapper(getContext(), R.style.ProfileActionButton), null, 0);

        button.setText(text);

        button.setOnClickListener(v -> action.run());
        containerApplicationButtons.addView(button);

        // Bottom line
        containerApplicationButtons.addView(createDivider());
    }*/

    private void addButton(String text, int iconRes, Runnable action) {
        MaterialButton button = new MaterialButton(new ContextThemeWrapper(getContext(), R.style.ProfileActionButton), null, 0);
        button.setText(text);

        // If button has icon, display it
        if (iconRes != 0) {
            button.setIcon(ContextCompat.getDrawable(getContext(), iconRes));
            button.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
            button.setIconPadding(16);
        }

        button.setOnClickListener(v -> action.run());
        containerApplicationButtons.addView(button);
        containerApplicationButtons.addView(createDivider());
    }

    private void openDriverHistoryFragment() {
        DriverHistoryFragment historyFragment = new DriverHistoryFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, historyFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openDriverRegistrationFragment() {
        DriverRegistrationFragment fragment = new DriverRegistrationFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.flFragment, fragment).addToBackStack(null).commit();
    }
}