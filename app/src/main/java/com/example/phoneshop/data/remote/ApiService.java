package com.example.phoneshop.data.remote;

import com.example.phoneshop.data.model.CartRequest;
import com.example.phoneshop.data.model.CartResponse;
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.model.OrderRequest;
import com.example.phoneshop.data.model.OrderResponse;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.model.ProductResponse;

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

}
