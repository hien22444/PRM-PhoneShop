package com.example.phoneshop.features.feature_order;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.repository.OrderRepository;
import com.example.phoneshop.service.OrderStorageService;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryViewModel extends ViewModel {

    private final OrderRepository repository;
    private OrderStorageService orderStorageService;
    
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
        orderStorageService = OrderStorageService.getInstance();
        _isLoading.setValue(false);
        _isEmpty.setValue(true);
        _error.setValue("");
    }
    
    public void initialize(Context context) {
        orderStorageService.initialize(context);
    }

    public void loadOrderHistory() {
        _isLoading.setValue(true);
        _error.setValue("");
        
        try {
            // Load real orders from storage
            List<Order> orders = orderStorageService.getAllOrders();
            
            _isLoading.setValue(false);
            _orders.setValue(orders);
            _isEmpty.setValue(orders.isEmpty());
            _error.setValue("");
            
            android.util.Log.d("OrderHistoryViewModel", "Loaded " + orders.size() + " orders from storage");
            
        } catch (Exception e) {
            android.util.Log.e("OrderHistoryViewModel", "Error loading orders: " + e.getMessage());
            _isLoading.setValue(false);
            _orders.setValue(new ArrayList<>());
            _isEmpty.setValue(true);
            _error.setValue("Không thể tải lịch sử đơn hàng: " + e.getMessage());
        }
    }

    public void refreshOrders() {
        loadOrderHistory();
    }
}
