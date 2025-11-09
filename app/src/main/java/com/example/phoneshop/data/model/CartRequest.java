package com.example.phoneshop.data.model;

/**
 * Request model để thêm sản phẩm vào giỏ hàng
 */
public class CartRequest {
    private String productId;
    private int quantity;

    public CartRequest() {
    }

    public CartRequest(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

