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
        apiService = RetrofitClient.getApiService();
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
        
        apiService.getProducts(page, size, query, brand, sort).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        
        return data;
    }

    // Lấy chi tiết sản phẩm
    public LiveData<Product> getProductById(String productId) {
        MutableLiveData<Product> data = new MutableLiveData<>();
        
        apiService.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                data.setValue(null);
            }
        });
        
        return data;
    }

    // Tìm kiếm sản phẩm
    public LiveData<ProductResponse> searchProducts(String query, int page, int size) {
        MutableLiveData<ProductResponse> data = new MutableLiveData<>();
        
        apiService.searchProducts(query, page, size).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        
        return data;
    }

    // Lọc theo thương hiệu
    public LiveData<ProductResponse> getProductsByBrand(String brand, int page, int size) {
        MutableLiveData<ProductResponse> data = new MutableLiveData<>();
        
        apiService.getProductsByBrand(brand, page, size).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        
        return data;
    }
}

