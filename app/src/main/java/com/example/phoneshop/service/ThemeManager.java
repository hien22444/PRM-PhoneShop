package com.example.phoneshop.service;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Service để quản lý theme của ứng dụng
 */
public class ThemeManager {
    
    private static ThemeManager instance;
    private static final String PREF_NAME = "PhoneShopPrefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    
    private SharedPreferences sharedPreferences;
    
    private ThemeManager() {}
    
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        // Apply saved theme on app start
        applySavedTheme();
    }
    
    /**
     * Apply theme based on saved preference
     */
    public void applySavedTheme() {
        if (sharedPreferences != null) {
            boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
    
    /**
     * Toggle between dark and light mode
     */
    public void toggleTheme() {
        if (sharedPreferences != null) {
            boolean currentMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
            setDarkMode(!currentMode);
        }
    }
    
    /**
     * Set dark mode on/off
     */
    public void setDarkMode(boolean isDarkMode) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply();
            
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
    
    /**
     * Check if dark mode is enabled
     */
    public boolean isDarkMode() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        }
        return false;
    }
}
