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

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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


                // ==== USER + ADMIN ====
                @GET("api/products")
                Call<ProductResponse> getProducts(
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("q") String query,
                        @Query("brand") String brand,
                        @Query("sort") String sort
                );

                @GET("api/products/{id}")
                Call<Product> getProductById(@Path("id") String productId);

                // ==== ADMIN ====
                @POST("api/products")
                Call<Product> createProduct(@Body Product p);

                @PUT("api/products/{id}")
                Call<Product> updateProduct(@Path("id") String id, @Body Product p);

                @DELETE("api/products/{id}")
                Call<Void> deleteProduct(@Path("id") String id);



        // === CART ===
        @GET("api/cart")
        Call<CartResponse> getCart();

        @POST("api/cart/items")
        Call<CartResponse> addToCart(@Body CartRequest request);

        @PUT("api/cart/items/{itemId}")
        Call<CartResponse> updateCartItem(@Path("itemId") String itemId, @Body CartRequest request);

        @DELETE("api/cart/items/{itemId}")
        Call<CartResponse> removeFromCart(@Path("itemId") String itemId);

        @DELETE("api/cart")
        Call<Void> clearCart();

        // === ORDER ===
        @GET("api/orders")
        Call<OrderResponse> getOrders(@Query("page") int page, @Query("size") int size);

        @GET("api/orders/{orderId}")
        Call<Order> getOrderById(@Path("orderId") String orderId);


        @POST("api/orders")
        Call<Order> createOrder(@Body OrderRequest request);

        @GET("api/orders/history")
        Call<List<Order>> getOrderHistory();

        // === SEARCH ===
        @GET("api/products")
        Call<ProductResponse> searchProducts(
                @Query("query") String query,
                @Query("page") int page,
                @Query("size") int size
        );


        // Lấy chi tiết đơn hàng với enhanced response
        @GET("api/orders/detail/{orderId}")
        Call<OrderDetailResponse> getOrderDetail(@Path("orderId") String orderId);

        // Tạo đơn hàng mới từ giỏ hàng
        @POST("api/orders/from-cart")
        Call<Order> createOrder(@Body OrderRequest request);

        // Lấy lịch sử đơn hàng theo userId
        @GET("api/orders/{userId}")
        Call<List<Order>> getOrderHistory(@Path("userId") String userId);

}
