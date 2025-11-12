package com.example.phoneshop.features.feature_admin.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.model.User;
import com.example.phoneshop.data.remote.ApiService;
import com.example.phoneshop.data.remote.RetrofitClient;
import com.example.phoneshop.features.feature_admin.model.AdminLoginResponse;
import com.example.phoneshop.features.feature_admin.model.AdminStatsResponse;
import com.example.phoneshop.features.feature_admin.model.AdminResponse;
import com.example.phoneshop.features.feature_admin.model.AdminRevenueResponse;
import com.example.phoneshop.features.feature_admin.model.AdminOrderStatsResponse;
import com.example.phoneshop.features.feature_admin.model.AdminUserStatsResponse;
import com.example.phoneshop.features.feature_admin.model.AdminProductStatsResponse;
import com.example.phoneshop.features.feature_admin.model.AdminOrdersResponse;
import com.example.phoneshop.features.feature_admin.model.AdminCustomerResponse;
import com.example.phoneshop.features.feature_admin.model.OrderStatusUpdateRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdminViewModel - Handles all admin-related API calls and data management
 */
public class AdminViewModel extends ViewModel {

    private static final String TAG = "AdminViewModel";
    
    private ApiService apiService;
    
    // Loading states
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // Admin authentication
    private MutableLiveData<AdminLoginResponse> adminLoginResult = new MutableLiveData<>();
    
    // Dashboard statistics
    private MutableLiveData<AdminStatsResponse> dashboardStats = new MutableLiveData<>();
    private MutableLiveData<AdminRevenueResponse> revenueStats = new MutableLiveData<>();
    private MutableLiveData<AdminOrderStatsResponse> orderStats = new MutableLiveData<>();
    private MutableLiveData<AdminUserStatsResponse> userStats = new MutableLiveData<>();
    private MutableLiveData<AdminProductStatsResponse> productStats = new MutableLiveData<>();
    
    // Order management
    private MutableLiveData<AdminOrdersResponse> adminOrders = new MutableLiveData<>();
    private MutableLiveData<AdminCustomerResponse> customerDetails = new MutableLiveData<>();
    private MutableLiveData<Boolean> orderUpdateResult = new MutableLiveData<>();
    
    // User management
    private MutableLiveData<List<User>> usersList = new MutableLiveData<>();
    private MutableLiveData<User> selectedUser = new MutableLiveData<>();
    
    // Order management
    private MutableLiveData<List<Order>> ordersList = new MutableLiveData<>();
    private MutableLiveData<Order> selectedOrder = new MutableLiveData<>();
    
    // Product management
    private MutableLiveData<List<Product>> productsList = new MutableLiveData<>();
    private MutableLiveData<Product> selectedProduct = new MutableLiveData<>();
    
    // Operation results
    private MutableLiveData<AdminResponse> operationResult = new MutableLiveData<>();

    public AdminViewModel() {
        apiService = RetrofitClient.getInstance().getApiService();
    }

    // ==================== GETTERS ====================
    
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<AdminLoginResponse> getAdminLoginResult() { return adminLoginResult; }
    public LiveData<AdminStatsResponse> getDashboardStats() { return dashboardStats; }
    public LiveData<AdminRevenueResponse> getRevenueStats() { return revenueStats; }
    public LiveData<AdminOrderStatsResponse> getOrderStats() { return orderStats; }
    public LiveData<AdminUserStatsResponse> getUserStats() { return userStats; }
    public LiveData<AdminProductStatsResponse> getProductStats() { return productStats; }
    public LiveData<AdminOrdersResponse> getAdminOrders() { return adminOrders; }
    public LiveData<AdminCustomerResponse> getCustomerDetails() { return customerDetails; }
    public LiveData<Boolean> getOrderUpdateResult() { return orderUpdateResult; }
    public LiveData<List<User>> getUsersList() { return usersList; }
    public LiveData<User> getSelectedUser() { return selectedUser; }
    public LiveData<List<Order>> getOrdersList() { return ordersList; }
    public LiveData<Order> getSelectedOrder() { return selectedOrder; }
    public LiveData<List<Product>> getProductsList() { return productsList; }
    public LiveData<Product> getSelectedProduct() { return selectedProduct; }
    public LiveData<AdminResponse> getOperationResult() { return operationResult; }

