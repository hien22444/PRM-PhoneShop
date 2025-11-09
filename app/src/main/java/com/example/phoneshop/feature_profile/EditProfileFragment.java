package com.example.phoneshop.feature_profile;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditProfileFragment extends Fragment {

    private NavController navController;
    private SharedPreferences sharedPreferences;

    // Views
    private MaterialToolbar toolbar;
    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputLayout tilAddress;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etAddress;
    private MaterialButton btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        sharedPreferences = requireActivity().getSharedPreferences("PhoneShopPrefs", Context.MODE_PRIVATE);

        // Bind views
        bindViews(view);

        // Setup toolbar
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        // Load current user data
        loadUserData();

        // Setup save button
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        tilFullName = view.findViewById(R.id.tilFullName);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPhone = view.findViewById(R.id.tilPhone);
        tilAddress = view.findViewById(R.id.tilAddress);
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);
        btnSave = view.findViewById(R.id.btnSave);
    }

    private void loadUserData() {
        // Load current user information from SharedPreferences
        String fullName = sharedPreferences.getString("full_name", "");
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");
        String address = sharedPreferences.getString("address", "");

        // If full_name is empty, try to get from username
        if (fullName.isEmpty()) {
            fullName = sharedPreferences.getString("username", "");
        }

        // Set values to EditText
        etFullName.setText(fullName);
        etEmail.setText(email);
        etPhone.setText(phone);
        etAddress.setText(address);
    }

    private void saveProfile() {
        // Get input values
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Validate input
        if (!validateInput(fullName, email, phone)) {
            return;
        }

        // Check if user is logged in
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (!isLoggedIn) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để chỉnh sửa thông tin", Toast.LENGTH_SHORT).show();
            navController.navigateUp();
            return;
        }

        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("full_name", fullName);
        editor.putString("username", fullName); // Also update username for backward compatibility
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("address", address);
        editor.apply();

        // Show success message
        Toast.makeText(getContext(), "Đã lưu thông tin thành công", Toast.LENGTH_SHORT).show();

        // Navigate back
        navController.navigateUp();
    }

    private boolean validateInput(String fullName, String email, String phone) {
        boolean isValid = true;

        // Validate full name
        if (fullName.isEmpty()) {
            tilFullName.setError("Vui lòng nhập họ tên");
            isValid = false;
        } else {
            tilFullName.setError(null);
        }

        // Validate email
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không hợp lệ");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        // Validate phone (optional but if provided, should be valid)
        if (!phone.isEmpty() && !android.util.Patterns.PHONE.matcher(phone).matches()) {
            tilPhone.setError("Số điện thoại không hợp lệ");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }

        return isValid;
    }
}

