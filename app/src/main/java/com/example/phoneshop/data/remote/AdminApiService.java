package com.example.phoneshop.data.remote;

import com.example.phoneshop.data.model.ApiResponse;
import com.example.phoneshop.data.model.Category;
import com.example.phoneshop.data.model.DashboardStats;
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface for administrative operations (Dashboard, Orders, Users, Products, Categories).
 * All endpoints require an Authorization Header (JWT Token).
 */
public interface AdminApiService {

    // --- Dashboard ---
    @GET("admin/dashboard/stats")
    Call<ApiResponse<DashboardStats>> getDashboardStats(@Header("Authorization") String authToken);

    // --- Quản lý Đơn hàng ---
    @GET("admin/orders")
    Call<ApiResponse<List<Order>>> getOrders(@Header("Authorization") String authToken);

    @POST("admin/orders/{orderId}/update-status")
    Call<ApiResponse<String>> updateOrderStatus(
            @Header("Authorization") String authToken,
            @Path("orderId") String orderId,
            @Query("status") String newStatus
    );

    // --- Quản lý Người dùng ---
    @GET("admin/users")
    Call<ApiResponse<List<User>>> getUsers(@Header("Authorization") String authToken);

    @POST("admin/users/{userId}/block")
    Call<ApiResponse<String>> blockUser(
            @Header("Authorization") String authToken,
            @Path("userId") String userId,
            @Query("isBlocked") boolean isBlocked
    );

    // --- Quản lý Sản phẩm ---
    @GET("admin/products")
    Call<ApiResponse<List<Product>>> getProducts(@Header("Authorization") String authToken);

    @DELETE("admin/products/{productId}")
    Call<ApiResponse<String>> deleteProduct(
            @Header("Authorization") String authToken,
            @Path("productId") String productId
    );

    // --- Quản lý Nhóm sản phẩm (Category) ---
    @GET("admin/categories")
    Call<ApiResponse<List<Category>>> getCategories(@Header("Authorization") String authToken);

    @DELETE("admin/categories/{categoryId}")
    Call<ApiResponse<String>> deleteCategory(
            @Header("Authorization") String authToken,
            @Path("categoryId") String categoryId
    );
}