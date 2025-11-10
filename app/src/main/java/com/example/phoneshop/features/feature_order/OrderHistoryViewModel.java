package com.example.phoneshop.features.feature_order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryViewModel extends ViewModel {

    private final OrderRepository repository;
    
    private final MutableLiveData<List<Order>> _orders = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isEmpty = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public LiveData<List<Order>> getOrders() {
        return _orders;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }
    
    public LiveData<Boolean> getIsEmpty() {
        return _isEmpty;
    }
    
    public LiveData<String> getError() {
        return _error;
    }

    public OrderHistoryViewModel() {
        repository = OrderRepository.getInstance();
        _isLoading.setValue(false);
        _isEmpty.setValue(true);
        _error.setValue("");
    }

    public void loadOrderHistory() {
        _isLoading.setValue(true);
        _error.setValue("");
        
        try {
            // Create mock data for testing
            List<Order> mockOrders = createMockOrders();
            
            _isLoading.setValue(false);
            _orders.setValue(mockOrders);
            _isEmpty.setValue(mockOrders.isEmpty());
            _error.setValue("");
            
        } catch (Exception e) {
            _isLoading.setValue(false);
            _orders.setValue(new ArrayList<>());
            _isEmpty.setValue(true);
            _error.setValue("Không thể tải lịch sử đơn hàng: " + e.getMessage());
        }
    }
    
    private List<Order> createMockOrders() {
        List<Order> orders = new ArrayList<>();
        
        // Mock order 1
        Order order1 = new Order();
        order1.setOrderId("ORD001");
        order1.setOrderDate("2024-11-10");
        order1.setStatus("Hoàn thành");
        order1.setTotalPrice(15750000L);
        order1.setItemCount(3);
        order1.setFullName("Nguyễn Văn A");
        order1.setPhone("0123456789");
        order1.setAddress("123 Đường ABC, Quận 1, TP.HCM");
        order1.setPaymentMethod("COD");
        orders.add(order1);
        
        // Mock order 2
        Order order2 = new Order();
        order2.setOrderId("ORD002");
        order2.setOrderDate("2024-11-09");
        order2.setStatus("Đang giao");
        order2.setTotalPrice(29990000L);
        order2.setItemCount(1);
        order2.setFullName("Nguyễn Văn A");
        order2.setPhone("0123456789");
        order2.setAddress("123 Đường ABC, Quận 1, TP.HCM");
        order2.setPaymentMethod("PayOS");
        orders.add(order2);
        
        return orders;
    }

    public void refreshOrders() {
        loadOrderHistory();
    }
}
