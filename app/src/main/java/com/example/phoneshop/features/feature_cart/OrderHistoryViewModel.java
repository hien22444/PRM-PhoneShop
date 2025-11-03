package com.example.phoneshop.features.feature_cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.phoneshop.data.model.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryViewModel extends ViewModel {

    private final MutableLiveData<List<Order>> _orders = new MutableLiveData<>();
    public LiveData<List<Order>> getOrders() { return _orders; }

    private final MutableLiveData<Boolean> _isEmpty = new MutableLiveData<>();
    public LiveData<Boolean> getIsEmpty() { return _isEmpty; }

    // Constructor
    public OrderHistoryViewModel() {
        loadOrderHistory();
    }

    public void loadOrderHistory() {
        // (Đây là nơi bạn gọi Repository/API thật)
        // Tạo dữ liệu giả để test:
        ArrayList<Order> dummyList = new ArrayList<>();
        dummyList.add(new Order("11345", "01/11/2025", "Hoàn thành", 29990000L, 1));
        dummyList.add(new Order("11344", "30/10/2025", "Đang giao", 31490000L, 1));
        dummyList.add(new Order("11342", "28/10/2025", "Đã hủy", 15990000L, 2));
        dummyList.add(new Order("11341", "27/10/2025", "Đang xử lý", 5990000L, 1));

        _orders.setValue(dummyList);
        _isEmpty.setValue(dummyList.isEmpty());
    }
}