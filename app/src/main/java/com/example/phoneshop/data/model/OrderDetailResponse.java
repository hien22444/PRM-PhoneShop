package com.example.phoneshop.data.model;

public class OrderDetailResponse {
    private boolean success;
    private String message;
    private Order order;

    public OrderDetailResponse() {}

    public OrderDetailResponse(boolean success, String message, Order order) {
        this.success = success;
        this.message = message;
        this.order = order;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
