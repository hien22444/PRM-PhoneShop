package com.example.phoneshop.features.feature_admin.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class AdminProductStatsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private ProductStatsData data;
    
    @SerializedName("timestamp")
    private String timestamp;

    public boolean isSuccess() {
        return success;
    }

    public ProductStatsData getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static class ProductStatsData {
        @SerializedName("totalProducts")
        private int totalProducts;
        
        @SerializedName("activeProducts")
        private int activeProducts;
        
        @SerializedName("inactiveProducts")
        private int inactiveProducts;
        
        @SerializedName("brandStats")
        private Map<String, Integer> brandStats;
        
        @SerializedName("priceRanges")
        private PriceRanges priceRanges;
        
        @SerializedName("topSellingProducts")
        private List<ProductSales> topSellingProducts;
        
        @SerializedName("topRevenueProducts")
        private List<ProductSales> topRevenueProducts;
        
        @SerializedName("lowStockProducts")
        private List<LowStockProduct> lowStockProducts;
        
        @SerializedName("averagePrice")
        private double averagePrice;

        // Getters
        public int getTotalProducts() { return totalProducts; }
        public int getActiveProducts() { return activeProducts; }
        public int getInactiveProducts() { return inactiveProducts; }
        public Map<String, Integer> getBrandStats() { return brandStats; }
        public PriceRanges getPriceRanges() { return priceRanges; }
        public List<ProductSales> getTopSellingProducts() { return topSellingProducts; }
        public List<ProductSales> getTopRevenueProducts() { return topRevenueProducts; }
        public List<LowStockProduct> getLowStockProducts() { return lowStockProducts; }
        public double getAveragePrice() { return averagePrice; }
    }

    public static class PriceRanges {
        @SerializedName("under5M")
        private int under5M;
        
        @SerializedName("from5to10M")
        private int from5to10M;
        
        @SerializedName("from10to20M")
        private int from10to20M;
        
        @SerializedName("over20M")
        private int over20M;

        public int getUnder5M() { return under5M; }
        public int getFrom5to10M() { return from5to10M; }
        public int getFrom10to20M() { return from10to20M; }
        public int getOver20M() { return over20M; }
    }

    public static class ProductSales {
        @SerializedName("productId")
        private String productId;
        
        @SerializedName("productName")
        private String productName;
        
        @SerializedName("totalSold")
        private int totalSold;
        
        @SerializedName("totalRevenue")
        private double totalRevenue;

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getTotalSold() { return totalSold; }
        public double getTotalRevenue() { return totalRevenue; }
    }

    public static class LowStockProduct {
        @SerializedName("id")
        private String id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("stock")
        private int stock;
        
        @SerializedName("brand")
        private String brand;

        public String getId() { return id; }
        public String getName() { return name; }
        public int getStock() { return stock; }
        public String getBrand() { return brand; }
    }
}
