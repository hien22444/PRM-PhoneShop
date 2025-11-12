package com.example.phoneshop.feature_auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.R;
import com.example.phoneshop.features.feature_cart.CartViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 1001;
    private static final String TAG = "LoginFragment";

    private AuthViewModel viewModel;
    private NavController navController;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient googleSignInClient;

    // Views
    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnSignIn, btnRegister, btnForgotPassword, btnGoogleSignIn;

    @Nullable
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup ViewModel, Navigation và SharedPrefs
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        viewModel.setApplicationContext(requireContext());
        navController = Navigation.findNavController(view);
        sharedPreferences = requireActivity().getSharedPreferences("PhoneShopPrefs", Context.MODE_PRIVATE);

        bindViews(view);
        setupGoogleSignIn();
        setupListeners();
        observeViewModel();
    }

    private void bindViews(android.view.View view) {
        tilUsername = view.findViewById(R.id.tilUsername);
        tilPassword = view.findViewById(R.id.tilPassword);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnForgotPassword = view.findViewById(R.id.btnForgotPassword);
        btnGoogleSignIn = view.findViewById(R.id.btnGoogleSignIn);
    }

    private void setupListeners() {
        // Login thường
        btnSignIn.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (validateInput(username, password)) {
                viewModel.login(username, password);
            }
        });

        // Chuyển màn khác
        btnRegister.setOnClickListener(v -> navController.navigate(R.id.action_loginFragment_to_registerFragment));
        btnForgotPassword.setOnClickListener(v -> navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment));

        // Login Google
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
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
        } else if (password.length() < 6 && !("admin".equals(username) && "admin".equals(password))) {
            tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        return isValid;
    }

    private void observeViewModel() {
        viewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.isSuccess()) {
                if (result.isAdmin()) {
                    saveAdminInfo(result.getUserId(), result.getUsername(), result.getFullName(), result.getEmail());
                    navigateToAdminPanel();
                } else {
                    saveUserInfo(result.getUserId(), result.getUsername(), result.getFullName(), result.getEmail());
                    checkCartItems();
                    navController.navigate(R.id.action_loginFragment_to_homeFragment);
                }
            } else {
                Toast.makeText(getContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnSignIn.setEnabled(!isLoading);
            btnSignIn.setText(isLoading ? "Đang đăng nhập..." : "Sign In");
        });
    }

    // ------------------ Lưu thông tin người dùng ------------------
    private void saveUserInfo(String userId, String username, String fullName, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_id", userId);
        editor.putString("username", username);
        editor.putString("full_name", fullName);
        editor.putString("email", email);
        editor.apply();
    }

    private void saveAdminInfo(String userId, String username, String fullName, String email) {
        SharedPreferences.Editor editor = requireActivity()
                .getSharedPreferences("admin_prefs", Context.MODE_PRIVATE)
                .edit();
        editor.putBoolean("admin_logged_in", true);
        editor.putString("admin_id", userId);
        editor.putString("admin_username", username);
        editor.putString("admin_fullname", fullName);
        editor.putString("admin_email", email);
        editor.apply();
    }

    // ------------------ Kiểm tra giỏ hàng ------------------
    private void checkCartItems() {
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                Toast.makeText(getContext(),
                        "Bạn đang có " + cartItems.size() + " sản phẩm trong giỏ hàng",
                        Toast.LENGTH_LONG).show();
            }
        });
        cartViewModel.loadCartItems();
    }

    // ------------------ GOOGLE SIGN-IN ------------------
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    saveUserInfo(account.getId(), account.getDisplayName(), account.getDisplayName(), account.getEmail());
                    checkCartItems();
                    navController.navigate(R.id.action_loginFragment_to_homeFragment);
                }
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(getContext(), "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ------------------ ADMIN PANEL ------------------
    private void navigateToAdminPanel() {
        try {
            Intent intent = new Intent(getActivity(), com.example.phoneshop.features.feature_admin.AdminActivity.class);
            startActivity(intent);
            requireActivity().finish();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi mở Admin Panel: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ------------------ LOGOUT ------------------
    public void logout() {
        // Xóa session user thường
        sharedPreferences.edit().clear().apply();

        // Xóa session admin (nếu có)
        requireActivity()
                .getSharedPreferences("admin_prefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // Sign out Google
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }

        // Quay lại màn đăng nhập
        navController.navigate(R.id.loginFragment);
        Toast.makeText(getContext(), "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }
}
