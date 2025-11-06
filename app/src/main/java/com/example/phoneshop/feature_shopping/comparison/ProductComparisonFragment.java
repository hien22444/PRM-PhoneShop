package com.example.phoneshop.feature_shopping.comparison;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductComparisonFragment extends Fragment {

    private NavController navController;
    private NumberFormat currencyFormat;

    // Views
    private MaterialToolbar toolbar;
    private LinearLayout layoutProductImages;
    private LinearLayout layoutProductNames;
    private LinearLayout layoutPrices;
    private LinearLayout layoutBrands;
    private LinearLayout layoutStock;
    private LinearLayout layoutRatings;
    private LinearLayout layoutEmptyState;

    // Demo products for comparison
    private List<Product> comparisonProducts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_comparison, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        bindViews(view);
        setupListeners();
        loadDemoProducts();
        displayComparison();
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        layoutProductImages = view.findViewById(R.id.layoutProductImages);
        layoutProductNames = view.findViewById(R.id.layoutProductNames);
        layoutPrices = view.findViewById(R.id.layoutPrices);
        layoutBrands = view.findViewById(R.id.layoutBrands);
        layoutStock = view.findViewById(R.id.layoutStock);
        layoutRatings = view.findViewById(R.id.layoutRatings);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
    }

    private void loadDemoProducts() {
        // Tạo demo products để so sánh
        comparisonProducts = new ArrayList<>();
        
        Product product1 = new Product();
        product1.setId("p1");
        product1.setName("iPhone 15 Pro");
        product1.setBrand("Apple");
        product1.setPrice(29990000L);
        product1.setStock(10);
        product1.setRating(4.8f);
        product1.setReviewCount(150);
        List<String> images1 = new ArrayList<>();
        images1.add("https://picsum.photos/seed/iphone15/300/300");
        product1.setImages(images1);
        
        Product product2 = new Product();
        product2.setId("p2");
        product2.setName("Samsung S24 Ultra");
        product2.setBrand("Samsung");
        product2.setPrice(31490000L);
        product2.setStock(8);
        product2.setRating(4.7f);
        product2.setReviewCount(120);
        List<String> images2 = new ArrayList<>();
        images2.add("https://picsum.photos/seed/samsung24/300/300");
        product2.setImages(images2);
        
        Product product3 = new Product();
        product3.setId("p3");
        product3.setName("Xiaomi 14 Pro");
        product3.setBrand("Xiaomi");
        product3.setPrice(18990000L);
        product3.setStock(15);
        product3.setRating(4.5f);
        product3.setReviewCount(80);
        List<String> images3 = new ArrayList<>();
        images3.add("https://picsum.photos/seed/xiaomi14/300/300");
        product3.setImages(images3);
        
        comparisonProducts.add(product1);
        comparisonProducts.add(product2);
        comparisonProducts.add(product3);
    }

    private void displayComparison() {
        if (comparisonProducts == null || comparisonProducts.size() < 2) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        layoutEmptyState.setVisibility(View.GONE);

        // Clear previous views
        layoutProductImages.removeAllViews();
        layoutProductNames.removeAllViews();
        layoutPrices.removeAllViews();
        layoutBrands.removeAllViews();
        layoutStock.removeAllViews();
        layoutRatings.removeAllViews();

        int columnWidth = 200; // Width in dp

        for (Product product : comparisonProducts) {
            // Product Images
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    dpToPx(columnWidth), dpToPx(200)
            );
            imageParams.setMargins(dpToPx(8), 0, dpToPx(8), 0);
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                Glide.with(this)
                        .load(product.getImages().get(0))
                        .into(imageView);
            }
            layoutProductImages.addView(imageView);

            // Product Names
            TextView nameView = createComparisonTextView(product.getName(), columnWidth);
            nameView.setTextSize(16);
            nameView.setTypeface(null, android.graphics.Typeface.BOLD);
            layoutProductNames.addView(nameView);

            // Prices
            TextView priceView = createComparisonTextView(
                    currencyFormat.format(product.getPrice()), columnWidth
            );
            priceView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            priceView.setTextSize(18);
            priceView.setTypeface(null, android.graphics.Typeface.BOLD);
            layoutPrices.addView(priceView);

            // Brands
            TextView brandView = createComparisonTextView(product.getBrand(), columnWidth);
            layoutBrands.addView(brandView);

            // Stock
            TextView stockView = createComparisonTextView(
                    product.isInStock() ? "Còn hàng: " + product.getStock() : "Hết hàng",
                    columnWidth
            );
            stockView.setTextColor(getResources().getColor(
                    product.isInStock() ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
            ));
            layoutStock.addView(stockView);

            // Ratings
            String ratingText = product.getRating() > 0 
                    ? String.format("⭐ %.1f (%d)", product.getRating(), product.getReviewCount())
                    : "Chưa có đánh giá";
            TextView ratingView = createComparisonTextView(ratingText, columnWidth);
            layoutRatings.addView(ratingView);
        }
    }

    private TextView createComparisonTextView(String text, int widthDp) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(widthDp), ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        return textView;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
