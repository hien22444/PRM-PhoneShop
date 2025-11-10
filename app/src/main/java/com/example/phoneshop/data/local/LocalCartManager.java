package com.example.phoneshop.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.phoneshop.data.model.CartItem;
import com.example.phoneshop.data.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Local cart manager using SharedPreferences to store cart data locally
 */
public class LocalCartManager {
    private static final String PREF_NAME = "cart_preferences";
    private static final String KEY_CART_ITEMS = "cart_items";
    
    private static LocalCartManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    
    private LocalCartManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    public static synchronized LocalCartManager getInstance(Context context) {
        if (instance == null) {
            instance = new LocalCartManager(context);
        }
        return instance;
    }
    
    /**
     * Add product to cart
     */
    public void addToCart(Product product, int quantity) {
        List<CartItem> cartItems = getCartItems();
        
        // Check if product already exists in cart
        CartItem existingItem = findCartItemByProductId(cartItems, product.getId());
        
        if (existingItem != null) {
            // Update quantity if product already exists
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem();
            newItem.setId(UUID.randomUUID().toString());
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setPrice(product.getDisplayPrice());
            newItem.setQuantity(quantity);
            newItem.setProduct(product);
            
            // Set image URL (use first image if available)
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                newItem.setImageUrl(product.getImages().get(0));
            }
            
            cartItems.add(newItem);
        }
        
        saveCartItems(cartItems);
    }
    
    /**
     * Get all cart items
     */
    public List<CartItem> getCartItems() {
        String json = preferences.getString(KEY_CART_ITEMS, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        
        Type listType = new TypeToken<List<CartItem>>(){}.getType();
        List<CartItem> items = gson.fromJson(json, listType);
        return items != null ? items : new ArrayList<>();
    }
    
    /**
     * Update cart item quantity
     */
    public void updateCartItem(String itemId, int newQuantity) {
        List<CartItem> cartItems = getCartItems();
        
        for (CartItem item : cartItems) {
            if (item.getId().equals(itemId)) {
                if (newQuantity <= 0) {
                    cartItems.remove(item);
                } else {
                    item.setQuantity(newQuantity);
                }
                break;
            }
        }
        
        saveCartItems(cartItems);
    }
    
    /**
     * Update cart item quantity (overloaded for CartItem object)
     */
    public void updateCartItem(CartItem targetItem, int newQuantity) {
        List<CartItem> cartItems = getCartItems();
        
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item.getProductId().equals(targetItem.getProductId()) || 
                (item.getId() != null && item.getId().equals(targetItem.getId()))) {
                if (newQuantity <= 0) {
                    cartItems.remove(i);
                } else {
                    item.setQuantity(newQuantity);
                }
                break;
            }
        }
        
        saveCartItems(cartItems);
    }
    
    /**
     * Remove item from cart
     */
    public void removeFromCart(String itemId) {
        List<CartItem> cartItems = getCartItems();
        cartItems.removeIf(item -> item.getId().equals(itemId));
        saveCartItems(cartItems);
    }
    
    /**
     * Remove item from cart (overloaded for CartItem object)
     */
    public void removeFromCart(CartItem targetItem) {
        List<CartItem> cartItems = getCartItems();
        cartItems.removeIf(item -> 
            item.getProductId().equals(targetItem.getProductId()) || 
            (item.getId() != null && item.getId().equals(targetItem.getId())));
        saveCartItems(cartItems);
    }
    
    /**
     * Clear all cart items
     */
    public void clearCart() {
        preferences.edit().remove(KEY_CART_ITEMS).apply();
    }
    
    /**
     * Get total price of all items in cart
     */
    public long getTotalPrice() {
        List<CartItem> cartItems = getCartItems();
        long total = 0;
        
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        
        return total;
    }
    
    /**
     * Get total number of items in cart
     */
    public int getItemCount() {
        List<CartItem> cartItems = getCartItems();
        int count = 0;
        
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        
        return count;
    }
    
    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return getCartItems().isEmpty();
    }
    
    /**
     * Save cart items to SharedPreferences
     */
    private void saveCartItems(List<CartItem> cartItems) {
        String json = gson.toJson(cartItems);
        preferences.edit().putString(KEY_CART_ITEMS, json).apply();
    }
    
    /**
     * Find cart item by product ID
     */
    private CartItem findCartItemByProductId(List<CartItem> cartItems, String productId) {
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                return item;
            }
        }
        return null;
    }
}
