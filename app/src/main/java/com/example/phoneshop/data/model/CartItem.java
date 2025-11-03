package com.example.phoneshop.data.model;

/**
 * Đây là lớp Model đại diện cho một sản phẩm NẰM TRONG GIỎ HÀNG.
 * Nó khác với Product.java (sản phẩm chung của shop).
 */
public class CartItem {

    private String productId;
    private String productName;
    private long price; // Dùng 'long' để lưu giá tiền (VND), an toàn hơn float/double
    private int quantity; // Số lượng
    private String imageUrl;

    // Constructor rỗng (cần cho Firebase/Room)
    public CartItem() {
    }

    // Constructor đầy đủ (dùng để tạo đối tượng khi thêm vào giỏ)
    public CartItem(String productId, String productName, long price, int quantity, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // --- Bắt đầu Getters và Setters ---
    // (Cần thiết để các lớp khác truy cập vào thuộc tính private)

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}