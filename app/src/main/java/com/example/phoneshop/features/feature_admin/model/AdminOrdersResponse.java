package com.example.phoneshop.features.feature_admin.model;

import com.example.phoneshop.data.model.Order;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminOrdersResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private OrdersData data;
    
    @SerializedName("timestamp")
    private String timestamp;

    public boolean isSuccess() {
        return success;
    }

    public OrdersData getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static class OrdersData {
        @SerializedName("orders")
        private List<EnrichedOrder> orders;
        
        @SerializedName("pagination")
        private Pagination pagination;

        public List<EnrichedOrder> getOrders() {
            return orders;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class EnrichedOrder extends Order {
        @SerializedName("itemCount")
        private int itemCount;
        
        @SerializedName("formattedDate")
        private String formattedDate;
        
        @SerializedName("formattedPrice")
        private String formattedPrice;
        
        @SerializedName("customerName")
        private String customerName;

        public int getItemCount() {
            return itemCount;
        }

        public String getFormattedDate() {
            return formattedDate;
        }

        public String getFormattedPrice() {
            return formattedPrice;
        }
        
        public String getCustomerName() {
            // Return customerName if available, otherwise fallback to fullName
            return customerName != null ? customerName : getFullName();
        }

        public void setItemCount(int itemCount) {
            this.itemCount = itemCount;
        }

        public void setFormattedDate(String formattedDate) {
            this.formattedDate = formattedDate;
        }

        public void setFormattedPrice(String formattedPrice) {
            this.formattedPrice = formattedPrice;
        }
        
        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
    }

    public static class Pagination {
        @SerializedName("currentPage")
        private int currentPage;
        
        @SerializedName("pageSize")
        private int pageSize;
        
        @SerializedName("totalItems")
        private int totalItems;
        
        @SerializedName("totalPages")
        private int totalPages;

        public int getCurrentPage() {
            return currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }
}
