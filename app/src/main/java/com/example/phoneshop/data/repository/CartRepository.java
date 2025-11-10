package com.example.phoneshop.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.phoneshop.data.local.LocalCartManager;
import com.example.phoneshop.data.model.CartItem;
import com.example.phoneshop.data.model.CartRequest;
import com.example.phoneshop.data.model.CartResponse;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.remote.ApiService;
import com.example.phoneshop.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    private static CartRepository instance;
    private final ApiService apiService;
    private LocalCartManager localCartManager;

    private CartRepository() {
        apiService = RetrofitClient.getInstance().getApiService();
    }

    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }
    
    public void initialize(Context context) {
        if (localCartManager == null) {
            localCartManager = LocalCartManager.getInstance(context);
        }
    }

    // Lấy giỏ hàng (local fallback)
    public LiveData<CartResponse> getCart() {
        MutableLiveData<CartResponse> data = new MutableLiveData<>();

        // Try API first, fallback to local storage
        if (localCartManager != null) {
            // Use local cart data
            CartResponse localCart = new CartResponse();
            localCart.setItems(localCartManager.getCartItems());
            localCart.setTotalPrice(localCartManager.getTotalPrice());
            localCart.setItemCount(localCartManager.getItemCount());
            data.setValue(localCart);
        } else {
            // Fallback to empty cart
            CartResponse emptyCart = new CartResponse();
            emptyCart.setItems(new ArrayList<>());
            emptyCart.setTotalPrice(0L);
            emptyCart.setItemCount(0);
            data.setValue(emptyCart);
        }

        return data;
    }

    // Thêm sản phẩm vào giỏ hàng (local)
    public LiveData<CartResponse> addProductToCart(Product product, int quantity) {
        MutableLiveData<CartResponse> data = new MutableLiveData<>();
        
        if (localCartManager != null) {
            localCartManager.addToCart(product, quantity);
            
            // Return updated cart
            CartResponse updatedCart = new CartResponse();
            updatedCart.setItems(localCartManager.getCartItems());
            updatedCart.setTotalPrice(localCartManager.getTotalPrice());
            updatedCart.setItemCount(localCartManager.getItemCount());
            data.setValue(updatedCart);
        } else {
            data.setValue(null);
        }
        
        return data;
    }

    // Thêm sản phẩm vào giỏ hàng (API - deprecated, use addProductToCart instead)
    public LiveData<CartResponse> addToCart(String productId, int quantity) {
        MutableLiveData<CartResponse> data = new MutableLiveData<>();

        CartRequest request = new CartRequest(productId, quantity);
        apiService.addToCart(request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    public LiveData<CartResponse> updateCartItem(String itemId, int quantity) {
        MutableLiveData<CartResponse> data = new MutableLiveData<>();

        CartRequest request = new CartRequest(null, quantity);
        apiService.updateCartItem(itemId, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public LiveData<CartResponse> removeFromCart(String itemId) {
        MutableLiveData<CartResponse> data = new MutableLiveData<>();

        apiService.removeFromCart(itemId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    // Update cart item quantity (local)
    public LiveData<CartResponse> updateCartItemQuantity(CartItem item, int newQuantity) {
        MutableLiveData<CartResponse> data = new MutableLiveData<>();
        
        if (localCartManager != null) {
            localCartManager.updateCartItem(item, newQuantity);
            
            // Return updated cart
            CartResponse updatedCart = new CartResponse();
            updatedCart.setItems(localCartManager.getCartItems());
            updatedCart.setTotalPrice(localCartManager.getTotalPrice());
            updatedCart.setItemCount(localCartManager.getItemCount());
            data.setValue(updatedCart);
        } else {
            data.setValue(null);
        }
        
        return data;
    }
    
    // Remove cart item (local)
    public LiveData<CartResponse> removeCartItem(CartItem item) {
        MutableLiveData<CartResponse> data = new MutableLiveData<>();
        
        if (localCartManager != null) {
            localCartManager.removeFromCart(item);
            
            // Return updated cart
            CartResponse updatedCart = new CartResponse();
            updatedCart.setItems(localCartManager.getCartItems());
            updatedCart.setTotalPrice(localCartManager.getTotalPrice());
            updatedCart.setItemCount(localCartManager.getItemCount());
            data.setValue(updatedCart);
        } else {
            data.setValue(null);
        }
        
        return data;
    }

    // Xóa toàn bộ giỏ hàng
    public LiveData<Boolean> clearCart() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();

        if (localCartManager != null) {
            localCartManager.clearCart();
            data.setValue(true);
        } else {
            data.setValue(false);
        }

        return data;
    }
}

