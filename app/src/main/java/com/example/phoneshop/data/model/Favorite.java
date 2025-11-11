package com.example.phoneshop.data.model;

public class Favorite {
    private String id;
    private String userId;
    private String productId;
    private Product product; // Để hiển thị thông tin sản phẩm
    private long createdAt;

    public Favorite() {
        this.createdAt = System.currentTimeMillis();
    }

    public Favorite(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
        this.createdAt = System.currentTimeMillis();
    }

    public Favorite(String id, String userId, String productId, Product product, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.product = product;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Favorite favorite = (Favorite) obj;
        return productId != null ? productId.equals(favorite.productId) : favorite.productId == null;
    }

    @Override
    public int hashCode() {
        return productId != null ? productId.hashCode() : 0;
    }
}
