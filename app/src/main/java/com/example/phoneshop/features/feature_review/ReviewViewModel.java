package com.example.phoneshop.features.feature_review;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.repository.OrderRepository;
import com.example.phoneshop.features.feature_review.adapters.ReviewProductAdapter.ReviewInput;

import java.util.ArrayList;
import java.util.List;

public class ReviewViewModel extends ViewModel {

    private OrderRepository orderRepository;

    // LiveData cho Fragment quan sát
    private final MutableLiveData<Order> order = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> orderProducts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> submitResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ReviewViewModel() {
        this.orderRepository = OrderRepository.getInstance();
    }

    // ==========================================================
    // CÁC HÀM GETTER ĐỂ FRAGMENT QUAN SÁT
    // ==========================================================

    public LiveData<Order> getOrder() {
        return order;
    }

    public LiveData<List<Product>> getOrderProducts() {
        return orderProducts;
    }

    public LiveData<Boolean> getSubmitResult() {
        return submitResult;
    }

    public LiveData<String> getError() {
        return error;
    }

    // ==========================================================
    // CÁC HÀM LOGIC MÀ FRAGMENT GỌI
    // ==========================================================

    /**
     * Lấy thông tin cơ bản của đơn hàng
     */
    public void loadOrderForReview(String orderId) {
        // TODO: Chỗ này bạn sẽ gọi API qua Repository
        // Ví dụ:
        // Order data = orderRepository.getOrderDetails(orderId);
        // order.setValue(data);

        // --- Code giả định ---
        // Giả sử lấy được đơn hàng thành công
        Order mockOrder = new Order();
        mockOrder.setOrderId(orderId);
        order.setValue(mockOrder);
        // --------------------
    }

    /**
     * Lấy danh sách sản phẩm trong đơn hàng để đánh giá
     */
    public void loadOrderProducts(String orderId) {
        // Tạo mock data để test
        List<Product> mockProducts = new ArrayList<>();
        
        // Mock product 1
        Product product1 = new Product();
        product1.setId("1");
        product1.setName("iPhone 15 Pro Max");
        product1.setPrice(29990000);
        List<String> images1 = new ArrayList<>();
        images1.add("https://example.com/iphone15.jpg");
        product1.setImages(images1);
        
        // Mock product 2
        Product product2 = new Product();
        product2.setId("2");
        product2.setName("Samsung Galaxy S24");
        product2.setPrice(22990000);
        List<String> images2 = new ArrayList<>();
        images2.add("https://example.com/galaxy.jpg");
        product2.setImages(images2);
        
        mockProducts.add(product1);
        mockProducts.add(product2);
        
        orderProducts.setValue(mockProducts);
    }

    /**
     * Gửi tất cả đánh giá lên server
     */
    public void submitReviews(String orderId, List<ReviewInput> reviews) {

        // TODO: Chỗ này bạn sẽ gọi API qua Repository

        // Ví dụ (dùng Retrofit):
        /*
        Call<Void> call = orderRepository.submitReviews(orderId, reviews);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    submitResult.setValue(true); // Gửi thành công
                } else {
                    submitResult.setValue(false); // Gửi thất bại
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                submitResult.setValue(false);
                error.setValue(t.getMessage());
            }
        });
        */

        // --- Code giả định ---
        // Giả sử gửi thành công
        submitResult.setValue(true);
        // --------------------

        // Nếu thất bại:
        // submitResult.setValue(false);
        // error.setValue("Gửi đánh giá thất bại, vui lòng thử lại.");
    }
}