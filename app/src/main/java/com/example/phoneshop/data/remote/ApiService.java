package com.example.phoneshop.data.remote;

import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.model.ProductResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // Lấy danh sách sản phẩm với phân trang, tìm kiếm, lọc, sắp xếp
    @GET("admin/products")
    Call<ProductResponse> getProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("q") String query,
            @Query("brand") String brand,
            @Query("sort") String sort
    );
    
    // Lấy chi tiết sản phẩm
    @GET("admin/products/{id}")
    Call<Product> getProductById(@Path("id") String productId);
    
    // Tìm kiếm sản phẩm
    @GET("admin/products")
    Call<ProductResponse> searchProducts(
            @Query("q") String query,
            @Query("page") int page,
            @Query("size") int size
    );
    
    // Lọc theo thương hiệu
    @GET("admin/products")
    Call<ProductResponse> getProductsByBrand(
            @Query("brand") String brand,
            @Query("page") int page,
            @Query("size") int size
    );
}

