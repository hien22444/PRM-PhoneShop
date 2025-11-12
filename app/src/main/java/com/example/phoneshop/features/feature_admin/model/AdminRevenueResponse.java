package com.example.phoneshop.features.feature_admin.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminRevenueResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private RevenueData data;
    
    @SerializedName("timestamp")
    private String timestamp;

    public boolean isSuccess() {
        return success;
    }

    public RevenueData getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static class RevenueData {
        @SerializedName("totalRevenue")
        private double totalRevenue;
        
        @SerializedName("completedRevenue")
        private double completedRevenue;
        
        @SerializedName("averageOrderValue")
        private double averageOrderValue;
        
        @SerializedName("revenueByPayment")
        private RevenueByPayment revenueByPayment;
        
        @SerializedName("monthlyRevenue")
        private List<MonthlyRevenue> monthlyRevenue;
        
        @SerializedName("totalOrders")
        private int totalOrders;
        
        @SerializedName("completedOrders")
        private int completedOrders;

        // Getters
        public double getTotalRevenue() { return totalRevenue; }
        public double getCompletedRevenue() { return completedRevenue; }
        public double getAverageOrderValue() { return averageOrderValue; }
        public RevenueByPayment getRevenueByPayment() { return revenueByPayment; }
        public List<MonthlyRevenue> getMonthlyRevenue() { return monthlyRevenue; }
        public int getTotalOrders() { return totalOrders; }
        public int getCompletedOrders() { return completedOrders; }
    }

    public static class RevenueByPayment {
        @SerializedName("cod")
        private double cod;
        
        @SerializedName("banking")
        private double banking;
        
        @SerializedName("ewallet")
        private double ewallet;

        public double getCod() { return cod; }
        public double getBanking() { return banking; }
        public double getEwallet() { return ewallet; }
    }

    public static class MonthlyRevenue {
        @SerializedName("month")
        private String month;
        
        @SerializedName("revenue")
        private double revenue;
        
        @SerializedName("orderCount")
        private int orderCount;

        public String getMonth() { return month; }
        public double getRevenue() { return revenue; }
        public int getOrderCount() { return orderCount; }
    }
}
