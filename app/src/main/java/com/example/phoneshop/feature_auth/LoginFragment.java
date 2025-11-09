package com.example.phoneshop.feature_auth;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.features.feature_cart.CartViewModel;

import com.example.phoneshop.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    private AuthViewModel viewModel;
    private NavController navController;
    private SharedPreferences sharedPreferences;

    // Views
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnSignIn;
    private MaterialButton btnRegister;
    private MaterialButton btnForgotPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel and NavController
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        viewModel.setApplicationContext(requireContext());
        navController = Navigation.findNavController(view);
        sharedPreferences = requireActivity().getSharedPreferences("PhoneShopPrefs", Context.MODE_PRIVATE);

        // Bind views
        bindViews(view);

        // Setup listeners
        setupListeners();

        // Observe ViewModel
        observeViewModel();
    }

    private void bindViews(View view) {
        tilUsername = view.findViewById(R.id.tilUsername);
        tilPassword = view.findViewById(R.id.tilPassword);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnForgotPassword = view.findViewById(R.id.btnForgotPassword);
    }

    private void setupListeners() {
        btnSignIn.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(username, password)) {
                viewModel.login(username, password);
            }
        });

        btnRegister.setOnClickListener(v -> {
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });

        btnForgotPassword.setOnClickListener(v -> {
            navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });
    }

    private boolean validateInput(String username, String password) {
        boolean isValid = true;

        if (username.isEmpty()) {
            tilUsername.setError("Vui lòng nhập tên đăng nhập");
            isValid = false;
        } else {
            tilUsername.setError(null);
        }

        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        return isValid;
    }

    private void observeViewModel() {
        viewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    // Save login status and user information
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_logged_in", true);
                    editor.putString("user_id", result.getUserId());
                    editor.putString("username", result.getUsername());
                    if (result.getFullName() != null) {
                        editor.putString("full_name", result.getFullName());
                    }
                    if (result.getEmail() != null) {
                        editor.putString("email", result.getEmail());
                    }
                    editor.apply();

                    // Check if there are items in cart and show notification
                    checkCartItems();

                    // Navigate to home
                    navController.navigate(R.id.action_loginFragment_to_homeFragment);
                } else {
                    Toast.makeText(getContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnSignIn.setEnabled(!isLoading);
            btnSignIn.setText(isLoading ? "Đang đăng nhập..." : "Sign In");
        });
    }

    private void checkCartItems() {
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                Toast.makeText(getContext(),
                        "Bạn đang có " + cartItems.size() + " đơn hàng trong giỏ hàng",
                        Toast.LENGTH_LONG).show();
            }
        });
        cartViewModel.loadCartItems();
    }
}
