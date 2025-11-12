package com.example.phoneshop.features.feature_admin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Admin Dashboard Statistics Response Model
 */
public class AdminStatsResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private StatsData data;
    
    @SerializedName("timestamp")
    private String timestamp;

    public AdminStatsResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public StatsData getData() {
        return data;
    }

    public void setData(StatsData data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Statistics Data nested class
     */
    public static class StatsData {
        @SerializedName("overview")
        private Overview overview;
        
        @SerializedName("ordersByStatus")
        private OrdersByStatus ordersByStatus;
        
        @SerializedName("recentOrders")
        private List<RecentOrder> recentOrders;
        
        @SerializedName("monthlyRevenue")
        private List<MonthlyRevenue> monthlyRevenue;
        
        @SerializedName("topProducts")
        private List<TopProduct> topProducts;
        
        @SerializedName("systemInfo")
        private SystemInfo systemInfo;

        // Getters and Setters
        public Overview getOverview() {
            return overview;
        }

        public void setOverview(Overview overview) {
            this.overview = overview;
        }

        public OrdersByStatus getOrdersByStatus() {
            return ordersByStatus;
        }

        public void setOrdersByStatus(OrdersByStatus ordersByStatus) {
            this.ordersByStatus = ordersByStatus;
        }

        public List<RecentOrder> getRecentOrders() {
            return recentOrders;
        }

        public void setRecentOrders(List<RecentOrder> recentOrders) {
            this.recentOrders = recentOrders;
        }

        public List<MonthlyRevenue> getMonthlyRevenue() {
            return monthlyRevenue;
        }

        public void setMonthlyRevenue(List<MonthlyRevenue> monthlyRevenue) {
            this.monthlyRevenue = monthlyRevenue;
        }

        public List<TopProduct> getTopProducts() {
            return topProducts;
        }

        public void setTopProducts(List<TopProduct> topProducts) {
            this.topProducts = topProducts;
        }

        public SystemInfo getSystemInfo() {
            return systemInfo;
        }

        public void setSystemInfo(SystemInfo systemInfo) {
            this.systemInfo = systemInfo;
        }
    }

    /**
     * Overview Statistics
     */
    public static class Overview {
        @SerializedName("totalUsers")
        private int totalUsers;
        
        @SerializedName("totalProducts")
        private int totalProducts;
        
        @SerializedName("totalOrders")
        private int totalOrders;
        
        @SerializedName("totalRevenue")
        private long totalRevenue;
        
        @SerializedName("averageOrderValue")
        private long averageOrderValue;

        // Getters and Setters
        public int getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
        }

        public int getTotalProducts() {
            return totalProducts;
        }

        public void setTotalProducts(int totalProducts) {
            this.totalProducts = totalProducts;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
        }

        public long getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(long totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public long getAverageOrderValue() {
            return averageOrderValue;
        }

        public void setAverageOrderValue(long averageOrderValue) {
            this.averageOrderValue = averageOrderValue;
        }
    }

    /**
     * Orders by Status
     */
    public static class OrdersByStatus {
        @SerializedName("Đang xử lý")
        private int processing;
        
        @SerializedName("Đã xác nhận")
        private int confirmed;
        
        @SerializedName("Đang giao")
        private int shipping;
        
        @SerializedName("Hoàn thành")
        private int completed;
        
        @SerializedName("Đã hủy")
        private int cancelled;

        // Getters and Setters
        public int getProcessing() {
            return processing;
        }

        public void setProcessing(int processing) {
            this.processing = processing;
        }

        public int getConfirmed() {
            return confirmed;
        }

        public void setConfirmed(int confirmed) {
            this.confirmed = confirmed;
        }

        public int getShipping() {
            return shipping;
        }

        public void setShipping(int shipping) {
            this.shipping = shipping;
        }

        public int getCompleted() {
            return completed;
        }

        public void setCompleted(int completed) {
            this.completed = completed;
        }

        public int getCancelled() {
            return cancelled;
        }

        public void setCancelled(int cancelled) {
            this.cancelled = cancelled;
        }
    }

    /**
     * Recent Order
     */
    public static class RecentOrder {
        @SerializedName("id")
        private String id;
        
        @SerializedName("customerName")
        private String customerName;
        
        @SerializedName("totalAmount")
        private long totalAmount;
        
        @SerializedName("status")
        private String status;
        
        @SerializedName("createdAt")
        private String createdAt;
        
        @SerializedName("itemCount")
        private int itemCount;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public long getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(long totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public int getItemCount() {
            return itemCount;
        }

        public void setItemCount(int itemCount) {
            this.itemCount = itemCount;
        }
    }

    /**
     * Monthly Revenue
     */
    public static class MonthlyRevenue {
        @SerializedName("month")
        private String month;
        
        @SerializedName("revenue")
        private long revenue;

        // Getters and Setters
        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public long getRevenue() {
            return revenue;
        }

        public void setRevenue(long revenue) {
            this.revenue = revenue;
        }
    }

    /**
     * Top Product
     */
    public static class TopProduct {
        @SerializedName("productId")
        private String productId;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("totalQuantity")
        private int totalQuantity;
        
        @SerializedName("totalRevenue")
        private long totalRevenue;

        // Getters and Setters
        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(int totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public long getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(long totalRevenue) {
            this.totalRevenue = totalRevenue;
        }
    }

    /**
     * System Info
     */
    public static class SystemInfo {
        @SerializedName("serverUptime")
        private double serverUptime;
        
        @SerializedName("dataFile")
        private String dataFile;
        
        @SerializedName("lastBackup")
        private String lastBackup;

        // Getters and Setters
        public double getServerUptime() {
            return serverUptime;
        }

        public void setServerUptime(double serverUptime) {
            this.serverUptime = serverUptime;
        }

        public String getDataFile() {
            return dataFile;
        }

        public void setDataFile(String dataFile) {
            this.dataFile = dataFile;
        }

        public String getLastBackup() {
            return lastBackup;
        }

        public void setLastBackup(String lastBackup) {
            this.lastBackup = lastBackup;
        }
    }
}
