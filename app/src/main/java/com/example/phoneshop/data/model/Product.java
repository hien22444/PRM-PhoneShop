package com.example.phoneshop.data.model;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String id;
    private String name;
    private String description;
    private String brand;
    private String category;
    private long price;
    private int stock;
    private boolean visible;
    private boolean flashSale;
    private long flashSalePrice;
    private float rating;
    private int reviewCount;
    private List<String> images;
    private String image;
    private String imageUrl; // fallback cho các adapter cũ

    public Product() {}

    // -------- Getter & Setter --------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isFlashSale() { return flashSale; }
    public void setFlashSale(boolean flashSale) { this.flashSale = flashSale; }

    public long getFlashSalePrice() { return flashSalePrice; }
    public void setFlashSalePrice(long flashSalePrice) { this.flashSalePrice = flashSalePrice; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getImageUrl() {
        // fallback — nếu imageUrl null thì lấy ảnh đầu tiên trong danh sách
        if (imageUrl != null) return imageUrl;
        if (images != null && !images.isEmpty()) return images.get(0);
        return image;
    }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // -------- Logic Helper Methods --------
    public boolean isInStock() {
        return stock > 0;
    }

    public long getDisplayPrice() {
        return flashSale ? flashSalePrice : price;
    }
}
