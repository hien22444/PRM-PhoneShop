package com.example.phoneshop.service;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.phoneshop.data.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Service để lưu trữ và quản lý đơn hàng trong SharedPreferences
 */
public class OrderStorageService {
    
    private static OrderStorageService instance;
    private static final String PREF_NAME = "PhoneShopOrders";
    private static final String KEY_ORDERS = "orders";
    
    private SharedPreferences sharedPreferences;
    private Gson gson;
    
    private OrderStorageService() {
        gson = new Gson();
    }
    
    public static synchronized OrderStorageService getInstance() {
        if (instance == null) {
            instance = new OrderStorageService();
        }
        return instance;
    }
    
    public void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Lưu đơn hàng mới
     */
    public void saveOrder(Order order) {
        if (sharedPreferences == null) {
            android.util.Log.e("OrderStorageService", "SharedPreferences not initialized");
            return;
        }
        
        List<Order> orders = getAllOrders();
        
        // Thêm đơn hàng mới vào đầu danh sách (mới nhất trước)
        orders.add(0, order);
        
        // Lưu lại vào SharedPreferences
        String ordersJson = gson.toJson(orders);
        sharedPreferences.edit().putString(KEY_ORDERS, ordersJson).apply();
        
        android.util.Log.d("OrderStorageService", "Order saved: " + order.getOrderId());
    }
    
    /**
     * Lấy tất cả đơn hàng
     */
    public List<Order> getAllOrders() {
        if (sharedPreferences == null) {
            android.util.Log.e("OrderStorageService", "SharedPreferences not initialized");
            return new ArrayList<>();
        }
        
        String ordersJson = sharedPreferences.getString(KEY_ORDERS, "");
        if (ordersJson.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            Type listType = new TypeToken<List<Order>>(){}.getType();
            List<Order> orders = gson.fromJson(ordersJson, listType);
            return orders != null ? orders : new ArrayList<>();
        } catch (Exception e) {
            android.util.Log.e("OrderStorageService", "Error parsing orders: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy đơn hàng theo ID
     */
    public Order getOrderById(String orderId) {
        List<Order> orders = getAllOrders();
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }
    
    /**
     * Cập nhật trạng thái đơn hàng
     */
    public void updateOrderStatus(String orderId, String newStatus) {
        List<Order> orders = getAllOrders();
        boolean updated = false;
        
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                order.setStatus(newStatus);
                updated = true;
                break;
            }
        }
        
        if (updated) {
            String ordersJson = gson.toJson(orders);
            sharedPreferences.edit().putString(KEY_ORDERS, ordersJson).apply();
            android.util.Log.d("OrderStorageService", "Order status updated: " + orderId + " -> " + newStatus);
        }
    }
    
    /**
     * Xóa tất cả đơn hàng (dùng để test)
     */
    public void clearAllOrders() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().remove(KEY_ORDERS).apply();
            android.util.Log.d("OrderStorageService", "All orders cleared");
        }
    }
    
    /**
     * Đếm số lượng đơn hàng
     */
    public int getOrderCount() {
        return getAllOrders().size();
    }
}
