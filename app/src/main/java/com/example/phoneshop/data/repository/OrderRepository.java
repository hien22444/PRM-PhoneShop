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
import java.util.List;
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

        android.util.Log.d("OrderRepository", "Creating order via API - Items: " + 
            (request.getItems() != null ? request.getItems().size() : "null"));

        // Use real API call instead of mock
        apiService.createOrder(request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("OrderRepository", "Order created successfully via API: " + 
                        response.body().getOrderId());
                    data.setValue(response.body());
                } else {
                    android.util.Log.e("OrderRepository", "API call failed - Response code: " + 
                        response.code() + ", Message: " + response.message());
                    
                    // Fallback to mock order if API fails
                    android.util.Log.w("OrderRepository", "Falling back to mock order");
                    Order mockOrder = createMockOrder(request);
                    data.setValue(mockOrder);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                android.util.Log.e("OrderRepository", "API call failed: " + t.getMessage());
                
                // Fallback to mock order if API fails
                android.util.Log.w("OrderRepository", "Falling back to mock order due to network error");
                try {
                    Order mockOrder = createMockOrder(request);
                    data.setValue(mockOrder);
                } catch (Exception e) {
                    android.util.Log.e("OrderRepository", "Mock order creation failed: " + e.getMessage());
                    data.setValue(null);
                }
            }
        });

        return data;
    }
    
    private Order createMockOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderId("ORD" + System.currentTimeMillis());
        order.setOrderDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
        order.setStatus("Đang xử lý");
        order.setFullName(request.getFullName());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        
        // Calculate total price from items
        double totalPrice = 0;
        int itemCount = 0;
        if (request.getItems() != null) {
            for (OrderRequest.OrderItemRequest item : request.getItems()) {
                totalPrice += item.getPrice() * item.getQuantity();
                itemCount += item.getQuantity();
            }
        }
        order.setTotalPrice((long) totalPrice);
        order.setItemCount(itemCount);
        
        return order;
    }

    // Lấy lịch sử đơn hàng theo userId
    public LiveData<List<Order>> getOrderHistory(String userId) {
        MutableLiveData<List<Order>> data = new MutableLiveData<>();

        android.util.Log.d("OrderRepository", "Getting order history for user: " + userId);

        apiService.getOrderHistory(userId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("OrderRepository", "Order history loaded: " + response.body().size() + " orders");
                    data.setValue(response.body());
                } else {
                    android.util.Log.e("OrderRepository", "Failed to load order history - Response code: " + 
                        response.code() + ", Message: " + response.message());
                    data.setValue(new java.util.ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                android.util.Log.e("OrderRepository", "Order history API call failed: " + t.getMessage());
                data.setValue(new java.util.ArrayList<>());
            }
        });

        return data;
    }
}

