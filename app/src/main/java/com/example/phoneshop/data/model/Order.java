package com.example.phoneshop.data.model;

import java.util.List;

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
    private String paymentUrl; // PayOS payment URL
    
    // Enhanced fields from API
    private String formattedDate;
    private String formattedTotalAmount;
    private int totalQuantity;
    private String previewImage;
    private String previewName;
    private String statusColor;
    private CustomerInfo customerInfo;
    private List<OrderItem> items;

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

    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }

    // Enhanced fields getters and setters
    public String getFormattedDate() { return formattedDate; }
    public void setFormattedDate(String formattedDate) { this.formattedDate = formattedDate; }

    public String getFormattedTotalAmount() { return formattedTotalAmount; }
    public void setFormattedTotalAmount(String formattedTotalAmount) { this.formattedTotalAmount = formattedTotalAmount; }

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

    public String getPreviewImage() { return previewImage; }
    public void setPreviewImage(String previewImage) { this.previewImage = previewImage; }

    public String getPreviewName() { return previewName; }
    public void setPreviewName(String previewName) { this.previewName = previewName; }

    public String getStatusColor() { return statusColor; }
    public void setStatusColor(String statusColor) { this.statusColor = statusColor; }

    public CustomerInfo getCustomerInfo() { return customerInfo; }
    public void setCustomerInfo(CustomerInfo customerInfo) { this.customerInfo = customerInfo; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    // Nested classes
    public static class CustomerInfo {
        private String fullName;
        private String phone;
        private String email;
        private String address;

        public CustomerInfo() {}

        public CustomerInfo(String fullName, String phone, String email, String address) {
            this.fullName = fullName;
            this.phone = phone;
            this.email = email;
            this.address = address;
        }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    public static class OrderItem {
        private String productId;
        private String name;
        private long price;
        private int quantity;
        private String image;
        private long subtotal;
        private String formattedPrice;
        private String formattedSubtotal;

        public OrderItem() {}

        public OrderItem(String productId, String name, long price, int quantity, String image) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.image = image;
            this.subtotal = price * quantity;
        }

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public long getPrice() { return price; }
        public void setPrice(long price) { this.price = price; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }

        public long getSubtotal() { return subtotal; }
        public void setSubtotal(long subtotal) { this.subtotal = subtotal; }

        public String getFormattedPrice() { return formattedPrice; }
        public void setFormattedPrice(String formattedPrice) { this.formattedPrice = formattedPrice; }

        public String getFormattedSubtotal() { return formattedSubtotal; }
        public void setFormattedSubtotal(String formattedSubtotal) { this.formattedSubtotal = formattedSubtotal; }
    }
}