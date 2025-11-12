package com.example.phoneshop.data.model;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private final String id;
    private final String name;
    private final String brand;
    private final double price;
    private final int stock;
    private boolean visible;
    private List<String> images;

    public Product(String id, String name, String brand, double price, int stock, boolean visible, List<String> images) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.visible = visible;
        this.images = images;
    }

    public Product() {
        this.id = null; this.name = null; this.brand = null;
        this.price = 0; this.stock = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public boolean isVisible() { return visible; }
    public List<String> getImages() { return images; }

    public void setVisible(boolean visible) { this.visible = visible; }
    public void setImages(List<String> images) { this.images = images; }
}