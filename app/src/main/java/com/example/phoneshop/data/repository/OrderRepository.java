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

        try {
            // Create mock order for testing (since API might not be ready)
            Order mockOrder = createMockOrder(request);
            data.setValue(mockOrder);
            
            // TODO: Uncomment this when API is ready
            /*
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
            */
        } catch (Exception e) {
            android.util.Log.e("OrderRepository", "Error creating order: " + e.getMessage());
            data.setValue(null);
        }

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

    // Lấy lịch sử đơn hàng
    public LiveData<List<Order>> getOrderHistory() {
        MutableLiveData<List<Order>> data = new MutableLiveData<>();

        apiService.getOrderHistory().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }
}

