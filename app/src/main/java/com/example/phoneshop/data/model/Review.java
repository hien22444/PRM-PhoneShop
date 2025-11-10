package com.example.phoneshop.data.model;

/**
 * Model cho đánh giá sản phẩm
 */
public class Review {
    private String id;
    private String orderId;
    private String productId;
    private String productName;
    private String userId;
    private String userName;
    private int rating; // 1-5 sao
    private String comment;
    private String reviewDate;
    private String imageUrl; // Ảnh sản phẩm

    public Review() {}

    public Review(String orderId, String productId, String productName, int rating, String comment) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
