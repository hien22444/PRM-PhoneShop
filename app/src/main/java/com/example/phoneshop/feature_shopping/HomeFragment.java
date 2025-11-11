package com.example.phoneshop.feature_shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.feature_shopping.adapters.FlashSaleAdapter;
import com.example.phoneshop.feature_shopping.adapters.ProductAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class HomeFragment extends Fragment implements 
        ProductAdapter.OnProductClickListener,
        FlashSaleAdapter.OnFlashSaleClickListener {

    private ShoppingViewModel viewModel;
    private ProductAdapter productAdapter;
    private FlashSaleAdapter flashSaleAdapter;
    private NavController navController;

    // Views
    private MaterialToolbar toolbar;
    private MaterialCardView searchCard;
    private ChipGroup chipGroupBrands;
    private RecyclerView rvFlashSale;
    private RecyclerView rvProducts;
    private TextView tvEmptyProducts;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ShoppingViewModel.class);
        navController = Navigation.findNavController(view);

        // Bind views
        bindViews(view);

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup listeners
        setupListeners();

        // Observe ViewModel
        observeViewModel();
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        searchCard = view.findViewById(R.id.searchCard);
        chipGroupBrands = view.findViewById(R.id.chipGroupBrands);
        rvFlashSale = view.findViewById(R.id.rvFlashSale);
        rvProducts = view.findViewById(R.id.rvProducts);
        tvEmptyProducts = view.findViewById(R.id.tvEmptyProducts);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupRecyclerViews() {
        // Flash Sale RecyclerView (Horizontal)
        flashSaleAdapter = new FlashSaleAdapter(getContext(), null, this);
        LinearLayoutManager flashSaleLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false
        );
        rvFlashSale.setLayoutManager(flashSaleLayoutManager);
        rvFlashSale.setAdapter(flashSaleAdapter);

        // Products RecyclerView (Grid)
        productAdapter = new ProductAdapter(getContext(), null, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvProducts.setLayoutManager(gridLayoutManager);
        rvProducts.setAdapter(productAdapter);

        // Pagination - Load more when scrolling
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                        viewModel.loadMoreProducts();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        // Search card click
        searchCard.setOnClickListener(v -> {
            // Navigate to search fragment
            navController.navigate(R.id.action_homeFragment_to_productSearchFragment);
        });

        // Brand filter chips
        chipGroupBrands.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                
                if (checkedId == R.id.chipAll) {
                    viewModel.resetProducts();
                } else if (checkedId == R.id.chipSamsung) {
                    viewModel.filterByBrand("Samsung");
                } else if (checkedId == R.id.chipApple) {
                    viewModel.filterByBrand("Apple");
                } else if (checkedId == R.id.chipXiaomi) {
                    viewModel.filterByBrand("Xiaomi");
                } else if (checkedId == R.id.chipOppo) {
                    viewModel.filterByBrand("OPPO");
                }
            } else {
                // If no chip is selected, show all products
                viewModel.resetProducts();
            }
        });

        // Toolbar menu
        toolbar.setOnMenuItemClickListener(item -> {
            // Handle menu items if needed
            return false;
        });
    }

    private void observeViewModel() {
        // Observe products
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.updateProducts(products);
            }
        });

        // Observe flash sale products
        viewModel.getFlashSaleProducts().observe(getViewLifecycleOwner(), flashSales -> {
            if (flashSales != null) {
                flashSaleAdapter.updateFlashSales(flashSales);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe empty state
        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            tvEmptyProducts.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            rvProducts.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ProductAdapter callbacks
    @Override
    public void onProductClick(Product product) {
        // Navigate to product detail
        Bundle bundle = new Bundle();
        bundle.putString("product_id", product.getId());
        navController.navigate(R.id.action_homeFragment_to_productDetailFragment, bundle);
    }

    @Override
    public void onAddToCartClick(Product product) {
        // Check if user is logged in
        if (!isLoggedIn()) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.action_homeFragment_to_loginFragment);
            return;
        }

        // Add to cart via API using CartViewModel
        addToCart(product);
    }

    private boolean isLoggedIn() {
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("PhoneShopPrefs", android.content.Context.MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }

    private void addToCart(Product product) {
        // Use CartViewModel to add to cart via API
        com.example.phoneshop.features.feature_cart.CartViewModel cartViewModel = 
            new androidx.lifecycle.ViewModelProvider(requireActivity()).get(com.example.phoneshop.features.feature_cart.CartViewModel.class);
        
        // Thêm vào giỏ hàng
        cartViewModel.addToCart(product.getId(), 1);
        Toast.makeText(getContext(), "Đang thêm " + product.getName() + " vào giỏ hàng...", Toast.LENGTH_SHORT).show();
    }

    // FlashSaleAdapter callback
    @Override
    public void onFlashSaleClick(Product product) {
        // Navigate to product detail
        Bundle bundle = new Bundle();
        bundle.putString("product_id", product.getId());
        navController.navigate(R.id.action_homeFragment_to_productDetailFragment, bundle);
    }
}

