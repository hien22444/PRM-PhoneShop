package com.example.phoneshop.data.remote;

import com.example.phoneshop.data.model.AuthRequest;
import com.example.phoneshop.data.model.AuthResponse;
import com.example.phoneshop.data.model.CartRequest;
import com.example.phoneshop.data.model.CartResponse;
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.model.OrderDetailResponse;
import com.example.phoneshop.data.model.OrderRequest;
import com.example.phoneshop.data.model.OrderResponse;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.model.ProductResponse;
import com.example.phoneshop.data.model.User;
import com.example.phoneshop.features.feature_admin.model.AdminLoginResponse;
import com.example.phoneshop.features.feature_admin.model.AdminResponse;
import com.example.phoneshop.features.feature_admin.model.AdminStatsResponse;
import com.example.phoneshop.features.feature_admin.model.AdminRevenueResponse;
import com.example.phoneshop.features.feature_admin.model.AdminOrderStatsResponse;
import com.example.phoneshop.features.feature_admin.model.AdminUserStatsResponse;
import com.example.phoneshop.features.feature_admin.model.AdminProductStatsResponse;
import com.example.phoneshop.features.feature_admin.model.AdminOrdersResponse;
import com.example.phoneshop.features.feature_admin.model.AdminCustomerResponse;
import com.example.phoneshop.features.feature_admin.model.OrderStatusUpdateRequest;
import com.example.phoneshop.features.feature_admin.viewmodel.AdminViewModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;
public interface ApiService {

        // ========== AUTHENTICATION ENDPOINTS ==========
        
        // Đăng ký người dùng mới
        @POST("api/auth/register")
        Call<AuthResponse> register(@Body AuthRequest request);
        
        // Đăng nhập
        @POST("api/auth/login")
        Call<AuthResponse> login(@Body AuthRequest request);
        
        // Lấy thông tin người dùng
        @GET("api/auth/user/{id}")
        Call<AuthResponse> getUser(@Path("id") String userId);
        
        // Cập nhật thông tin người dùng
        @PUT("api/auth/user/{id}")
        Call<AuthResponse> updateUser(@Path("id") String userId, @Body AuthRequest request);
        
        // Đổi mật khẩu
        @PUT("api/auth/user/{id}/password")
        Call<AuthResponse> changePassword(@Path("id") String userId, @Body AuthRequest request);
        
        // Kiểm tra username có tồn tại
        @GET("api/auth/check-username/{username}")
        Call<CheckAvailabilityResponse> checkUsername(@Path("username") String username);
        
        // Kiểm tra email có tồn tại
        @GET("api/auth/check-email/{email}")
        Call<CheckAvailabilityResponse> checkEmail(@Path("email") String email);
        
        // Response class for availability check
        class CheckAvailabilityResponse {
            public boolean exists;
            public boolean available;
        }

        // ========== PRODUCT ENDPOINTS ==========

        // ORIGINAL (Admin) endpoints - keep for admin flows
        // Lấy danh sách sản phẩm (admin)
        @GET("admin/products")
        Call<ProductResponse> getProductsAdmin(
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("q") String query,
                        @Query("brand") String brand,
                        @Query("sort") String sort);

        // Lấy chi tiết sản phẩm (admin)
        @GET("admin/products/{id}")
        Call<Product> getProductByIdAdmin(@Path("id") String productId);

        // Tìm kiếm sản phẩm (admin)
        @GET("admin/products")
        Call<ProductResponse> searchProductsAdmin(
                        @Query("q") String query,
                        @Query("page") int page,
                        @Query("size") int size);

        // Lọc theo thương hiệu (admin)
        @GET("admin/products")
        Call<ProductResponse> getProductsByBrandAdmin(
                        @Query("brand") String brand,
                        @Query("page") int page,
                        @Query("size") int size);

        // PUBLIC endpoints - for anonymous or authenticated regular users
        // Lấy danh sách sản phẩm (public)
        @GET("api/products")
        Call<ProductResponse> getProducts(
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("q") String query,
                        @Query("brand") String brand,
                        @Query("sort") String sort);

        // Lấy chi tiết sản phẩm (public)
        @GET("api/products/{id}")
        Call<Product> getProductById(@Path("id") String productId);

        // Tìm kiếm sản phẩm (public)
        @GET("api/products")
        Call<ProductResponse> searchProducts(
                        @Query("q") String query,
                        @Query("page") int page,
                        @Query("size") int size);

        // Lọc theo thương hiệu (public)
        @GET("api/products")
        Call<ProductResponse> getProductsByBrand(
                        @Query("brand") String brand,
                        @Query("page") int page,
                        @Query("size") int size);

        // ========== CART ENDPOINTS ==========

        // Lấy giỏ hàng của user
        @GET("api/cart")
        Call<CartResponse> getCart();

