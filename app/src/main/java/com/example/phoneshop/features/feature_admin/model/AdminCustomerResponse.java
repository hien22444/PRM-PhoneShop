package com.example.phoneshop.features.feature_admin.model;

import com.example.phoneshop.data.model.Order;
import com.google.gson.annotations.SerializedName;

public class AdminCustomerResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private CustomerData data;

    public boolean isSuccess() {
        return success;
    }

    public CustomerData getData() {
        return data;
    }

    public static class CustomerData {
        @SerializedName("customer")
        private CustomerInfo customer;
        
        @SerializedName("currentOrder")
        private Order currentOrder;

        public CustomerInfo getCustomer() {
            return customer;
        }

        public Order getCurrentOrder() {
            return currentOrder;
        }
    }

    public static class CustomerInfo {
        @SerializedName("id")
        private String id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("phone")
        private String phone;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("address")
        private String address;
        
        @SerializedName("isGuest")
        private boolean isGuest;
        
        @SerializedName("totalOrders")
        private int totalOrders;
        
        @SerializedName("totalSpent")
        private double totalSpent;
        
        @SerializedName("formattedTotalSpent")
        private String formattedTotalSpent;
        
        @SerializedName("registrationDate")
        private String registrationDate;
        
        @SerializedName("lastOrderDate")
        private Long lastOrderDate;

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }
        public boolean isGuest() { return isGuest; }
        public int getTotalOrders() { return totalOrders; }
        public double getTotalSpent() { return totalSpent; }
        public String getFormattedTotalSpent() { return formattedTotalSpent; }
        public String getRegistrationDate() { return registrationDate; }
        public Long getLastOrderDate() { return lastOrderDate; }
    }
}
