package com.example.phoneshop.data.model;

public class Order {

    private String orderId;
    private String orderDate;
    private String status; // Ví dụ: "Đang xử lý", "Đang giao", "Hoàn thành", "Đã hủy"
    private long totalPrice;
    private int itemCount;

    // Constructor rỗng
    public Order() {
    }

    // Constructor đầy đủ
    public Order(String orderId, String orderDate, String status, long totalPrice, int itemCount) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.itemCount = itemCount;
    }

    // --- Getters and Setters ---
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTotalPrice() { return totalPrice; }
    public void setTotalPrice(long totalPrice) { this.totalPrice = totalPrice; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
}