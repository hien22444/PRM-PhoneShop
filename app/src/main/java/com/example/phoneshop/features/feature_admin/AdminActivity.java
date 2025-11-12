package com.example.phoneshop.features.feature_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.phoneshop.MainActivity;
import com.example.phoneshop.R;
import com.google.android.material.navigation.NavigationView;

/**
 * Admin Activity - Main container for admin panel
 * Handles navigation between admin fragments using drawer navigation
 */
public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "AdminActivity";
    private static final String ADMIN_PREFS = "admin_prefs";
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SharedPreferences adminPrefs;
    
    // Current fragment tracking
    private Fragment currentFragment;
    private String currentFragmentTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_admin);
            
            adminPrefs = getSharedPreferences(ADMIN_PREFS, MODE_PRIVATE);
            
            initViews();
            setupToolbar();
            setupNavigation();
            checkAdminLogin();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo Admin Panel: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setTitle("PhoneShop Admin");
        }
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(this);
        
        // Set header info
        View headerView = navigationView.getHeaderView(0);
        // You can customize header view here if needed
    }

    private void checkAdminLogin() {
        try {
            boolean isAdminLoggedIn = adminPrefs.getBoolean("admin_logged_in", false);
            
            if (!isAdminLoggedIn) {
                // Show admin login fragment
                showFragment(new AdminLoginFragment(), "AdminLogin", false);
            } else {
                // Admin is logged in, show dashboard directly
                showFragment(new AdminDashboardFragment(), "AdminDashboard", false);
                navigationView.setCheckedItem(R.id.nav_dashboard);
                
                // Update toolbar title
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Admin Dashboard");
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi load fragment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Fallback to login fragment
            try {
                showFragment(new AdminLoginFragment(), "AdminLogin", false);
            } catch (Exception fallbackError) {
                finish();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        // Check if admin is logged in for protected routes
        if (!isAdminLoggedIn()) {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
            // Show login fragment
            showFragment(new AdminLoginFragment(), "AdminLogin", false);
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }
        
        Fragment fragment = null;
        String tag = "";
        
        if (itemId == R.id.nav_dashboard) {
            fragment = new AdminDashboardFragment();
            tag = "AdminDashboard";
        } else if (itemId == R.id.nav_users) {
            fragment = new AdminUserManagementFragment();
            tag = "AdminUsers";
        } else if (itemId == R.id.nav_orders) {
            fragment = new AdminOrderManagementFragment();
            tag = "AdminOrders";
        } else if (itemId == R.id.nav_products) {
            fragment = new AdminProductManagementFragment();
            tag = "AdminProducts";
        } else if (itemId == R.id.nav_statistics) {
            fragment = new AdminStatisticsFragment();
            tag = "AdminStatistics";
        } else if (itemId == R.id.nav_logout) {
            performLogout();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.nav_back_to_app) {
            // Navigate back to main app
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        
        if (fragment != null) {
            showFragment(fragment, tag, true);
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(Fragment fragment, String tag, boolean addToBackStack) {
        try {
            if (tag.equals(currentFragmentTag)) {
                return; // Same fragment, no need to replace
            }
            
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            
            // Add fade animation
            transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            );
            
            transaction.replace(R.id.admin_fragment_container, fragment, tag);
            
            if (addToBackStack) {
                transaction.addToBackStack(tag);
            }
            
            transaction.commit();
            
            currentFragment = fragment;
            currentFragmentTag = tag;
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi hiển thị fragment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAdminLoggedIn() {
        return adminPrefs.getBoolean("admin_logged_in", false);
    }

    private void performLogout() {
        // Clear admin session
        SharedPreferences.Editor editor = adminPrefs.edit();
        editor.clear();
        editor.apply();
        
        // Show login fragment
        showFragment(new AdminLoginFragment(), "AdminLogin", false);
        
        // Clear back stack
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        Toast.makeText(this, "Đã đăng xuất admin", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            } else {
                // If no fragments in back stack, go back to main app
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * Called by AdminLoginFragment when login is successful
     */
    public void onAdminLoginSuccess() {
        // Save login state
        SharedPreferences.Editor editor = adminPrefs.edit();
        editor.putBoolean("admin_logged_in", true);
        editor.putString("admin_username", "admin");
        editor.putLong("admin_login_time", System.currentTimeMillis());
        editor.apply();
        
        // Show dashboard
        showFragment(new AdminDashboardFragment(), "AdminDashboard", false);
        navigationView.setCheckedItem(R.id.nav_dashboard);
        
        Toast.makeText(this, "Đăng nhập admin thành công", Toast.LENGTH_SHORT).show();
    }

    /**
     * Get current admin session info
     */
    public String getAdminUsername() {
        return adminPrefs.getString("admin_username", "admin");
    }

    public long getAdminLoginTime() {
        return adminPrefs.getLong("admin_login_time", 0);
    }
}