        // Thêm sản phẩm vào giỏ hàng
        @POST("api/cart/items")
        Call<CartResponse> addToCart(@Body CartRequest request);

        // Cập nhật số lượng sản phẩm trong giỏ hàng
        @PUT("api/cart/items/{itemId}")
        Call<CartResponse> updateCartItem(@Path("itemId") String itemId, @Body CartRequest request);

        // Xóa sản phẩm khỏi giỏ hàng
        @DELETE("api/cart/items/{itemId}")
        Call<CartResponse> removeFromCart(@Path("itemId") String itemId);

        // Xóa toàn bộ giỏ hàng
        @DELETE("api/cart")
        Call<Void> clearCart();

        // ========== ORDER ENDPOINTS ==========

        // Lấy danh sách đơn hàng của user
        @GET("api/orders")
        Call<OrderResponse> getOrders(
                        @Query("page") int page,
                        @Query("size") int size);

        // Lấy chi tiết đơn hàng
        @GET("api/orders/{orderId}")
        Call<Order> getOrderById(@Path("orderId") String orderId);

        // Lấy chi tiết đơn hàng với enhanced response
        @GET("api/orders/detail/{orderId}")
        Call<OrderDetailResponse> getOrderDetail(@Path("orderId") String orderId);

        // Tạo đơn hàng mới từ giỏ hàng
        @POST("api/orders/from-cart")
        Call<Order> createOrder(@Body OrderRequest request);

        // Lấy lịch sử đơn hàng theo userId
        @GET("api/orders/{userId}")
        Call<List<Order>> getOrderHistory(@Path("userId") String userId);

        // ========== ADMIN ENDPOINTS ==========

        // Admin Authentication
        @POST("admin/login")
        Call<AdminLoginResponse> loginAdmin(@Body AdminViewModel.AdminLoginRequest request);

        // Admin Dashboard
        @GET("admin/dashboard")
        Call<AdminStatsResponse> getAdminDashboardStats();
        
        // Admin Revenue Statistics
        @GET("admin/revenue")
        Call<AdminRevenueResponse> getAdminRevenue(
                @Query("startDate") String startDate,
                @Query("endDate") String endDate,
                @Query("period") String period);
        
        // Admin Order Statistics
        @GET("admin/orders/stats")
        Call<AdminOrderStatsResponse> getAdminOrderStats(
                @Query("period") String period,
                @Query("status") String status);
        
        // Admin User Statistics
        @GET("admin/users/stats")
        Call<AdminUserStatsResponse> getAdminUserStats(
                @Query("period") String period);
        
        // Admin Product Statistics
        @GET("admin/products/stats")
        Call<AdminProductStatsResponse> getAdminProductStats();

        // Admin User Management
        @GET("admin/users")
        Call<AdminResponse<User>> getAdminUsers(
                @Query("page") int page,
                @Query("size") int size,
                @Query("q") String query);
        
        @GET("admin/users/{id}")
        Call<AdminResponse<User>> getAdminUserDetail(@Path("id") String userId);
        
        @DELETE("admin/users/{id}")
        Call<AdminResponse> deleteAdminUser(@Path("id") String userId);
        
        // Admin Order Management
        @GET("admin/orders")
        Call<AdminOrdersResponse> getAdminOrders(
                @Query("page") int page,
                @Query("size") int size,
                @Query("status") String status,
                @Query("search") String search);
        
        @GET("admin/orders/{id}")
        Call<AdminResponse<Order>> getAdminOrderDetail(@Path("id") String orderId);
        
        @PUT("admin/orders/{orderId}/status")
        Call<AdminResponse<Object>> updateOrderStatus(
                @Path("orderId") String orderId,
                @Body OrderStatusUpdateRequest request);
        
        @GET("admin/orders/{orderId}/customer")
        Call<AdminCustomerResponse> getOrderCustomer(
                @Path("orderId") String orderId);
        
        // Admin Product Management
        @GET("admin/products")
        Call<AdminResponse<Product>> getAdminProducts(
                @Query("page") int page,
                @Query("size") int size,
                @Query("q") String query,
                @Query("brand") String brand,
                @Query("sort") String sort);
        
        @GET("admin/products/{id}")
        Call<AdminResponse<Product>> getAdminProductDetail(@Path("id") String productId);
        
        @POST("admin/products")
        Call<Product> createAdminProduct(@Body Product product);
        
        @PUT("admin/products/{id}")
        Call<Product> updateAdminProduct(@Path("id") String productId, @Body Product product);
        
        @DELETE("admin/products/{id}")
        Call<Void> deleteAdminProduct(@Path("id") String productId);
        
        @PUT("admin/products/{id}/visibility")
        Call<AdminResponse> updateAdminProductVisibility(
                @Path("id") String productId,
                @Query("visible") boolean visible);
}
