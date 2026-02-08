package com.example.clickanddrive;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // One bottom navigation bar will exist for all roles
        // However based on who is logged in, a different menu will be displayed in the nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Fragments that will be displayed
        Fragment homeFragment = new HomeFragment();
        Fragment profileFragment = new ProfileFragment();
        Fragment newRideFragment = new NewRideFragment();
        // add more based on role ...


        refreshBottomNavigation();


        setCurrentFragment(homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                setCurrentFragment(homeFragment);
                return true;
            } else if (id == R.id.profile) {
                setCurrentFragment(profileFragment);
                return true;
            } else if (id == R.id.new_ride) {
                setCurrentFragment(newRideFragment);
                return true;
            }
            // ...
            // will add other cases when other role fragments are created

            return false;
        });
    }

    public void refreshBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.getMenu().clear();

        switch(SessionManager.currentUserType) {
            case SessionManager.GUEST:
            case SessionManager.USER:
                bottomNavigationView.inflateMenu(R.menu.bottom_nav_user);
                break;
            case SessionManager.DRIVER:
                bottomNavigationView.inflateMenu(R.menu.bottom_nav_driver);
                break;
            case SessionManager.ADMIN:
                bottomNavigationView.inflateMenu(R.menu.bottom_nav_admin);
                break;
        }
    }

    public void logoutAndGoToLogin() {
        SessionManager.logout();

        if (!isFinishing() && !isDestroyed()) {
            getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            if (bottomNavigationView != null) {
                bottomNavigationView.getMenu().clear();
                bottomNavigationView.inflateMenu(R.menu.bottom_nav_user); // guest menu
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, new LoginFragment())
                    .commitAllowingStateLoss();
        }
    }

    public void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }

}