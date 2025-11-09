package com.example.phoneshop.feature_auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordFragment extends Fragment {

    private NavController navController;

    // Views
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private MaterialButton btnSendReset;
    private MaterialButton btnBackToLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Bind views
        bindViews(view);

        // Setup listeners
        setupListeners();
    }

    private void bindViews(View view) {
        tilEmail = view.findViewById(R.id.tilEmail);
        etEmail = view.findViewById(R.id.etEmail);
        btnSendReset = view.findViewById(R.id.btnSendReset);
        btnBackToLogin = view.findViewById(R.id.btnBackToLogin);
    }

    private void setupListeners() {
        btnSendReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (validateEmail(email)) {
                // Simulate sending reset email
                Toast.makeText(getContext(), "Đã gửi email khôi phục mật khẩu đến " + email, Toast.LENGTH_LONG).show();
                navController.navigateUp();
            }
        });

        btnBackToLogin.setOnClickListener(v -> {
            navController.navigateUp();
        });
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không hợp lệ");
            return false;
        } else {
            tilEmail.setError(null);
            return true;
        }
    }
}
