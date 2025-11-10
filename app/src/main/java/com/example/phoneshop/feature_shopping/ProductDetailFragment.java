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
    // private MaterialToolbar toolbar; // Removed
    // private ViewPager2 viewPagerImages; // Removed
    // private LinearLayout layoutIndicators; // Removed
    private TextView tvProductName;
    // private TextView tvBrand; // Not in new layout
    private TextView tvProductPrice; // Changed from tvPrice
    // private TextView tvOriginalPrice; // Not in new layout
    // private TextView tvStock; // Not in new layout
    // private TextView tvRating; // Not in new layout
    private TextView tvProductDescription; // Changed from tvDescription
    // private TextView tvSpecifications; // Not in new layout
    // private MaterialCardView cardSpecifications; // Not in new layout
    // private MaterialCardView cardVideoReview; // Not in new layout
    // private WebView webViewVideo; // Not in new layout
    // private ProgressBar progressBar; // Not in new layout
    private MaterialButton btnAddToCart;
    // private MaterialButton btnBuyNow; // Not in new layout

    private Product currentProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
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
            android.util.Log.d("ProductDetailFragment", "Received product_id: " + productId);

            if (productId != null) {
                // Show loading state
                // progressBar.setVisibility(View.VISIBLE); // ProgressBar removed from layout
                viewModel.loadProductDetail(productId);
            } else {
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
                android.util.Log.e("ProductDetailFragment", "Product ID is null");
                navController.navigateUp();
            }
        } else {
            Toast.makeText(getContext(), "Lỗi: Không có thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            android.util.Log.e("ProductDetailFragment", "Arguments bundle is null");
            navController.navigateUp();
        }
    }

    private void bindViews(View view) {
        // toolbar = view.findViewById(R.id.toolbar); // Removed toolbar
        // viewPagerImages = view.findViewById(R.id.viewPagerImages); // Changed to imgProduct
        // layoutIndicators = view.findViewById(R.id.layoutIndicators); // Not needed in new layout
        tvProductName = view.findViewById(R.id.tvProductName);
        // tvBrand = view.findViewById(R.id.tvBrand); // Not in new layout
        tvProductPrice = view.findViewById(R.id.tvProductPrice); // Changed from tvPrice
        // tvOriginalPrice = view.findViewById(R.id.tvOriginalPrice); // Not in new layout
        // tvStock = view.findViewById(R.id.tvStock); // Not in new layout
        // tvRating = view.findViewById(R.id.tvRating); // Not in new layout
        tvProductDescription = view.findViewById(R.id.tvProductDescription); // Changed from tvDescription
        // tvSpecifications = view.findViewById(R.id.tvSpecifications); // Not in new layout
        // cardSpecifications = view.findViewById(R.id.cardSpecifications); // Not in new layout
        // cardVideoReview = view.findViewById(R.id.cardVideoReview); // Not in new layout
        // webViewVideo = view.findViewById(R.id.webViewVideo); // Not in new layout
        // progressBar = view.findViewById(R.id.progressBar); // Not in new layout
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
        // btnBuyNow = view.findViewById(R.id.btnBuyNow); // Not in new layout
    }

    private void setupListeners() {
        // toolbar.setNavigationOnClickListener(v -> navController.navigateUp()); // Toolbar removed

        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.isInStock()) {
                // Check if user is logged in
                if (!isLoggedIn()) {
                    Toast.makeText(getContext(), "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_productDetailFragment_to_loginFragment);
                    return;
                }

                addToCart(currentProduct);
                Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Sản phẩm hết hàng", Toast.LENGTH_SHORT).show();
            }
        });

        // btnBuyNow.setOnClickListener(v -> { // BuyNow button removed from layout
        //     if (currentProduct != null && currentProduct.isInStock()) {
        //         // Check if user is logged in
        //         if (!isLoggedIn()) {
        //             Toast.makeText(getContext(), "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
        //             navController.navigate(R.id.action_productDetailFragment_to_loginFragment);
        //             return;
        //         }
        //
        //         // Add to cart first
        //         viewModel.addToCart(currentProduct.getId(), 1);
        //
        //         // Navigate to checkout
        //         Bundle bundle = new Bundle();
        //         bundle.putString("product_id", currentProduct.getId());
        //         bundle.putInt("quantity", 1);
        //         navController.navigate(R.id.action_productDetailFragment_to_checkoutFragment, bundle);
        //     } else {
        //         Toast.makeText(getContext(), "Sản phẩm hiện không có sẵn", Toast.LENGTH_SHORT).show();
        //     }
        // });
    }

    private void observeViewModel() {
        viewModel.getSelectedProduct().observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                android.util.Log.d("ProductDetailFragment", "Received product data: " + product.getName());
                currentProduct = product;
                displayProductDetails(product);
            } else {
                android.util.Log.e("ProductDetailFragment", "Received null product data");
                Toast.makeText(getContext(), "Không thể tải thông tin sản phẩm", Toast.LENGTH_LONG).show();
                navController.navigateUp();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE); // ProgressBar removed
            if (isLoading) {
                android.util.Log.d("ProductDetailFragment", "Loading product details...");
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                android.util.Log.e("ProductDetailFragment", "Error loading product: " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                navController.navigateUp();
            }
        });
    }

    private void displayProductDetails(Product product) {
        // Only use views that exist in new layout
        
        // Product name
        tvProductName.setText(product.getName());
        
        // Price
        tvProductPrice.setText(currencyFormat.format(product.getDisplayPrice()));
        
        // Description
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvProductDescription.setText(product.getDescription());
        } else {
            tvProductDescription.setText("Đây là sản phẩm " + product.getName() + ". Sản phẩm chính hãng, bảo hành đầy đủ.");
        }

        // Enable/disable add to cart button based on stock
        if (product.isInStock()) {
            btnAddToCart.setEnabled(true);
        } else {
            btnAddToCart.setEnabled(false);
        }
        
        // All other views (tvBrand, tvStock, tvRating, etc.) are not in new layout
        // so we skip them
    }

    // All image pager and video methods commented out since views are not in new layout
    
    // private void setupImagePager(java.util.List<String> images) {
    //     imagePagerAdapter = new ImagePagerAdapter(getContext(), images);
    //     viewPagerImages.setAdapter(imagePagerAdapter);
    //
    //     // Setup indicators
    //     setupIndicators(images.size());
    //
    //     viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
    //         @Override
    //         public void onPageSelected(int position) {
    //             updateIndicators(position);
    //         }
    //     });
    // }
    //
    // private void setupIndicators(int count) {
    //     layoutIndicators.removeAllViews();
    //
    //     for (int i = 0; i < count; i++) {
    //         View indicator = new View(getContext());
    //         LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
    //                 24, 24);
    //         params.setMargins(8, 0, 8, 0);
    //         indicator.setLayoutParams(params);
    //         indicator.setBackgroundResource(android.R.drawable.presence_invisible);
    //         layoutIndicators.addView(indicator);
    //     }
    //
    //     if (count > 0) {
    //         updateIndicators(0);
    //     }
    // }
    //
    // private void updateIndicators(int position) {
    //     for (int i = 0; i < layoutIndicators.getChildCount(); i++) {
    //         View indicator = layoutIndicators.getChildAt(i);
    //         if (i == position) {
    //             indicator.setBackgroundResource(android.R.drawable.presence_online);
    //         } else {
    //             indicator.setBackgroundResource(android.R.drawable.presence_invisible);
    //         }
    //     }
    // }
    //
    // private void loadYouTubeVideo(String videoUrl) {
    //     webViewVideo.getSettings().setJavaScriptEnabled(true);
    //
    //     // Extract video ID from YouTube URL
    //     String videoId = extractYouTubeVideoId(videoUrl);
    //     if (videoId != null) {
    //         String html = "<iframe width=\"100%\" height=\"100%\" " +
    //                 "src=\"https://www.youtube.com/embed/" + videoId + "\" " +
    //                 "frameborder=\"0\" allowfullscreen></iframe>";
    //         webViewVideo.loadData(html, "text/html", "utf-8");
    //     }
    // }
    //
    // private String extractYouTubeVideoId(String url) {
    //     // Simple extraction - can be improved
    //     if (url.contains("youtube.com/watch?v=")) {
    //         return url.split("v=")[1].split("&")[0];
    //     } else if (url.contains("youtu.be/")) {
    //         return url.split("youtu.be/")[1].split("\\?")[0];
    //     }
    //     return null;
    // }

    private boolean isLoggedIn() {
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("PhoneShopPrefs",
                android.content.Context.MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }

    private void addToCart(Product product) {
        // Use CartViewModel to add to cart locally
        com.example.phoneshop.features.feature_cart.CartViewModel cartViewModel = new ViewModelProvider(
                requireActivity()).get(com.example.phoneshop.features.feature_cart.CartViewModel.class);

        // Initialize cart with context
        cartViewModel.initialize(requireContext());
        
        // Thêm vào giỏ hàng (local storage)
        cartViewModel.addProductToCart(product, 1);
        Toast.makeText(getContext(), "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
    }
}
