package com.example.phoneshop.features.feature_admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.phoneshop.R;
import com.example.phoneshop.features.feature_admin.viewmodel.AdminViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Admin Login Fragment
 * Handles admin authentication with username/password
 */
public class AdminLoginFragment extends Fragment {

    private static final String TAG = "AdminLoginFragment";
    
    // Views
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private CircularProgressIndicator progressIndicator;
    
    // ViewModel
    private AdminViewModel adminViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupViewModel();
        setupListeners();
        observeViewModel();
        
        // Pre-fill admin credentials for demo
        etUsername.setText("admin");
        etPassword.setText("admin");
    }

    private void initViews(View view) {
        // Note: Using standard IDs from fragment_admin_login.xml
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        progressIndicator = view.findViewById(R.id.progressIndicator);
    }

    private void setupViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            if (validateInput(username, password)) {
                performLogin(username, password);
            }
        });
        
        // Clear any error messages when user starts typing
        // Note: Error handling will be done via Toast messages since we don't have TextInputLayout references
    }

    private void observeViewModel() {
        // Observe login result
        adminViewModel.getAdminLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    onLoginSuccess();
                } else {
                    onLoginError(result.getMessage());
                }
            }
        });
        
        // Observe loading state
        adminViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            updateLoadingState(isLoading);
        });
        
        // Observe error messages
        adminViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput(String username, String password) {
        // Validate username
        if (username.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên đăng nhập admin", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Validate password
        if (password.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập mật khẩu admin", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 3) {
            Toast.makeText(getContext(), "Mật khẩu phải có ít nhất 3 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private void performLogin(String username, String password) {
        // Call admin login API
        adminViewModel.loginAdmin(username, password);
    }

    private void updateLoadingState(boolean isLoading) {
        if (isLoading) {
            btnLogin.setEnabled(false);
            btnLogin.setText("Đang đăng nhập...");
            progressIndicator.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setEnabled(true);
            btnLogin.setText("Đăng nhập Admin");
            progressIndicator.setVisibility(View.GONE);
        }
    }

    private void onLoginSuccess() {
        Toast.makeText(getContext(), "Đăng nhập admin thành công!", Toast.LENGTH_SHORT).show();
        
        // Notify parent activity
        if (getActivity() instanceof AdminActivity) {
            ((AdminActivity) getActivity()).onAdminLoginSuccess();
        }
    }

    private void onLoginError(String errorMessage) {
        String message = errorMessage != null ? errorMessage : "Đăng nhập thất bại";
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        
        // Clear password field for security
        etPassword.setText("");
        etPassword.requestFocus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references to prevent memory leaks
//        tilUsername = null;
//        tilPassword = null;
        etUsername = null;
        etPassword = null;
        btnLogin = null;
        progressIndicator = null;
    }
}
