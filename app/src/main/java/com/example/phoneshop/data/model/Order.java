package com.example.phoneshop.data.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private final String id;
    private final String userName;
    private final double totalAmount;
    private String status;
    private final String date;
    private final String address;
    private final String phoneNumber;
    private final List<CartItem> items;

    public Order(String id, String userName, double totalAmount, String status, String date, String address, String phoneNumber, List<CartItem> items) {
        this.id = id;
        this.userName = userName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.date = date;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.items = items;
    }

    // Getters
    public String getId() { return id; }
    public String getDate() { return date; }
    public String getUserName() { return userName; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<CartItem> getItems() { return items; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
}