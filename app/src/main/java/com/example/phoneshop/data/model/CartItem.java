package com.example.phoneshop.data.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private final String productId;
    private final String productName;
    private final int quantity;
    private final double price;
    private final String imageUrl; // Ví dụ: Có thể null nếu không cần

    // Constructor phải khớp với cách bạn tạo đối tượng trong Repository
    public CartItem(String productId, String productName, int quantity, double price, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Constructor trống (cần cho Gson/Firebase)
    public CartItem() {
        this.productId = null;
        this.productName = null;
        this.quantity = 0;
        this.price = 0;
        this.imageUrl = null;
    }

    // Getters
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}