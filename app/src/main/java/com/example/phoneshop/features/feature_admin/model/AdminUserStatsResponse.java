package com.example.phoneshop.features.feature_admin.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminUserStatsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private UserStatsData data;
    
    @SerializedName("timestamp")
    private String timestamp;

    public boolean isSuccess() {
        return success;
    }

    public UserStatsData getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static class UserStatsData {
        @SerializedName("totalUsers")
        private int totalUsers;
        
        @SerializedName("newUsers")
        private int newUsers;
        
        @SerializedName("activeUsers")
        private int activeUsers;
        
        @SerializedName("usersWithOrders")
        private int usersWithOrders;
        
        @SerializedName("usersWithoutOrders")
        private int usersWithoutOrders;
        
        @SerializedName("monthlyRegistrations")
        private List<MonthlyRegistration> monthlyRegistrations;
        
        @SerializedName("topSpenders")
        private List<TopSpender> topSpenders;
        
        @SerializedName("userGrowthRate")
        private double userGrowthRate;

        // Getters
        public int getTotalUsers() { return totalUsers; }
        public int getNewUsers() { return newUsers; }
        public int getActiveUsers() { return activeUsers; }
        public int getUsersWithOrders() { return usersWithOrders; }
        public int getUsersWithoutOrders() { return usersWithoutOrders; }
        public List<MonthlyRegistration> getMonthlyRegistrations() { return monthlyRegistrations; }
        public List<TopSpender> getTopSpenders() { return topSpenders; }
        public double getUserGrowthRate() { return userGrowthRate; }
    }

    public static class MonthlyRegistration {
        @SerializedName("month")
        private String month;
        
        @SerializedName("count")
        private int count;

        public String getMonth() { return month; }
        public int getCount() { return count; }
    }

    public static class TopSpender {
        @SerializedName("userId")
        private String userId;
        
        @SerializedName("totalSpent")
        private double totalSpent;
        
        @SerializedName("orderCount")
        private int orderCount;

        public String getUserId() { return userId; }
        public double getTotalSpent() { return totalSpent; }
        public int getOrderCount() { return orderCount; }
    }
}
