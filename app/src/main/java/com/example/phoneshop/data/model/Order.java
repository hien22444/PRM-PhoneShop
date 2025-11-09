package com.example.phoneshop.data.model;

public class Order {

    private String id;
    private String orderId;
    private String orderDate;
    private String status; // Ví dụ: "Đang xử lý", "Đang giao", "Hoàn thành", "Đã hủy"
    private long totalPrice;
    private int itemCount;
    private String fullName;
    private String phone;
    private String address;
    private String paymentMethod;

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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}