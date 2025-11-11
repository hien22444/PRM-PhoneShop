package com.example.phoneshop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigation;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize theme manager before setting content view
        com.example.phoneshop.service.ThemeManager.getInstance().initialize(this);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("PhoneShopPrefs", MODE_PRIVATE);
        
        setupNavigation();
        checkLoginStatus();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            NavigationUI.setupWithNavController(bottomNavigation, navController);
            
            // Custom click listener to reset to main page of each tab
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                
                // Always navigate to the main page of each tab
                if (itemId == R.id.homeFragment) {
                    navController.navigate(R.id.homeFragment);
                    return true;
                } else if (itemId == R.id.cartFragment) {
                    navController.navigate(R.id.cartFragment);
                    return true;
                } else if (itemId == R.id.profileFragment) {
                    navController.navigate(R.id.profileFragment);
                    return true;
                } else if (itemId == R.id.orderHistoryFragment) {
                    navController.navigate(R.id.orderHistoryFragment);
                    return true;
                }
                
                return false;
            });
            
            // Hide bottom navigation on certain fragments
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destinationId = destination.getId();
                if (destinationId == R.id.loginFragment || 
                    destinationId == R.id.registerFragment ||
                    destinationId == R.id.forgotPasswordFragment ||
                    destinationId == R.id.checkoutFragment ||
                    destinationId == R.id.paymentWebViewFragment ||
                    destinationId == R.id.orderSuccessFragment ||
                    destinationId == R.id.editProfileFragment) {
                    bottomNavigation.setVisibility(View.GONE);
                } else {
                    bottomNavigation.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void checkLoginStatus() {
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (!isLoggedIn) {
            // If not logged in, start with home but show login when needed
            // The home fragment will handle login requirement for cart/profile actions
        }
    }

    public void showBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(View.VISIBLE);
        }
    }

    public void hideBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(View.GONE);
        }
    }
}