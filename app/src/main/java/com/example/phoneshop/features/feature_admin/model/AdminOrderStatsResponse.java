package com.example.phoneshop.features.feature_admin.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminOrderStatsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private OrderStatsData data;
    
    @SerializedName("timestamp")
    private String timestamp;

    public boolean isSuccess() {
        return success;
    }

    public OrderStatsData getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static class OrderStatsData {
        @SerializedName("totalOrders")
        private int totalOrders;
        
        @SerializedName("statusBreakdown")
        private StatusBreakdown statusBreakdown;
        
        @SerializedName("paymentBreakdown")
        private PaymentBreakdown paymentBreakdown;
        
        @SerializedName("dailyOrders")
        private List<DailyOrder> dailyOrders;
        
        @SerializedName("topCustomers")
        private List<TopCustomer> topCustomers;
        
        @SerializedName("averageOrderValue")
        private double averageOrderValue;

        // Getters
        public int getTotalOrders() { return totalOrders; }
        public StatusBreakdown getStatusBreakdown() { return statusBreakdown; }
        public PaymentBreakdown getPaymentBreakdown() { return paymentBreakdown; }
        public List<DailyOrder> getDailyOrders() { return dailyOrders; }
        public List<TopCustomer> getTopCustomers() { return topCustomers; }
        public double getAverageOrderValue() { return averageOrderValue; }
    }

    public static class StatusBreakdown {
        @SerializedName("processing")
        private int processing;
        
        @SerializedName("confirmed")
        private int confirmed;
        
        @SerializedName("shipping")
        private int shipping;
        
        @SerializedName("completed")
        private int completed;
        
        @SerializedName("cancelled")
        private int cancelled;

        public int getProcessing() { return processing; }
        public int getConfirmed() { return confirmed; }
        public int getShipping() { return shipping; }
        public int getCompleted() { return completed; }
        public int getCancelled() { return cancelled; }
    }

    public static class PaymentBreakdown {
        @SerializedName("cod")
        private int cod;
        
        @SerializedName("banking")
        private int banking;
        
        @SerializedName("ewallet")
        private int ewallet;

        public int getCod() { return cod; }
        public int getBanking() { return banking; }
        public int getEwallet() { return ewallet; }
    }

    public static class DailyOrder {
        @SerializedName("date")
        private String date;
        
        @SerializedName("count")
        private int count;
        
        @SerializedName("revenue")
        private double revenue;

        public String getDate() { return date; }
        public int getCount() { return count; }
        public double getRevenue() { return revenue; }
    }

    public static class TopCustomer {
        @SerializedName("name")
        private String name;
        
        @SerializedName("orderCount")
        private int orderCount;
        
        @SerializedName("totalSpent")
        private double totalSpent;

        public String getName() { return name; }
        public int getOrderCount() { return orderCount; }
        public double getTotalSpent() { return totalSpent; }
    }
}
