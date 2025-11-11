package com.example.phoneshop.feature_auth;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.data.local.UserManager;
import com.example.phoneshop.data.model.AuthRequest;
import com.example.phoneshop.data.model.AuthResponse;
import com.example.phoneshop.data.model.User;
import com.example.phoneshop.data.remote.ApiService;
import com.example.phoneshop.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    private final ApiService apiService;
    private final MutableLiveData<AuthResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<AuthResult> registerResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    private Context applicationContext;

    public AuthViewModel() {
        apiService = RetrofitClient.getInstance().getApiService();
        isLoading.setValue(false);
    }
    
    public void setApplicationContext(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    public LiveData<AuthResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<AuthResult> getRegisterResult() {
        return registerResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void login(String username, String password) {
        isLoading.setValue(true);

        // Tạo request cho API
        AuthRequest request = AuthRequest.forLogin(username, password);
        
        // Gọi API đăng nhập
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    if (authResponse.isSuccess() && authResponse.getUser() != null) {
                        // Đăng nhập thành công
                        User user = authResponse.getUser();
                        AuthResult result = new AuthResult(
                            true, 
                            user.getId(), 
                            user.getUsername(),
                            null
                        );
                        result.setFullName(user.getFullName());
                        result.setEmail(user.getEmail());
                        loginResult.setValue(result);
                    } else {
                        // Đăng nhập thất bại với message từ server
                        AuthResult result = new AuthResult(false, null, null, authResponse.getMessage());
                        loginResult.setValue(result);
                    }
                } else {
                    // Lỗi HTTP
                    String errorMessage = "Đăng nhập thất bại. Vui lòng thử lại.";
                    if (response.code() == 401) {
                        errorMessage = "Tên đăng nhập hoặc mật khẩu không đúng.";
                    } else if (response.code() == 400) {
                        errorMessage = "Thông tin đăng nhập không hợp lệ.";
                    }
                    
                    AuthResult result = new AuthResult(false, null, null, errorMessage);
                    loginResult.setValue(result);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                isLoading.setValue(false);
                AuthResult result = new AuthResult(false, null, null, "Lỗi kết nối. Vui lòng kiểm tra internet.");
                loginResult.setValue(result);
            }
        });
    }

    public void register(String fullName, String email, String username, String password) {
        isLoading.setValue(true);

        // Tạo request cho API
        AuthRequest request = AuthRequest.forRegistration(fullName, email, username, password);
        
        // Gọi API đăng ký
        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    if (authResponse.isSuccess()) {
                        // Đăng ký thành công
                        AuthResult result = new AuthResult(true, null, null, null);
                        registerResult.setValue(result);
                    } else {
                        // Đăng ký thất bại với message từ server
                        AuthResult result = new AuthResult(false, null, null, authResponse.getMessage());
                        registerResult.setValue(result);
                    }
                } else {
                    // Lỗi HTTP
                    String errorMessage = "Đăng ký thất bại. Vui lòng thử lại.";
                    if (response.code() == 409) {
                        errorMessage = "Tên đăng nhập hoặc email đã tồn tại.";
                    } else if (response.code() == 400) {
                        errorMessage = "Thông tin đăng ký không hợp lệ.";
                    }
                    
                    AuthResult result = new AuthResult(false, null, null, errorMessage);
                    registerResult.setValue(result);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                isLoading.setValue(false);
                AuthResult result = new AuthResult(false, null, null, "Lỗi kết nối. Vui lòng kiểm tra internet.");
                registerResult.setValue(result);
            }
        });
    }

    // Result class for authentication operations
    public static class AuthResult {
        private final boolean success;
        private final String userId;
        private final String username;
        private String fullName;
        private String email;
        private final String errorMessage;

        public AuthResult(boolean success, String userId, String username, String errorMessage) {
            this.success = success;
            this.userId = userId;
            this.username = username;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
