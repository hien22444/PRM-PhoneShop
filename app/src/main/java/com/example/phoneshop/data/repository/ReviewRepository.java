package com.example.phoneshop.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.phoneshop.data.model.Review;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ReviewRepository {
    private static final String PREF_NAME = "reviews_pref";
    private static final String KEY_REVIEWS = "reviews";
    
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private final MutableLiveData<List<Review>> reviewsLiveData;
    
    public ReviewRepository(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.reviewsLiveData = new MutableLiveData<>();
        loadReviews();
    }
    
    // Thêm đánh giá mới
    public LiveData<Boolean> addReview(Review review) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        try {
            List<Review> reviews = getCurrentReviews();
            
            // Set review details
            review.setId(UUID.randomUUID().toString());
            review.setReviewDate(getCurrentDateTime());
            review.setUserId("user_123"); // Should get from session
            review.setUserName("Người dùng"); // Should get from session
            
            // Check if review already exists for this order and product
            boolean alreadyReviewed = false;
            for (Review existingReview : reviews) {
                if (existingReview.getOrderId().equals(review.getOrderId()) && 
                    existingReview.getProductId().equals(review.getProductId())) {
                    alreadyReviewed = true;
                    break;
                }
            }
            
            if (alreadyReviewed) {
                android.util.Log.w("ReviewRepository", "Product already reviewed for this order");
                result.setValue(false);
                return result;
            }
            
            reviews.add(review);
            saveReviews(reviews);
            reviewsLiveData.setValue(reviews);
            
            android.util.Log.d("ReviewRepository", "Review added successfully: " + review.getProductName());
            result.setValue(true);
            
        } catch (Exception e) {
            android.util.Log.e("ReviewRepository", "Error adding review", e);
            result.setValue(false);
        }
        
        return result;
    }
    
    // Lấy tất cả đánh giá
    public LiveData<List<Review>> getAllReviews() {
        return reviewsLiveData;
    }
    
    // Lấy đánh giá theo sản phẩm
    public LiveData<List<Review>> getReviewsByProduct(String productId) {
        MutableLiveData<List<Review>> productReviewsLiveData = new MutableLiveData<>();
        List<Review> allReviews = getCurrentReviews();
        List<Review> productReviews = new ArrayList<>();
        
        for (Review review : allReviews) {
            if (review.getProductId().equals(productId)) {
                productReviews.add(review);
            }
        }
        
        productReviewsLiveData.setValue(productReviews);
        return productReviewsLiveData;
    }
    
    // Lấy đánh giá theo user
    public LiveData<List<Review>> getReviewsByUser(String userId) {
        MutableLiveData<List<Review>> userReviewsLiveData = new MutableLiveData<>();
        List<Review> allReviews = getCurrentReviews();
        List<Review> userReviews = new ArrayList<>();
        
        for (Review review : allReviews) {
            if (review.getUserId().equals(userId)) {
                userReviews.add(review);
            }
        }
        
        userReviewsLiveData.setValue(userReviews);
        return userReviewsLiveData;
    }
    
    // Kiểm tra sản phẩm đã được đánh giá chưa
    public boolean isProductReviewed(String orderId, String productId) {
        List<Review> reviews = getCurrentReviews();
        
        for (Review review : reviews) {
            if (review.getOrderId().equals(orderId) && 
                review.getProductId().equals(productId)) {
                return true;
            }
        }
        return false;
    }
    
    // Xóa đánh giá
    public LiveData<Boolean> deleteReview(String reviewId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        try {
            List<Review> reviews = getCurrentReviews();
            reviews.removeIf(review -> review.getId().equals(reviewId));
            
            saveReviews(reviews);
            reviewsLiveData.setValue(reviews);
            
            android.util.Log.d("ReviewRepository", "Review deleted: " + reviewId);
            result.setValue(true);
            
        } catch (Exception e) {
            android.util.Log.e("ReviewRepository", "Error deleting review", e);
            result.setValue(false);
        }
        
        return result;
    }
    
    // Tính điểm trung bình cho sản phẩm
    public double getAverageRatingForProduct(String productId) {
        List<Review> reviews = getCurrentReviews();
        List<Review> productReviews = new ArrayList<>();
        
        for (Review review : reviews) {
            if (review.getProductId().equals(productId)) {
                productReviews.add(review);
            }
        }
        
        if (productReviews.isEmpty()) {
            return 0.0;
        }
        
        double totalRating = 0;
        for (Review review : productReviews) {
            totalRating += review.getRating();
        }
        
        return totalRating / productReviews.size();
    }
    
    // Load reviews từ SharedPreferences
    private void loadReviews() {
        String reviewsJson = sharedPreferences.getString(KEY_REVIEWS, "[]");
        Type type = new TypeToken<List<Review>>(){}.getType();
        List<Review> reviews = gson.fromJson(reviewsJson, type);
        
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        
        reviewsLiveData.setValue(reviews);
    }
    
    // Lưu reviews vào SharedPreferences
    private void saveReviews(List<Review> reviews) {
        String reviewsJson = gson.toJson(reviews);
        sharedPreferences.edit()
                .putString(KEY_REVIEWS, reviewsJson)
                .apply();
    }
    
    // Lấy danh sách reviews hiện tại
    private List<Review> getCurrentReviews() {
        List<Review> reviews = reviewsLiveData.getValue();
        return reviews != null ? new ArrayList<>(reviews) : new ArrayList<>();
    }
    
    // Lấy thời gian hiện tại
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}
