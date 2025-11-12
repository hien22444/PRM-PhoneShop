package com.example.phoneshop; // Thay đổi theo package của bạn

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI; // <<< CẦN IMPORT LỚP NÀY
import com.google.android.material.bottomnavigation.BottomNavigationView; // <<< CẦN IMPORT LỚP NÀY
import com.example.phoneshop.R; // Đảm bảo R được import đúng

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Đảm bảo layout này có NavHostFragment và BottomNavigationView

        // 1. Lấy NavController từ NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // 2. TÌM BOTTOM NAVIGATION VIEW (ID: bottom_nav_view trong activity_main.xml)
            BottomNavigationView navView = findViewById(R.id.bottom_nav_view);

            if (navView != null && navController != null) {
                // 3. KẾT NỐI: Liên kết BottomNavigationView với NavController
                // Dòng code này làm cho Bottom Bar hoạt động
                NavigationUI.setupWithNavController(navView, navController);
            }
        }

        // Lưu ý: Nếu bạn đã đặt Start Destination trong nav_graph.xml là Dashboard,
        // thì dòng navController.navigate(R.id.adminDashboardFragment); không cần thiết.
    }
}