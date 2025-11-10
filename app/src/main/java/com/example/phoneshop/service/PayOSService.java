package com.example.phoneshop.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.phoneshop.config.PayOSConfig;
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.data.model.CartItem;

import java.util.List;
import java.util.UUID;

/**
 * PayOS Service để xử lý thanh toán
 */
public class PayOSService {
    
    private static PayOSService instance;
    private Context context;
    
    private PayOSService() {}
    
    public static PayOSService getInstance() {
        if (instance == null) {
            instance = new PayOSService();
        }
        return instance;
    }
    
    public void initialize(Context context) {
        this.context = context;
    }
    
    /**
     * Tạo payment link với PayOS
     */
    public LiveData<String> createPaymentLink(Order order, List<CartItem> cartItems) {
        MutableLiveData<String> paymentUrlLiveData = new MutableLiveData<>();
        
        // Tạo payment request
        PaymentRequest paymentRequest = createPaymentRequest(order, cartItems);
        
        // Simulate API call to PayOS
        // Trong thực tế, bạn sẽ gọi API PayOS ở đây
        simulatePayOSApiCall(paymentRequest, paymentUrlLiveData);
        
        return paymentUrlLiveData;
    }
    
    private PaymentRequest createPaymentRequest(Order order, List<CartItem> cartItems) {
        PaymentRequest request = new PaymentRequest();
        
        // Generate unique order code
        String orderCode = generateOrderCode();
        request.setOrderCode(orderCode);
        request.setAmount((int) order.getTotalPrice());
        request.setDescription("Thanh toán đơn hàng #" + order.getOrderId());
        request.setReturnUrl(PayOSConfig.RETURN_URL_SUCCESS);
        request.setCancelUrl(PayOSConfig.RETURN_URL_CANCEL);
        
        // Set buyer info
        request.setBuyerName(order.getFullName());
        request.setBuyerPhone(order.getPhone());
        request.setBuyerAddress(order.getAddress());
        
        return request;
    }
    
    private void simulatePayOSApiCall(PaymentRequest request, MutableLiveData<String> paymentUrlLiveData) {
        // Simulate network delay
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate 1 second delay
                
                // Create real PayOS-like URL with QR code
                String paymentUrl = "https://pay.payos.vn/web/" + request.getOrderCode() + 
                                   "?amount=" + request.getAmount() + 
                                   "&description=" + java.net.URLEncoder.encode(request.getDescription(), "UTF-8");
                
                android.util.Log.d("PayOSService", "Generated payment URL: " + paymentUrl);
                
                // Post result on main thread using Handler
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
                    paymentUrlLiveData.setValue(paymentUrl);
                });
                
            } catch (Exception e) {
                android.util.Log.e("PayOSService", "Error creating payment URL: " + e.getMessage());
                
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
                    paymentUrlLiveData.setValue(null);
                });
            }
        }).start();
    }
    
    private String generateOrderCode() {
        return "ORDER_" + System.currentTimeMillis();
    }
    
    /**
     * PayOS Payment Request model
     */
    public static class PaymentRequest {
        private String orderCode;
        private int amount;
        private String description;
        private String returnUrl;
        private String cancelUrl;
        private String buyerName;
        private String buyerPhone;
        private String buyerAddress;
        
        // Getters and Setters
        public String getOrderCode() { return orderCode; }
        public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
        
        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getReturnUrl() { return returnUrl; }
        public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
        
        public String getCancelUrl() { return cancelUrl; }
        public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
        
        public String getBuyerName() { return buyerName; }
        public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
        
        public String getBuyerPhone() { return buyerPhone; }
        public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }
        
        public String getBuyerAddress() { return buyerAddress; }
        public void setBuyerAddress(String buyerAddress) { this.buyerAddress = buyerAddress; }
    }
}
