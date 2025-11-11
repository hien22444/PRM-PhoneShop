package com.example.phoneshop.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.phoneshop.data.model.Favorite;
import com.example.phoneshop.data.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FavoriteRepository {
    private static final String PREF_NAME = "favorites_pref";
    private static final String KEY_FAVORITES = "favorites";
    
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private final MutableLiveData<List<Favorite>> favoritesLiveData;
    private final ProductRepository productRepository;
    
    public FavoriteRepository(Context context, ProductRepository productRepository) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.favoritesLiveData = new MutableLiveData<>();
        this.productRepository = productRepository;
        loadFavorites();
    }
    
    // Lấy danh sách yêu thích
    public LiveData<List<Favorite>> getFavorites() {
        return favoritesLiveData;
    }
    
    // Thêm sản phẩm vào yêu thích
    public void addToFavorites(String userId, Product product) {
        List<Favorite> favorites = getCurrentFavorites();
        
        // Kiểm tra xem đã có trong danh sách chưa
        for (Favorite favorite : favorites) {
            if (favorite.getProductId().equals(product.getId()) && 
                favorite.getUserId().equals(userId)) {
                return; // Đã có rồi
            }
        }
        
        // Thêm mới
        Favorite newFavorite = new Favorite(userId, product.getId());
        newFavorite.setId(UUID.randomUUID().toString());
        newFavorite.setProduct(product);
        favorites.add(newFavorite);
        
        saveFavorites(favorites);
        favoritesLiveData.setValue(favorites);
        
        android.util.Log.d("FavoriteRepository", "Added to favorites: " + product.getName());
    }
    
    // Xóa sản phẩm khỏi yêu thích
    public void removeFromFavorites(String userId, String productId) {
        List<Favorite> favorites = getCurrentFavorites();
        
        favorites.removeIf(favorite -> 
            favorite.getProductId().equals(productId) && 
            favorite.getUserId().equals(userId));
        
        saveFavorites(favorites);
        favoritesLiveData.setValue(favorites);
        
        android.util.Log.d("FavoriteRepository", "Removed from favorites: " + productId);
    }
    
    // Kiểm tra sản phẩm có trong yêu thích không
    public boolean isFavorite(String userId, String productId) {
        List<Favorite> favorites = getCurrentFavorites();
        
        for (Favorite favorite : favorites) {
            if (favorite.getProductId().equals(productId) && 
                favorite.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
    
    // Lấy danh sách yêu thích của user cụ thể
    public LiveData<List<Favorite>> getUserFavorites(String userId) {
        MutableLiveData<List<Favorite>> userFavoritesLiveData = new MutableLiveData<>();
        List<Favorite> allFavorites = getCurrentFavorites();
        List<Favorite> userFavorites = new ArrayList<>();
        
        for (Favorite favorite : allFavorites) {
            if (favorite.getUserId().equals(userId)) {
                userFavorites.add(favorite);
            }
        }
        
        userFavoritesLiveData.setValue(userFavorites);
        return userFavoritesLiveData;
    }
    
    // Xóa tất cả yêu thích của user
    public void clearUserFavorites(String userId) {
        List<Favorite> favorites = getCurrentFavorites();
        favorites.removeIf(favorite -> favorite.getUserId().equals(userId));
        
        saveFavorites(favorites);
        favoritesLiveData.setValue(favorites);
    }
    
    // Load favorites từ SharedPreferences
    private void loadFavorites() {
        String favoritesJson = sharedPreferences.getString(KEY_FAVORITES, "[]");
        Type type = new TypeToken<List<Favorite>>(){}.getType();
        List<Favorite> favorites = gson.fromJson(favoritesJson, type);
        
        if (favorites == null) {
            favorites = new ArrayList<>();
        }
        
        favoritesLiveData.setValue(favorites);
    }
    
    // Lưu favorites vào SharedPreferences
    private void saveFavorites(List<Favorite> favorites) {
        String favoritesJson = gson.toJson(favorites);
        sharedPreferences.edit()
                .putString(KEY_FAVORITES, favoritesJson)
                .apply();
    }
    
    // Lấy danh sách favorites hiện tại
    private List<Favorite> getCurrentFavorites() {
        List<Favorite> favorites = favoritesLiveData.getValue();
        return favorites != null ? new ArrayList<>(favorites) : new ArrayList<>();
    }
}
