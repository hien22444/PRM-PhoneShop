package com.example.phoneshop.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.phoneshop.data.model.CartItem;
import com.example.phoneshop.data.model.CartRequest;
import com.example.phoneshop.data.model.CartResponse;
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

    private CartRepository() {
        apiService = RetrofitClient.getInstance().getApiService();
    }

    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }

    // Lấy giỏ hàng
    public LiveData<CartResponse> getCart() {
        MutableLiveData<CartResponse> data = new MutableLiveData<>();

        apiService.getCart().enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    // Nếu giỏ hàng trống, trả về giỏ hàng rỗng
                    CartResponse emptyCart = new CartResponse();
                    emptyCart.setItems(new ArrayList<>());
                    emptyCart.setTotalPrice(0L);
                    emptyCart.setItemCount(0);
                    data.setValue(emptyCart);
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                // Nếu lỗi, trả về giỏ hàng rỗng
                CartResponse emptyCart = new CartResponse();
                emptyCart.setItems(new ArrayList<>());
                emptyCart.setTotalPrice(0L);
                emptyCart.setItemCount(0);
                data.setValue(emptyCart);
            }
        });

        return data;
    }

    // Thêm sản phẩm vào giỏ hàng
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

    // Xóa toàn bộ giỏ hàng
    public LiveData<Boolean> clearCart() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();

        apiService.clearCart().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                data.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                data.setValue(false);
            }
        });

        return data;
    }
}

