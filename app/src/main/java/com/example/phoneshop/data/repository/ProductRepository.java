package com.example.phoneshop.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.model.ProductResponse;
import com.example.phoneshop.data.remote.ApiService;
import com.example.phoneshop.data.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static ProductRepository instance;
    private final ApiService apiService;

    private ProductRepository() {
        apiService = RetrofitClient.getInstance().getApiService();
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    // Lấy danh sách sản phẩm
    public LiveData<ProductResponse> getProducts(int page, int size, String query, String brand, String sort) {
        MutableLiveData<ProductResponse> data = new MutableLiveData<>();

        android.util.Log.d("ProductRepository", String.format(
                "Starting to fetch products - Page: %d, Size: %d, Query: %s, Brand: %s, Sort: %s",
                page, size, query, brand, sort));
        android.util.Log.d("ProductRepository", "Base URL: " + RetrofitClient.getInstance().getBaseUrl());

        Call<ProductResponse> call = apiService.getProducts(page, size, query, brand, sort);
        android.util.Log.d("ProductRepository", "Full request URL: " + call.request().url());
        android.util.Log.d("ProductRepository", "Request headers: " + call.request().headers());

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                android.util.Log.d("ProductRepository", "Response received with code: " + response.code());
                android.util.Log.d("ProductRepository", "Response headers: " + response.headers());

                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    int productCount = (productResponse.getContent() != null) ? productResponse.getContent().size() : 0;
                    android.util.Log.d("ProductRepository", String.format(
                            "Successfully fetched products. Total count: %d, Current page items: %d",
                            productResponse.getTotalElements(), productCount));

                    if (productCount > 0) {
                        Product firstProduct = productResponse.getContent().get(0);
                        android.util.Log.d("ProductRepository", String.format(
                                "Sample product - Name: %s, Price: %d, Brand: %s",
                                firstProduct.getName(), firstProduct.getPrice(), firstProduct.getBrand()));
                    }

                    data.setValue(productResponse);
                } else {
                    android.util.Log.e("ProductRepository", String.format(
                            "Error fetching products. Response Code: %d, Message: %s",
                            response.code(), response.message()));

                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string()
                                : "Unknown error";
                        android.util.Log.e("ProductRepository", "Error response body: " + errorBody);
                    } catch (Exception e) {
                        android.util.Log.e("ProductRepository", "Error reading error response body", e);
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                android.util.Log.e("ProductRepository", String.format(
                        "Network failure while fetching products. Request URL: %s",
                        call.request().url()));
                android.util.Log.e("ProductRepository", "Error details:", t);
                data.setValue(null);
            }
        });

        return data;
    }

    // Lấy chi tiết sản phẩm
    public LiveData<Product> getProductById(String productId) {
        MutableLiveData<Product> data = new MutableLiveData<>();

        // Log request
        android.util.Log.d("ProductRepository", "Fetching product details for ID: " + productId);

        apiService.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("ProductRepository",
                            "Successfully fetched product: " + response.body().getName());
                    data.setValue(response.body());
                } else {
                    android.util.Log.e("ProductRepository", "Error fetching product. Code: " + response.code() +
                            " Message: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string()
                                : "Unknown error";
                        android.util.Log.e("ProductRepository", "Error body: " + errorBody);
                    } catch (Exception e) {
                        android.util.Log.e("ProductRepository", "Error reading error body", e);
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                android.util.Log.e("ProductRepository", "Network failure fetching product", t);
                data.setValue(null);
            }
        });

        return data;
    }

    // Tìm kiếm sản phẩm
    public LiveData<ProductResponse> searchProducts(String query, int page, int size) {
        android.util.Log.d("ProductRepository", "Searching products with query: " + query);
        
        // Use the main getProducts method with query parameter for better consistency
        return getProducts(page, size, query, null, null);
    }

    // Lọc theo thương hiệu
    public LiveData<ProductResponse> getProductsByBrand(String brand, int page, int size) {
        android.util.Log.d("ProductRepository", "Filtering products by brand: " + brand);
        
        // Use the main getProducts method with brand parameter for better consistency
        return getProducts(page, size, null, brand, null);
    }
}
