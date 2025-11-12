package com.example.phoneshop.feature_admin_main;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.phoneshop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Lấy NavHostFragment và NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.admin_nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Tìm BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_navigation);

        // Liên kết BottomNavigationView với NavController
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}