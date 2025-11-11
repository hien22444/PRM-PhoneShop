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

    public void loadOrderHistory(String userId) {
        _isLoading.setValue(true);
        _error.setValue("");
        
        android.util.Log.d("OrderHistoryViewModel", "Loading order history for user: " + userId);
        
        if (userId == null || userId.isEmpty()) {
            android.util.Log.e("OrderHistoryViewModel", "UserId is null or empty");
            _isLoading.setValue(false);
            _orders.setValue(new ArrayList<>());
            _isEmpty.setValue(true);
            _error.setValue("Vui lòng đăng nhập để xem lịch sử đơn hàng");
            return;
        }
        
        try {
            // Use API to load orders instead of local storage
            LiveData<List<Order>> orderHistoryLiveData = repository.getOrderHistory(userId);
            
            Observer<List<Order>> observer = new Observer<List<Order>>() {
                private boolean hasBeenCalled = false;
                
                @Override
                public void onChanged(List<Order> orders) {
                    if (hasBeenCalled) return;
                    hasBeenCalled = true;
                    
                    _isLoading.setValue(false);
                    if (orders != null) {
                        _orders.setValue(orders);
                        _isEmpty.setValue(orders.isEmpty());
                        _error.setValue("");
                        android.util.Log.d("OrderHistoryViewModel", "Loaded " + orders.size() + " orders from API");
                    } else {
                        _orders.setValue(new ArrayList<>());
                        _isEmpty.setValue(true);
                        _error.setValue("Không thể tải lịch sử đơn hàng từ server");
                        android.util.Log.e("OrderHistoryViewModel", "Orders is null from API");
                    }
                    orderHistoryLiveData.removeObserver(this);
                }
            };
            
            orderHistoryLiveData.observeForever(observer);
            
        } catch (Exception e) {
            android.util.Log.e("OrderHistoryViewModel", "Error loading orders: " + e.getMessage());
            _isLoading.setValue(false);
            _orders.setValue(new ArrayList<>());
            _isEmpty.setValue(true);
            _error.setValue("Không thể tải lịch sử đơn hàng: " + e.getMessage());
        }
    }
    
    // Backward compatibility method
    public void loadOrderHistory() {
        // Try to get userId from somewhere or show error
        android.util.Log.w("OrderHistoryViewModel", "loadOrderHistory() called without userId - this is deprecated");
        _isLoading.setValue(false);
        _orders.setValue(new ArrayList<>());
        _isEmpty.setValue(true);
        _error.setValue("Vui lòng đăng nhập để xem lịch sử đơn hàng");
    }

    public void refreshOrders(String userId) {
        loadOrderHistory(userId);
    }
    
    // Backward compatibility method
    public void refreshOrders() {
        android.util.Log.w("OrderHistoryViewModel", "refreshOrders() called without userId - this is deprecated");
        loadOrderHistory();
    }
}
