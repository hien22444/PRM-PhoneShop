package com.example.phoneshop.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class DashboardStats implements Serializable {

    @SerializedName("total_users")
    private int totalUsers;

    @SerializedName("total_orders")
    private int totalOrders;

    @SerializedName("total_revenue")
    private double totalRevenue;

    @SerializedName("pending_orders")
    private int pendingOrders;

    @SerializedName("monthly_revenue_data")
    private List<RevenueData> monthlyRevenueData;


    // INNER CLASS CHO DỮ LIỆU BIỂU ĐỒ (RevenueData)
    public static class RevenueData implements Serializable {
        @SerializedName("month")
        private String month;

        @SerializedName("revenue_amount")
        private float revenueAmount;

        // Getters
        public String getMonth() { return month; }
        public float getRevenueAmount() { return revenueAmount; }

        // Setters (Cần thiết cho Mock Data)
        public void setMonth(String month) { this.month = month; }
        public void setRevenueAmount(float revenueAmount) { this.revenueAmount = revenueAmount; }
    }

    // ================== GETTERS ==================
    public int getTotalUsers() { return totalUsers; }
    public int getTotalOrders() { return totalOrders; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getPendingOrders() { return pendingOrders; }
    public List<RevenueData> getMonthlyRevenueData() { return monthlyRevenueData; }

    // ================== SETTERS (Cần thiết cho Mock Data) ==================
    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public void setPendingOrders(int pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public void setMonthlyRevenueData(List<RevenueData> monthlyRevenueData) {
        this.monthlyRevenueData = monthlyRevenueData;
    }
}