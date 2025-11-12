package com.example.phoneshop.features.feature_admin_ops.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.repository.AdminOrderRepository;
import java.util.List;

public class AdminOrderViewModel extends ViewModel {

    private final AdminOrderRepository repository;
    private final MutableLiveData<List<Order>> _orders = new MutableLiveData<>();
    public LiveData<List<Order>> orders = _orders;

    // THÊM: LiveData để giữ thông báo lỗi
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage; // Phương thức getErrorMessage() sẽ trả về cái này

    public AdminOrderViewModel() {
        this.repository = new AdminOrderRepository();
    }

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    // THÊM: Phương thức công khai để lấy LiveData lỗi
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchOrders() {
        LiveData<List<Order>> repoOrders = repository.getOrders();

        repoOrders.observeForever(newOrders -> {
            // Giả định thành công, cập nhật dữ liệu
            _orders.setValue(newOrders);

            // Xử lý lỗi (Ví dụ giả định lỗi)
            if (newOrders == null || newOrders.isEmpty()) {
                // Nếu dữ liệu trả về null hoặc rỗng, có thể coi là lỗi hoặc không có dữ liệu
                _errorMessage.setValue("Không tìm thấy đơn hàng nào hoặc lỗi kết nối.");
            } else {
                _errorMessage.setValue(null); // Xóa lỗi nếu tải thành công
            }
        });

        // Lưu ý: Trong ứng dụng thực tế, bạn sẽ nhận lỗi từ API (ví dụ: onFailure của Retrofit)
    }
}