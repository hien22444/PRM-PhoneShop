package com.example.phoneshop.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.model.CartItem; // Cần import CartItem nếu nó tồn tại
import java.util.ArrayList;
import java.util.List;

public class AdminOrderRepository {

    public AdminOrderRepository() {
        // Khởi tạo các dịch vụ API/DB ở đây
    }

    public LiveData<List<Order>> getOrders() {
        MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();

        List<Order> mockOrders = createMockOrders();
        ordersLiveData.setValue(mockOrders);

        return ordersLiveData;
    }

    // Hàm tạo dữ liệu giả lập (Sử dụng đúng 8 tham số của Constructor Order)
    private List<Order> createMockOrders() {
        List<Order> orders = new ArrayList<>();

        // Giả lập một danh sách các CartItem cho mỗi đơn hàng
        List<CartItem> items1 = new ArrayList<>();
        items1.add(new CartItem("SP001", "iPhone 15 Pro Max", 1, 35000000.0, null));

        List<CartItem> items2 = new ArrayList<>();
        items2.add(new CartItem("SP002", "Samsung S24 Ultra", 2, 28000000.0, null));

        // 8 tham số: id, userName, totalAmount, status, date, address, phoneNumber, items
        orders.add(new Order(
                "ORD001",
                "Nguyễn Văn A",
                35000000.0,
                "Đang chờ xử lý",
                "2025-11-10",
                "123 Đường ABC, Hà Nội",
                "090111222",
                items1));

        orders.add(new Order(
                "ORD002",
                "Trần Thị B",
                56000000.0,
                "Đang giao",
                "2025-11-09",
                "456 Phố XYZ, TP.HCM",
                "090333444",
                items2));

        return orders;
    }
}