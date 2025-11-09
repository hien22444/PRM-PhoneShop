package com.example.phoneshop.data.model;

import java.util.List;

/**
 * Response model cho giỏ hàng
 */
public class CartResponse {
    private List<CartItem> items;
    private long totalPrice;
    private int itemCount;

    public CartResponse() {
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}

