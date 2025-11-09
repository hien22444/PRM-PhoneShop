package com.example.phoneshop.features.feature_cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.data.model.CartItem;
import com.example.phoneshop.data.model.CartResponse;
import com.example.phoneshop.data.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {

    private final CartRepository repository;
    
    // Dùng MutableLiveData để Fragment có thể "lắng nghe" (observe) sự thay đổi
    private final MutableLiveData<List<CartItem>> _cartItems = new MutableLiveData<>();
    private final MutableLiveData<Long> _totalPrice = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isEmpty = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();

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
    
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }
    
    public LiveData<String> getError() {
        return _error;
    }

    // Constructor: Nơi bạn sẽ load dữ liệu lần đầu (từ Repository)
    public CartViewModel() {
        repository = CartRepository.getInstance();
        _isLoading.setValue(false);
        _error.setValue("");
        loadCartItems();
    }

    // ***** Nhiệm vụ 1: Load dữ liệu từ API *****
    public void loadCartItems() {
        _isLoading.setValue(true);
        _error.setValue("");
        
        LiveData<CartResponse> responseLiveData = repository.getCart();
        
        Observer<CartResponse> observer = new Observer<CartResponse>() {
            private boolean hasBeenCalled = false;
            
            @Override
            public void onChanged(CartResponse response) {
                if (hasBeenCalled) return;
                hasBeenCalled = true;
                
                _isLoading.setValue(false);
                if (response != null && response.getItems() != null) {
                    _cartItems.setValue(response.getItems());
                    _totalPrice.setValue(response.getTotalPrice());
                    _isEmpty.setValue(response.getItems().isEmpty());
                    _error.setValue("");
                } else {
                    _cartItems.setValue(new ArrayList<>());
                    _totalPrice.setValue(0L);
                    _isEmpty.setValue(true);
                    _error.setValue("Không thể tải giỏ hàng");
                }
                responseLiveData.removeObserver(this);
            }
        };
        
        responseLiveData.observeForever(observer);
    }

    // ***** Nhiệm vụ 2: Thêm sản phẩm vào giỏ hàng *****
    public void addToCart(String productId, int quantity) {
        _isLoading.setValue(true);
        _error.setValue("");
        
        LiveData<CartResponse> responseLiveData = repository.addToCart(productId, quantity);
        
        Observer<CartResponse> observer = new Observer<CartResponse>() {
            private boolean hasBeenCalled = false;
            
            @Override
            public void onChanged(CartResponse response) {
                if (hasBeenCalled) return;
                hasBeenCalled = true;
                
                _isLoading.setValue(false);
                if (response != null && response.getItems() != null) {
                    _cartItems.setValue(response.getItems());
                    _totalPrice.setValue(response.getTotalPrice());
                    _isEmpty.setValue(response.getItems().isEmpty());
                    _error.setValue("");
                } else {
                    _error.setValue("Không thể thêm sản phẩm vào giỏ hàng");
                }
                responseLiveData.removeObserver(this);
            }
        };
        
        responseLiveData.observeForever(observer);
    }

    // ***** Nhiệm vụ 3: Xử lý sự kiện từ Adapter (do Fragment gọi) *****

    public void onIncreaseClick(CartItem item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            return;
        }
        
        _isLoading.setValue(true);
        _error.setValue("");
        
        int newQuantity = item.getQuantity() + 1;
        LiveData<CartResponse> responseLiveData = repository.updateCartItem(item.getId(), newQuantity);
        
        Observer<CartResponse> observer = new Observer<CartResponse>() {
            private boolean hasBeenCalled = false;
            
            @Override
            public void onChanged(CartResponse response) {
                if (hasBeenCalled) return;
                hasBeenCalled = true;
                
                _isLoading.setValue(false);
                if (response != null && response.getItems() != null) {
                    _cartItems.setValue(response.getItems());
                    _totalPrice.setValue(response.getTotalPrice());
                    _isEmpty.setValue(response.getItems().isEmpty());
                    _error.setValue("");
                } else {
                    _error.setValue("Không thể cập nhật giỏ hàng");
                }
                responseLiveData.removeObserver(this);
            }
        };
        
        responseLiveData.observeForever(observer);
    }

    public void onDecreaseClick(CartItem item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            return;
        }
        
        if (item.getQuantity() <= 1) {
            return; // Không giảm nếu số lượng <= 1
        }
        
        _isLoading.setValue(true);
        _error.setValue("");
        
        int newQuantity = item.getQuantity() - 1;
        LiveData<CartResponse> responseLiveData = repository.updateCartItem(item.getId(), newQuantity);
        
        Observer<CartResponse> observer = new Observer<CartResponse>() {
            private boolean hasBeenCalled = false;
            
            @Override
            public void onChanged(CartResponse response) {
                if (hasBeenCalled) return;
                hasBeenCalled = true;
                
                _isLoading.setValue(false);
                if (response != null && response.getItems() != null) {
                    _cartItems.setValue(response.getItems());
                    _totalPrice.setValue(response.getTotalPrice());
                    _isEmpty.setValue(response.getItems().isEmpty());
                    _error.setValue("");
                } else {
                    _error.setValue("Không thể cập nhật giỏ hàng");
                }
                responseLiveData.removeObserver(this);
            }
        };
        
        responseLiveData.observeForever(observer);
    }

    public void onDeleteClick(CartItem item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            return;
        }
        
        _isLoading.setValue(true);
        _error.setValue("");
        
        LiveData<CartResponse> responseLiveData = repository.removeFromCart(item.getId());
        
        Observer<CartResponse> observer = new Observer<CartResponse>() {
            private boolean hasBeenCalled = false;
            
            @Override
            public void onChanged(CartResponse response) {
                if (hasBeenCalled) return;
                hasBeenCalled = true;
                
                _isLoading.setValue(false);
                if (response != null && response.getItems() != null) {
                    _cartItems.setValue(response.getItems());
                    _totalPrice.setValue(response.getTotalPrice());
                    _isEmpty.setValue(response.getItems().isEmpty());
                    _error.setValue("");
                } else {
                    _error.setValue("Không thể xóa sản phẩm khỏi giỏ hàng");
                }
                responseLiveData.removeObserver(this);
            }
        };
        
        responseLiveData.observeForever(observer);
    }

    // ***** Nhiệm vụ 4: Xóa toàn bộ giỏ hàng *****
    public void clearCart() {
        _isLoading.setValue(true);
        _error.setValue("");
        
        LiveData<Boolean> resultLiveData = repository.clearCart();
        
        Observer<Boolean> observer = new Observer<Boolean>() {
            private boolean hasBeenCalled = false;
            
            @Override
            public void onChanged(Boolean success) {
                if (hasBeenCalled) return;
                hasBeenCalled = true;
                
                _isLoading.setValue(false);
                if (success != null && success) {
                    _cartItems.setValue(new ArrayList<>());
                    _totalPrice.setValue(0L);
                    _isEmpty.setValue(true);
                    _error.setValue("");
                } else {
                    _error.setValue("Không thể xóa giỏ hàng");
                }
                resultLiveData.removeObserver(this);
            }
        };
        
        resultLiveData.observeForever(observer);
    }
}