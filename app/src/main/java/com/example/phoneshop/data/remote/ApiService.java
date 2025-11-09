package com.example.phoneshop.data.remote;

import com.example.phoneshop.data.model.CartRequest;
import com.example.phoneshop.data.model.CartResponse;
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.model.OrderRequest;
import com.example.phoneshop.data.model.OrderResponse;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.model.ProductResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

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

        // Tạo đơn hàng mới
        @POST("api/orders")
        Call<Order> createOrder(@Body OrderRequest request);
}
