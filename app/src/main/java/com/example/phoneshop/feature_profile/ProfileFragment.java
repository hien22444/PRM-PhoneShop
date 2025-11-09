package com.example.phoneshop.feature_profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {

    private NavController navController;
    private SharedPreferences sharedPreferences;

    // Views
    private TextView tvUserName;
    private TextView tvUserEmail;
    private MaterialCardView cardEditProfile;
    private MaterialCardView cardOrderHistory;
    private MaterialCardView cardSettings;
    private MaterialCardView cardSupport;
    private MaterialButton btnLogout;
    private MaterialButton btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        sharedPreferences = requireActivity().getSharedPreferences("PhoneShopPrefs", Context.MODE_PRIVATE);

        // Bind views
        bindViews(view);

        // Setup listeners
        setupListeners();

        // Update UI based on login status
        updateUI();
    }

    private void bindViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        cardEditProfile = view.findViewById(R.id.cardEditProfile);
        cardOrderHistory = view.findViewById(R.id.cardOrderHistory);
        cardSettings = view.findViewById(R.id.cardSettings);
        cardSupport = view.findViewById(R.id.cardSupport);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogin = view.findViewById(R.id.btnLogin);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_loginFragment);
        });

        btnLogout.setOnClickListener(v -> {
            // Clear login data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Clear cart data as well
            SharedPreferences cartPrefs = requireActivity().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor cartEditor = cartPrefs.edit();
            cartEditor.clear();
            cartEditor.apply();

            Toast.makeText(getContext(), "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
            updateUI();
        });

        cardEditProfile.setOnClickListener(v -> {
            if (isLoggedIn()) {
                navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
            } else {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để chỉnh sửa thông tin", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_profileFragment_to_loginFragment);
            }
        });

        cardOrderHistory.setOnClickListener(v -> {
            if (isLoggedIn()) {
                navController.navigate(R.id.action_profileFragment_to_orderHistoryFragment);
            } else {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_profileFragment_to_loginFragment);
            }
        });

        cardSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        cardSupport.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI() {
        boolean isLoggedIn = isLoggedIn();
        
        if (isLoggedIn) {
            // Try to get full_name first, if not available, use username
            String fullName = sharedPreferences.getString("full_name", "");
            if (fullName.isEmpty()) {
                fullName = sharedPreferences.getString("username", "User");
            }
            String email = sharedPreferences.getString("email", "user@example.com");
            
            tvUserName.setText(fullName);
            tvUserEmail.setText(email);
            tvUserName.setVisibility(View.VISIBLE);
            tvUserEmail.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            cardEditProfile.setVisibility(View.VISIBLE);
        } else {
            tvUserName.setVisibility(View.GONE);
            tvUserEmail.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            cardEditProfile.setVisibility(View.GONE);
        }
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(); // Update UI when returning to this fragment
    }
}
