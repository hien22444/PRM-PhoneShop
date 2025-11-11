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
import com.example.phoneshop.data.repository.FavoriteRepository;
import com.example.phoneshop.data.repository.ProductRepository;
import com.example.phoneshop.feature_shopping.adapters.ImagePagerAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.Locale;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class ProductDetailFragment extends Fragment {

    private ShoppingViewModel viewModel;
    private NavController navController;
    private NumberFormat currencyFormat;
    private ImagePagerAdapter imagePagerAdapter;
    private FavoriteRepository favoriteRepository;
    
    // Views
    private ImageView imgProduct;
    private TextView tvProductName;
    private TextView tvProductPrice;
    private TextView tvProductDescription;
    private ImageView ivFavorite;
    private MaterialButton btnAddToCart;
    private MaterialButton btnBuyNow;

    private Product currentProduct;
    private String currentUserId = "user_123"; // Temporary user ID, should get from session

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
        
        // Initialize FavoriteRepository
        ProductRepository productRepository = ProductRepository.getInstance();
        favoriteRepository = new FavoriteRepository(requireContext(), productRepository);

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
        imgProduct = view.findViewById(R.id.imgProduct);
        tvProductName = view.findViewById(R.id.tvProductName);
        tvProductPrice = view.findViewById(R.id.tvProductPrice);
        tvProductDescription = view.findViewById(R.id.tvProductDescription);
        ivFavorite = view.findViewById(R.id.ivFavorite);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
        btnBuyNow = view.findViewById(R.id.btnBuyNow);
    }

    private void setupListeners() {
        // Favorite button listener
        ivFavorite.setOnClickListener(v -> {
            if (currentProduct != null) {
                toggleFavorite();
            }
        });

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


        btnBuyNow.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.isInStock()) {
                // 1. Kiểm tra đăng nhập
                if (!isLoggedIn()) {
                    Toast.makeText(getContext(), "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
                    // TODO: Thay 'R.id.action_productDetailFragment_to_loginFragment' bằng ID action của bạn
                    navController.navigate(R.id.action_productDetailFragment_to_loginFragment);
                    return;
                }

                // 2. Thêm vào giỏ hàng (dùng hàm có sẵn)
                addToCart(currentProduct);

                // 3. Chuyển đến màn hình giỏ hàng
                // **QUAN TRỌNG**: Thay 'R.id.action_productDetailFragment_to_cartFragment'
                // bằng ID action (mũi tên) trong nav_graph.xml của bạn.
                navController.navigate(R.id.action_productDetailFragment_to_cartFragment);

            } else {
                Toast.makeText(getContext(), "Sản phẩm hiện không có sẵn", Toast.LENGTH_SHORT).show();
            }
        });
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

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(requireContext())
                    .load(product.getImages().get(0)) // Tải ảnh đầu tiên
                    .placeholder(R.drawable.placeholder_product) // Ảnh giữ chỗ
                    .error(R.drawable.placeholder_product) // Ảnh khi lỗi
                    .into(imgProduct); // Đặt vào ImageView
        } else {
            // Nếu sản phẩm không có ảnh
            imgProduct.setImageResource(R.drawable.placeholder_product);
        }
        
        // Description
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvProductDescription.setText(product.getDescription());
        } else {
            tvProductDescription.setText("Đây là sản phẩm " + product.getName() + ". Sản phẩm chính hãng, bảo hành đầy đủ.");
        }

        // Check and update favorite status
        checkFavoriteStatus();

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
        
        // Show loading message
        Toast.makeText(getContext(), "Đang thêm " + product.getName() + " vào giỏ hàng...", Toast.LENGTH_SHORT).show();
        
        // Observe the result to show success/error feedback
        cartViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observe loading state
        cartViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (!isLoading) {
                // Check if there's no error, then it's success
                String error = cartViewModel.getError().getValue();
                if (error == null || error.isEmpty()) {
                    Toast.makeText(getContext(), "Đã thêm " + product.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void toggleFavorite() {
        if (currentProduct == null) return;

        boolean isFavorite = favoriteRepository.isFavorite(currentUserId, currentProduct.getId());
        
        if (isFavorite) {
            // Remove from favorites
            favoriteRepository.removeFromFavorites(currentUserId, currentProduct.getId());
            updateFavoriteIcon(false);
            Toast.makeText(getContext(), "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
        } else {
            // Add to favorites
            favoriteRepository.addToFavorites(currentUserId, currentProduct);
            updateFavoriteIcon(true);
            Toast.makeText(getContext(), "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (isFavorite) {
            ivFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            ivFavorite.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            ivFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            ivFavorite.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void checkFavoriteStatus() {
        if (currentProduct != null) {
            boolean isFavorite = favoriteRepository.isFavorite(currentUserId, currentProduct.getId());
            updateFavoriteIcon(isFavorite);
        }
    }
}
