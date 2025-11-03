package com.example.phoneshop.features.feature_cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.data.model.CartItem; // Giả sử bạn có model này

import java.util.ArrayList;
import java.util.List;

// (Ghi chú: Bạn nên kế thừa từ BaseViewModel nếu có)
public class CartViewModel extends ViewModel {

    // Dùng MutableLiveData để Fragment có thể "lắng nghe" (observe) sự thay đổi
    // (private) Biến để giữ dữ liệu
    private final MutableLiveData<List<CartItem>> _cartItems = new MutableLiveData<>();
    private final MutableLiveData<Long> _totalPrice = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isEmpty = new MutableLiveData<>();

    // (public) Biến LiveData để Fragment truy cập (chỉ đọc)
    public LiveData<List<CartItem>> getCartItems() {
        return _cartItems;
    }
    public LiveData<Long> getTotalPrice() {
        return _totalPrice;
    }
    public LiveData<Boolean> getIsEmpty() {
        return _isEmpty;
    }

    // Constructor: Nơi bạn sẽ load dữ liệu lần đầu (từ Repository)
    public CartViewModel() {
        loadCartItems();
    }

    // ***** Nhiệm vụ 1: Load dữ liệu *****
    // (Đây là nơi bạn sẽ gọi API hoặc Database.
    // Bây giờ chúng ta sẽ tạo dữ liệu giả (dummy data) để test)
    public void loadCartItems() {
        ArrayList<CartItem> dummyList = new ArrayList<>();
        // (Bạn sẽ thay thế phần này bằng code gọi Repository)
        dummyList.add(new CartItem("1", "iPhone 15 Pro", 29990000L, 1, "url_to_image"));
        dummyList.add(new CartItem("2", "Samsung S24 Ultra", 31490000L, 1, "url_to_image"));
        // ...

        _cartItems.setValue(dummyList); // Cập nhật LiveData
        calculateTotalPrice(); // Tính tổng tiền sau khi load
    }

    // ***** Nhiệm vụ 2: Tính tổng tiền *****
    private void calculateTotalPrice() {
        List<CartItem> items = _cartItems.getValue();
        if (items == null || items.isEmpty()) {
            _totalPrice.setValue(0L);
            _isEmpty.setValue(true); // Giỏ hàng trống
            return;
        }

        long total = 0;
        for (CartItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }

        _totalPrice.setValue(total); // Cập nhật LiveData tổng tiền
        _isEmpty.setValue(false); // Giỏ hàng có đồ
    }

    // ***** Nhiệm vụ 3: Xử lý sự kiện từ Adapter (do Fragment gọi) *****

    public void onIncreaseClick(CartItem item) {
        List<CartItem> currentList = _cartItems.getValue();
        if (currentList == null) return;

        for (CartItem i : currentList) {
            if (i.getProductId().equals(item.getProductId())) {
                i.setQuantity(i.getQuantity() + 1); // Tăng số lượng
                break;
            }
        }
        _cartItems.setValue(currentList); // Cập nhật lại
        calculateTotalPrice(); // Tính lại tổng tiền
    }

    public void onDecreaseClick(CartItem item) {
        List<CartItem> currentList = _cartItems.getValue();
        if (currentList == null) return;

        for (CartItem i : currentList) {
            if (i.getProductId().equals(item.getProductId())) {
                if (i.getQuantity() > 1) { // Chỉ giảm khi > 1
                    i.setQuantity(i.getQuantity() - 1); // Giảm số lượng
                }
                break;
            }
        }
        _cartItems.setValue(currentList); // Cập nhật lại
        calculateTotalPrice(); // Tính lại tổng tiền
    }

    public void onDeleteClick(CartItem item) {
        List<CartItem> currentList = _cartItems.getValue();
        if (currentList == null) return;

        currentList.remove(item); // Xóa khỏi danh sách

        _cartItems.setValue(currentList); // Cập nhật lại
        calculateTotalPrice(); // Tính lại tổng tiền
    }
}