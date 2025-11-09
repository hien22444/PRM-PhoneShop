package com.example.phoneshop.features.feature_cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.model.OrderResponse;
import com.example.phoneshop.data.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryViewModel extends ViewModel {

    private final OrderRepository repository;
    
    private final MutableLiveData<List<Order>> _orders = new MutableLiveData<>();
    public LiveData<List<Order>> getOrders() { return _orders; }

    private final MutableLiveData<Boolean> _isEmpty = new MutableLiveData<>();
    public LiveData<Boolean> getIsEmpty() { return _isEmpty; }
    
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading() { return _isLoading; }
    
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> getError() { return _error; }

    // Constructor
    public OrderHistoryViewModel() {
        repository = OrderRepository.getInstance();
        _isLoading.setValue(false);
        _error.setValue("");
        loadOrderHistory();
    }

    public void loadOrderHistory() {
        _isLoading.setValue(true);
        _error.setValue("");
        
        LiveData<OrderResponse> responseLiveData = repository.getOrders(0, 20);
        
        Observer<OrderResponse> observer = new Observer<OrderResponse>() {
            private boolean hasBeenCalled = false;
            
            @Override
            public void onChanged(OrderResponse response) {
                if (hasBeenCalled) return;
                hasBeenCalled = true;
                
                _isLoading.setValue(false);
                if (response != null && response.getContent() != null) {
                    _orders.setValue(response.getContent());
                    _isEmpty.setValue(response.getContent().isEmpty());
                    _error.setValue("");
                } else {
                    _orders.setValue(new ArrayList<>());
                    _isEmpty.setValue(true);
                    _error.setValue("Không thể tải lịch sử đơn hàng");
                }
                responseLiveData.removeObserver(this);
            }
        };
        
        responseLiveData.observeForever(observer);
    }
}