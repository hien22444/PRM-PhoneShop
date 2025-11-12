package com.example.phoneshop.feature_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Favorite;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.repository.FavoriteRepository;
import com.example.phoneshop.feature_shopping.adapters.ProductAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import com.example.phoneshop.data.repository.ProductRepository;

public class FavoriteFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private FavoriteRepository favoriteRepository;
    private NavController navController;
    private String currentUserId = "user_123"; // Should get from session

    // Views
    private MaterialToolbar toolbar;
    private RecyclerView rvFavorites;
    private LinearLayout layoutEmptyState;
    private TextView tvEmptyMessage;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        
        // Initialize repository
        ProductRepository productRepository = ProductRepository.getInstance();
        favoriteRepository = new FavoriteRepository(requireContext(), productRepository);

        bindViews(view);
        setupRecyclerView();
        setupListeners();
        loadFavorites();
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        rvFavorites = view.findViewById(R.id.rvFavorites);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(getContext(), new ArrayList<>(), this);
        rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFavorites.setAdapter(productAdapter);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
    }

    private void loadFavorites() {
        favoriteRepository.getUserFavorites(currentUserId).observe(getViewLifecycleOwner(), favorites -> {
            if (favorites != null) {
                List<Product> products = new ArrayList<>();
                for (Favorite favorite : favorites) {
                    if (favorite.getProduct() != null) {
                        products.add(favorite.getProduct());
                    }
                }

                if (products.isEmpty()) {
                    showEmptyState();
                } else {
                    showFavorites(products);
                }
            }
        });
    }

    private void showEmptyState() {
        layoutEmptyState.setVisibility(View.VISIBLE);
        rvFavorites.setVisibility(View.GONE);
        tvEmptyMessage.setText("Chưa có sản phẩm yêu thích nào\nHãy thêm sản phẩm vào danh sách yêu thích!");
    }

    private void showFavorites(List<Product> products) {
        layoutEmptyState.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.VISIBLE);
        productAdapter.updateProducts(products);
    }

    @Override
    public void onProductClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putString("product_id", product.getId());
        navController.navigate(R.id.action_favoriteFragment_to_productDetailFragment, bundle);
    }

    @Override
    public void onAddToCartClick(Product product) {
        // Check if user is logged in
        if (!isLoggedIn()) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add to cart functionality
        try {
            // Use CartViewModel to add to cart
            com.example.phoneshop.features.feature_cart.CartViewModel cartViewModel = 
                new androidx.lifecycle.ViewModelProvider(requireActivity())
                    .get(com.example.phoneshop.features.feature_cart.CartViewModel.class);
            
            cartViewModel.initialize(requireContext());
            cartViewModel.addProductToCart(product, 1);
            
            Toast.makeText(getContext(), "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Có lỗi xảy ra khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("PhoneShopPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }
}
