package com.example.phoneshop.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.model.OrderRequest;
import com.example.phoneshop.data.model.OrderResponse;
import com.example.phoneshop.data.remote.ApiService;
import com.example.phoneshop.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static OrderRepository instance;
    private final ApiService apiService;

    private OrderRepository() {
        apiService = RetrofitClient.getInstance().getApiService();
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    // Lấy danh sách đơn hàng
    public LiveData<OrderResponse> getOrders(int page, int size) {
        MutableLiveData<OrderResponse> data = new MutableLiveData<>();

        apiService.getOrders(page, size).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    // Lấy chi tiết đơn hàng
    public LiveData<Order> getOrderById(String orderId) {
        MutableLiveData<Order> data = new MutableLiveData<>();

        apiService.getOrderById(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    // Tạo đơn hàng mới
    public LiveData<Order> createOrder(OrderRequest request) {
        MutableLiveData<Order> data = new MutableLiveData<>();

        apiService.createOrder(request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }
}

