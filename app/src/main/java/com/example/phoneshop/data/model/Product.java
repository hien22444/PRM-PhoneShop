package com.example.phoneshop.data.model;

import java.util.List;

public class Product {
    private String id;
    private String name;
    private String brand;
    private long price;
    private int stock;
    private boolean visible;
    private List<String> images;
    private String description;
    private String category;
    private float rating;
    private int reviewCount;
    private List<String> specifications;
    private String videoUrl; // YouTube video URL for review
    private boolean isFlashSale;
    private long flashSalePrice;
    private String flashSaleEndTime;

    // Constructor rỗng
    public Product() {
    }

    // Constructor đầy đủ
    public Product(String id, String name, String brand, long price, int stock, 
                   boolean visible, List<String> images) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.visible = visible;
        this.images = images;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<String> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<String> specifications) {
        this.specifications = specifications;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean isFlashSale() {
        return isFlashSale;
    }

    public void setFlashSale(boolean flashSale) {
        isFlashSale = flashSale;
    }

    public long getFlashSalePrice() {
        return flashSalePrice;
    }

    public void setFlashSalePrice(long flashSalePrice) {
        this.flashSalePrice = flashSalePrice;
    }

    public String getFlashSaleEndTime() {
        return flashSaleEndTime;
    }

    public void setFlashSaleEndTime(String flashSaleEndTime) {
        this.flashSaleEndTime = flashSaleEndTime;
    }

    // Helper method to get display price
    public long getDisplayPrice() {
        return isFlashSale ? flashSalePrice : price;
    }

    // Helper method to check if in stock
    public boolean isInStock() {
        return stock > 0;
    }
}