    // ==================== ADMIN AUTHENTICATION ====================
    
    public void loginAdmin(String username, String password) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        Log.d(TAG, "Attempting admin login: " + username);
        
        // Create login request
        AdminLoginRequest request = new AdminLoginRequest(username, password);
        
        Call<AdminLoginResponse> call = apiService.loginAdmin(request);
        call.enqueue(new Callback<AdminLoginResponse>() {
            @Override
            public void onResponse(Call<AdminLoginResponse> call, Response<AdminLoginResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminLoginResponse result = response.body();
                    Log.d(TAG, "Admin login successful: " + result.isSuccess());
                    adminLoginResult.setValue(result);
                } else {
                    Log.e(TAG, "Admin login failed: " + response.code());
                    AdminLoginResponse errorResult = new AdminLoginResponse();
                    errorResult.setSuccess(false);
                    errorResult.setMessage("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
                    adminLoginResult.setValue(errorResult);
                }
            }

            @Override
            public void onFailure(Call<AdminLoginResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Admin login network error", t);
                
                AdminLoginResponse errorResult = new AdminLoginResponse();
                errorResult.setSuccess(false);
                errorResult.setMessage("Lỗi kết nối. Vui lòng thử lại.");
                adminLoginResult.setValue(errorResult);
            }
        });
    }

    // ==================== DASHBOARD STATISTICS ====================
    
    public void loadDashboardStats() {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        Log.d(TAG, "Loading dashboard statistics");
        
        Call<AdminStatsResponse> call = apiService.getAdminDashboardStats();
        call.enqueue(new Callback<AdminStatsResponse>() {
            @Override
            public void onResponse(Call<AdminStatsResponse> call, Response<AdminStatsResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminStatsResponse stats = response.body();
                    Log.d(TAG, "Dashboard stats loaded successfully");
                    dashboardStats.setValue(stats);
                } else {
                    Log.e(TAG, "Failed to load dashboard stats: " + response.code());
                    errorMessage.setValue("Không thể tải thống kê dashboard");
                }
            }

            @Override
            public void onFailure(Call<AdminStatsResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Dashboard stats network error", t);
                errorMessage.setValue("Lỗi kết nối khi tải thống kê");
            }
        });
    }

    // ==================== USER MANAGEMENT ====================
    
    public void loadUsers(int page, int size, String query) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        Log.d(TAG, "Loading users - page: " + page + ", size: " + size + ", query: " + query);
        
        Call<AdminResponse<User>> call = apiService.getAdminUsers(page, size, query);
        call.enqueue(new Callback<AdminResponse<User>>() {
            @Override
            public void onResponse(Call<AdminResponse<User>> call, Response<AdminResponse<User>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminResponse<User> result = response.body();
                    if (result.getContent() != null) {
                        Log.d(TAG, "Users loaded: " + result.getContent().size());
                        usersList.setValue(result.getContent());
                    }
                } else {
                    Log.e(TAG, "Failed to load users: " + response.code());
                    errorMessage.setValue("Không thể tải danh sách người dùng");
                }
            }

            @Override
            public void onFailure(Call<AdminResponse<User>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Users loading network error", t);
                errorMessage.setValue("Lỗi kết nối khi tải người dùng");
            }
        });
    }
    
    public void getUserDetail(String userId) {
        isLoading.setValue(true);
        
        Call<AdminResponse<User>> call = apiService.getAdminUserDetail(userId);
        call.enqueue(new Callback<AdminResponse<User>>() {
            @Override
            public void onResponse(Call<AdminResponse<User>> call, Response<AdminResponse<User>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminResponse<User> result = response.body();
                    selectedUser.setValue(result.getData());
                } else {
                    errorMessage.setValue("Không thể tải thông tin người dùng");
                }
            }

            @Override
            public void onFailure(Call<AdminResponse<User>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối khi tải thông tin người dùng");
            }
        });
    }
    
    public void deleteUser(String userId) {
        isLoading.setValue(true);
        
        Call<AdminResponse> call = apiService.deleteAdminUser(userId);
        call.enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    operationResult.setValue(response.body());
                } else {
                    AdminResponse errorResult = new AdminResponse();
                    errorResult.setSuccess(false);
                    errorResult.setMessage("Không thể xóa người dùng");
                    operationResult.setValue(errorResult);
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                isLoading.setValue(false);
                AdminResponse errorResult = new AdminResponse();
                errorResult.setSuccess(false);
                errorResult.setMessage("Lỗi kết nối khi xóa người dùng");
                operationResult.setValue(errorResult);
            }
        });
    }

    // ==================== ORDER MANAGEMENT ====================
    
    public void loadOrders(int page, int size, String status, String query) {
        // Delegate to the new loadAdminOrders method
        loadAdminOrders(page, size, status, query);
    }
    
    public void getOrderDetail(String orderId) {
        isLoading.setValue(true);
        
        Call<AdminResponse<Order>> call = apiService.getAdminOrderDetail(orderId);
        call.enqueue(new Callback<AdminResponse<Order>>() {
            @Override
            public void onResponse(Call<AdminResponse<Order>> call, Response<AdminResponse<Order>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminResponse<Order> result = response.body();
                    selectedOrder.setValue(result.getData());
                } else {
                    errorMessage.setValue("Không thể tải thông tin đơn hàng");
                }
            }

            @Override
            public void onFailure(Call<AdminResponse<Order>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối khi tải thông tin đơn hàng");
            }
        });
    }

    // ==================== PRODUCT MANAGEMENT ====================
    
    public void loadProducts(int page, int size, String query, String brand) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        Log.d(TAG, "Loading products - page: " + page + ", query: " + query + ", brand: " + brand);
        
        Call<AdminResponse<Product>> call = apiService.getAdminProducts(page, size, query, brand, null);
        call.enqueue(new Callback<AdminResponse<Product>>() {
            @Override
            public void onResponse(Call<AdminResponse<Product>> call, Response<AdminResponse<Product>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminResponse<Product> result = response.body();
                    if (result.getContent() != null) {
                        Log.d(TAG, "Products loaded: " + result.getContent().size());
                        productsList.setValue(result.getContent());
                    }
                } else {
                    Log.e(TAG, "Failed to load products: " + response.code());
                    errorMessage.setValue("Không thể tải danh sách sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<AdminResponse<Product>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Products loading network error", t);
                errorMessage.setValue("Lỗi kết nối khi tải sản phẩm");
            }
        });
    }
    
    public void createProduct(Product product) {
        isLoading.setValue(true);
        
        Call<Product> call = apiService.createAdminProduct(product);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminResponse successResult = new AdminResponse();
                    successResult.setSuccess(true);
                    successResult.setMessage("Tạo sản phẩm thành công");
                    operationResult.setValue(successResult);
                } else {
                    AdminResponse errorResult = new AdminResponse();
                    errorResult.setSuccess(false);
                    errorResult.setMessage("Không thể tạo sản phẩm");
                    operationResult.setValue(errorResult);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                isLoading.setValue(false);
                AdminResponse errorResult = new AdminResponse();
                errorResult.setSuccess(false);
                errorResult.setMessage("Lỗi kết nối khi tạo sản phẩm");
                operationResult.setValue(errorResult);
            }
        });
    }
    
    public void updateProduct(String productId, Product product) {
        isLoading.setValue(true);
        
        Call<Product> call = apiService.updateAdminProduct(productId, product);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminResponse successResult = new AdminResponse();
                    successResult.setSuccess(true);
                    successResult.setMessage("Cập nhật sản phẩm thành công");
                    operationResult.setValue(successResult);
                } else {
                    AdminResponse errorResult = new AdminResponse();
                    errorResult.setSuccess(false);
                    errorResult.setMessage("Không thể cập nhật sản phẩm");
                    operationResult.setValue(errorResult);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                isLoading.setValue(false);
                AdminResponse errorResult = new AdminResponse();
                errorResult.setSuccess(false);
                errorResult.setMessage("Lỗi kết nối khi cập nhật sản phẩm");
                operationResult.setValue(errorResult);
            }
        });
    }
    
    public void deleteProduct(String productId) {
        isLoading.setValue(true);
        
        Call<Void> call = apiService.deleteAdminProduct(productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful()) {
                    AdminResponse successResult = new AdminResponse();
                    successResult.setSuccess(true);
                    successResult.setMessage("Xóa sản phẩm thành công");
                    operationResult.setValue(successResult);
                } else {
                    AdminResponse errorResult = new AdminResponse();
                    errorResult.setSuccess(false);
                    errorResult.setMessage("Không thể xóa sản phẩm");
                    operationResult.setValue(errorResult);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                AdminResponse errorResult = new AdminResponse();
                errorResult.setSuccess(false);
                errorResult.setMessage("Lỗi kết nối khi xóa sản phẩm");
                operationResult.setValue(errorResult);
            }
        });
    }

    // ==================== HELPER CLASSES ====================
    
    public static class AdminLoginRequest {
        private String username;
        private String password;
        
        public AdminLoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class OrderStatusRequest {
        private String status;
        
        public OrderStatusRequest(String status) {
            this.status = status;
        }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    // ==================== NEW STATISTICS METHODS ====================
    
    public void loadRevenueStats(String startDate, String endDate, String period) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        Log.d(TAG, "Loading revenue statistics");
        
        Call<AdminRevenueResponse> call = apiService.getAdminRevenue(startDate, endDate, period);
        call.enqueue(new Callback<AdminRevenueResponse>() {
            @Override
            public void onResponse(Call<AdminRevenueResponse> call, Response<AdminRevenueResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminRevenueResponse stats = response.body();
                    Log.d(TAG, "Revenue stats loaded successfully");
                    revenueStats.setValue(stats);
                } else {
                    Log.e(TAG, "Failed to load revenue stats: " + response.code());
                    errorMessage.setValue("Không thể tải thống kê doanh thu");
                }
            }

            @Override
            public void onFailure(Call<AdminRevenueResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Revenue stats network error", t);
                errorMessage.setValue("Lỗi kết nối khi tải thống kê doanh thu");
            }
        });
    }
    
    public void loadOrderStats(String period, String status) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        Log.d(TAG, "Loading order statistics");
        
        Call<AdminOrderStatsResponse> call = apiService.getAdminOrderStats(period, status);
        call.enqueue(new Callback<AdminOrderStatsResponse>() {
            @Override
            public void onResponse(Call<AdminOrderStatsResponse> call, Response<AdminOrderStatsResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminOrderStatsResponse stats = response.body();
                    Log.d(TAG, "Order stats loaded successfully");
                    orderStats.setValue(stats);
                } else {
                    Log.e(TAG, "Failed to load order stats: " + response.code());
                    errorMessage.setValue("Không thể tải thống kê đơn hàng");
                }
            }

            @Override
            public void onFailure(Call<AdminOrderStatsResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Order stats network error", t);
                errorMessage.setValue("Lỗi kết nối khi tải thống kê đơn hàng");
            }
        });
    }
    
    public void loadUserStats(String period) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        Log.d(TAG, "Loading user statistics");
        
        Call<AdminUserStatsResponse> call = apiService.getAdminUserStats(period);
        call.enqueue(new Callback<AdminUserStatsResponse>() {
            @Override
            public void onResponse(Call<AdminUserStatsResponse> call, Response<AdminUserStatsResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminUserStatsResponse stats = response.body();
                    Log.d(TAG, "User stats loaded successfully");
                    userStats.setValue(stats);
                } else {
                    Log.e(TAG, "Failed to load user stats: " + response.code());
                    errorMessage.setValue("Không thể tải thống kê người dùng");
                }
            }

            @Override
            public void onFailure(Call<AdminUserStatsResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "User stats network error", t);
                errorMessage.setValue("Lỗi kết nối khi tải thống kê người dùng");
            }
        });
    }
    
    public void loadProductStats() {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        Log.d(TAG, "Loading product statistics");
        
        Call<AdminProductStatsResponse> call = apiService.getAdminProductStats();
        call.enqueue(new Callback<AdminProductStatsResponse>() {
            @Override
            public void onResponse(Call<AdminProductStatsResponse> call, Response<AdminProductStatsResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminProductStatsResponse stats = response.body();
                    Log.d(TAG, "Product stats loaded successfully");
                    productStats.setValue(stats);
                } else {
                    Log.e(TAG, "Failed to load product stats: " + response.code());
                    errorMessage.setValue("Không thể tải thống kê sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<AdminProductStatsResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Product stats network error", t);
                errorMessage.setValue("Lỗi kết nối khi tải thống kê sản phẩm");
            }
        });
    }
    
    // Convenience method to load all stats at once
    public void loadAllStats() {
        loadDashboardStats();
        loadRevenueStats(null, null, null);
        loadOrderStats(null, null);
        loadUserStats(null);
        loadProductStats();
    }
    
    // ==================== ORDER MANAGEMENT METHODS ====================
    
    public void loadAdminOrders(int page, int size, String status, String search) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        Log.d(TAG, "Loading admin orders - page: " + page + ", status: " + status);
        
        Call<AdminOrdersResponse> call = apiService.getAdminOrders(page, size, status, search);
        call.enqueue(new Callback<AdminOrdersResponse>() {
            @Override
            public void onResponse(Call<AdminOrdersResponse> call, Response<AdminOrdersResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminOrdersResponse orders = response.body();
                    Log.d(TAG, "Admin orders loaded successfully: " + 
                        (orders.getData() != null ? orders.getData().getOrders().size() : 0) + " orders");
                    adminOrders.setValue(orders);
                } else {
                    Log.e(TAG, "Failed to load admin orders: " + response.code());
                    errorMessage.setValue("Không thể tải danh sách đơn hàng");
                }
            }

            @Override
            public void onFailure(Call<AdminOrdersResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Admin orders network error", t);
                errorMessage.setValue("Lỗi kết nối khi tải danh sách đơn hàng");
            }
        });
    }
    
    public void updateOrderStatus(String orderId, String newStatus) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        orderUpdateResult.setValue(false);
        
        Log.d(TAG, "Updating order status - ID: " + orderId + ", Status: " + newStatus);
        
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest(newStatus);
        Call<AdminResponse<Object>> call = apiService.updateOrderStatus(orderId, request);
        call.enqueue(new Callback<AdminResponse<Object>>() {
            @Override
            public void onResponse(Call<AdminResponse<Object>> call, Response<AdminResponse<Object>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminResponse<Object> result = response.body();
                    if (result.isSuccess()) {
                        Log.d(TAG, "Order status updated successfully");
                        orderUpdateResult.setValue(true);
                        // Reload orders to reflect changes
                        loadAdminOrders(1, 10, null, null);
                    } else {
                        Log.e(TAG, "Failed to update order status: " + result.getMessage());
                        errorMessage.setValue(result.getMessage());
                    }
                } else {
                    Log.e(TAG, "Failed to update order status: " + response.code());
                    errorMessage.setValue("Không thể cập nhật trạng thái đơn hàng");
                }
            }

            @Override
            public void onFailure(Call<AdminResponse<Object>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Update order status network error", t);
                errorMessage.setValue("Lỗi kết nối khi cập nhật trạng thái");
            }
        });
    }
    
    public void loadCustomerDetails(String orderId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        Log.d(TAG, "Loading customer details for order: " + orderId);
        
        Call<AdminCustomerResponse> call = apiService.getOrderCustomer(orderId);
        call.enqueue(new Callback<AdminCustomerResponse>() {
            @Override
            public void onResponse(Call<AdminCustomerResponse> call, Response<AdminCustomerResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AdminCustomerResponse customer = response.body();
                    Log.d(TAG, "Customer details loaded successfully");
                    customerDetails.setValue(customer);
                } else {
                    Log.e(TAG, "Failed to load customer details: " + response.code());
                    errorMessage.setValue("Không thể tải thông tin khách hàng");
                }
            }

            @Override
            public void onFailure(Call<AdminCustomerResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Customer details network error", t);
                errorMessage.setValue("Lỗi kết nối khi tải thông tin khách hàng");
            }
        });
    }
}
