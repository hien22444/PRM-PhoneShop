package com.example.phoneshop.feature_shopping;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.feature_shopping.adapters.ImagePagerAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailFragment extends Fragment {

    private ShoppingViewModel viewModel;
    private NavController navController;
    private NumberFormat currencyFormat;
    private ImagePagerAdapter imagePagerAdapter;

    // Views
    private MaterialToolbar toolbar;
    private ViewPager2 viewPagerImages;
    private LinearLayout layoutIndicators;
    private TextView tvProductName;
    private TextView tvBrand;
    private TextView tvPrice;
    private TextView tvOriginalPrice;
    private TextView tvStock;
    private TextView tvRating;
    private TextView tvDescription;
    private TextView tvSpecifications;
    private MaterialCardView cardSpecifications;
    private MaterialCardView cardVideoReview;
    private WebView webViewVideo;
    private ProgressBar progressBar;
    private MaterialButton btnAddToCart;
    private MaterialButton btnBuyNow;

    private Product currentProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        navController = Navigation.findNavController(view);
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        bindViews(view);
        setupListeners();
        observeViewModel();

        // Get product ID from arguments
        if (getArguments() != null) {
            String productId = getArguments().getString("product_id");
            if (productId != null) {
                viewModel.loadProductDetail(productId);
            }
        }
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        viewPagerImages = view.findViewById(R.id.viewPagerImages);
        layoutIndicators = view.findViewById(R.id.layoutIndicators);
        tvProductName = view.findViewById(R.id.tvProductName);
        tvBrand = view.findViewById(R.id.tvBrand);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvOriginalPrice = view.findViewById(R.id.tvOriginalPrice);
        tvStock = view.findViewById(R.id.tvStock);
        tvRating = view.findViewById(R.id.tvRating);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvSpecifications = view.findViewById(R.id.tvSpecifications);
        cardSpecifications = view.findViewById(R.id.cardSpecifications);
        cardVideoReview = view.findViewById(R.id.cardVideoReview);
        webViewVideo = view.findViewById(R.id.webViewVideo);
        progressBar = view.findViewById(R.id.progressBar);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
        btnBuyNow = view.findViewById(R.id.btnBuyNow);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.isInStock()) {
                Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                // TODO: Implement add to cart
            } else {
                Toast.makeText(getContext(), "Sản phẩm hết hàng", Toast.LENGTH_SHORT).show();
            }
        });

        btnBuyNow.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.isInStock()) {
                Toast.makeText(getContext(), "Chuyển đến thanh toán", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to checkout
            } else {
                Toast.makeText(getContext(), "Sản phẩm hết hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getSelectedProduct().observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                currentProduct = product;
                displayProductDetails(product);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProductDetails(Product product) {
        // Product name and brand
        tvProductName.setText(product.getName());
        tvBrand.setText(product.getBrand());

        // Price
        tvPrice.setText(currencyFormat.format(product.getDisplayPrice()));
        
        if (product.isFlashSale()) {
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.setText(currencyFormat.format(product.getPrice()));
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
        }

        // Stock
        if (product.isInStock()) {
            tvStock.setText("Còn hàng: " + product.getStock());
            tvStock.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnAddToCart.setEnabled(true);
            btnBuyNow.setEnabled(true);
        } else {
            tvStock.setText("Hết hàng");
            tvStock.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnAddToCart.setEnabled(false);
            btnBuyNow.setEnabled(false);
        }

        // Rating
        if (product.getRating() > 0) {
            tvRating.setText(String.format("⭐ %.1f (%d đánh giá)", 
                product.getRating(), product.getReviewCount()));
        } else {
            tvRating.setText("Chưa có đánh giá");
        }

        // Description
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvDescription.setText(product.getDescription());
        } else {
            tvDescription.setText("Đây là sản phẩm " + product.getName() + " của thương hiệu " + 
                product.getBrand() + ". Sản phẩm chính hãng, bảo hành đầy đủ.");
        }

        // Specifications
        if (product.getSpecifications() != null && !product.getSpecifications().isEmpty()) {
            cardSpecifications.setVisibility(View.VISIBLE);
            StringBuilder specs = new StringBuilder();
            for (String spec : product.getSpecifications()) {
                specs.append("• ").append(spec).append("\n");
            }
            tvSpecifications.setText(specs.toString());
        } else {
            cardSpecifications.setVisibility(View.GONE);
        }

        // Video Review (YouTube embed)
        if (product.getVideoUrl() != null && !product.getVideoUrl().isEmpty()) {
            cardVideoReview.setVisibility(View.VISIBLE);
            loadYouTubeVideo(product.getVideoUrl());
        } else {
            cardVideoReview.setVisibility(View.GONE);
        }

        // Images
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            setupImagePager(product.getImages());
        }
    }

    private void setupImagePager(java.util.List<String> images) {
        imagePagerAdapter = new ImagePagerAdapter(getContext(), images);
        viewPagerImages.setAdapter(imagePagerAdapter);

        // Setup indicators
        setupIndicators(images.size());
        
        viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
            }
        });
    }

    private void setupIndicators(int count) {
        layoutIndicators.removeAllViews();
        
        for (int i = 0; i < count; i++) {
            View indicator = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    24, 24
            );
            params.setMargins(8, 0, 8, 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(android.R.drawable.presence_invisible);
            layoutIndicators.addView(indicator);
        }
        
        if (count > 0) {
            updateIndicators(0);
        }
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < layoutIndicators.getChildCount(); i++) {
            View indicator = layoutIndicators.getChildAt(i);
            if (i == position) {
                indicator.setBackgroundResource(android.R.drawable.presence_online);
            } else {
                indicator.setBackgroundResource(android.R.drawable.presence_invisible);
            }
        }
    }

    private void loadYouTubeVideo(String videoUrl) {
        webViewVideo.getSettings().setJavaScriptEnabled(true);
        
        // Extract video ID from YouTube URL
        String videoId = extractYouTubeVideoId(videoUrl);
        if (videoId != null) {
            String html = "<iframe width=\"100%\" height=\"100%\" " +
                    "src=\"https://www.youtube.com/embed/" + videoId + "\" " +
                    "frameborder=\"0\" allowfullscreen></iframe>";
            webViewVideo.loadData(html, "text/html", "utf-8");
        }
    }

    private String extractYouTubeVideoId(String url) {
        // Simple extraction - can be improved
        if (url.contains("youtube.com/watch?v=")) {
            return url.split("v=")[1].split("&")[0];
        } else if (url.contains("youtu.be/")) {
            return url.split("youtu.be/")[1].split("\\?")[0];
        }
        return null;
    }
}

