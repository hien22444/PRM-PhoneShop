package com.example.phoneshop.data.model;

import java.util.List;

/**
 * Request model để tạo đơn hàng từ giỏ hàng
 */
public class OrderRequest {
    private String userId;
    private CustomerInfo customerInfo;
    private String paymentMethod;
    private String shippingAddress;
    private String note;
    
    // Legacy fields for backward compatibility
    private List<OrderItemRequest> items;
    private String fullName;
    private String phone;
    private String address;

    public OrderRequest() {
    }

    public OrderRequest(List<OrderItemRequest> items, String fullName, String phone, String address, String paymentMethod) {
        this.items = items;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public static class CustomerInfo {
        private String fullName;
        private String phone;
        private String email;
        private String address;

        public CustomerInfo() {
        }

        public CustomerInfo(String fullName, String phone, String email, String address) {
            this.fullName = fullName;
            this.phone = phone;
            this.email = email;
            this.address = address;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class OrderItemRequest {
        private String productId;
        private int quantity;
        private long price;

        public OrderItemRequest() {
        }

        public OrderItemRequest(String productId, int quantity, long price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }
    }
}

