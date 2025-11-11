package com.example.phoneshop.feature_profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private NavController navController;
    private SharedPreferences sharedPreferences;
    
    // Views
    private MaterialToolbar toolbar;
    private SwitchMaterial switchDarkMode;
    private SwitchMaterial switchNotifications;
    private SwitchMaterial switchAutoUpdate;
    private MaterialCardView cardClearCache;
    private MaterialCardView cardResetSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        sharedPreferences = requireActivity().getSharedPreferences("PhoneShopPrefs", Context.MODE_PRIVATE);

        // Map views
        toolbar = view.findViewById(R.id.toolbar);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        switchNotifications = view.findViewById(R.id.switchNotifications);
        switchAutoUpdate = view.findViewById(R.id.switchAutoUpdate);
        cardClearCache = view.findViewById(R.id.cardClearCache);
        cardResetSettings = view.findViewById(R.id.cardResetSettings);

        setupToolbar();
        setupSwitches();
        setupClickListeners();
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
    }

    private void setupSwitches() {
        // Load saved preferences
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        boolean autoUpdateEnabled = sharedPreferences.getBoolean("auto_update_enabled", true);

        // Set switch states
        switchDarkMode.setChecked(isDarkMode);
        switchNotifications.setChecked(notificationsEnabled);
        switchAutoUpdate.setChecked(autoUpdateEnabled);

        // Dark mode switch listener
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save preference
                sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
                
                // Apply theme
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(getContext(), "Đã chuyển sang chế độ tối", Toast.LENGTH_SHORT).show();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(getContext(), "Đã chuyển sang chế độ sáng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Notifications switch listener
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
                String message = isChecked ? "Đã bật thông báo" : "Đã tắt thông báo";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Auto update switch listener
        switchAutoUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("auto_update_enabled", isChecked).apply();
                String message = isChecked ? "Đã bật tự động cập nhật" : "Đã tắt tự động cập nhật";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        // Clear cache
        cardClearCache.setOnClickListener(v -> {
            // Simulate clearing cache
            Toast.makeText(getContext(), "Đang xóa bộ nhớ đệm...", Toast.LENGTH_SHORT).show();
            
            // Simulate delay
            v.postDelayed(() -> {
                Toast.makeText(getContext(), "Đã xóa bộ nhớ đệm thành công!", Toast.LENGTH_SHORT).show();
            }, 1500);
        });

        // Reset settings
        cardResetSettings.setOnClickListener(v -> {
            // Show confirmation dialog (simplified with toast for now)
            Toast.makeText(getContext(), "Đang khôi phục cài đặt mặc định...", Toast.LENGTH_SHORT).show();
            
            // Reset all preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode", false);
            editor.putBoolean("notifications_enabled", true);
            editor.putBoolean("auto_update_enabled", true);
            editor.apply();
            
            // Reset switches
            switchDarkMode.setChecked(false);
            switchNotifications.setChecked(true);
            switchAutoUpdate.setChecked(true);
            
            // Apply light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            
            v.postDelayed(() -> {
                Toast.makeText(getContext(), "Đã khôi phục cài đặt mặc định!", Toast.LENGTH_SHORT).show();
            }, 1500);
        });
    }
}
