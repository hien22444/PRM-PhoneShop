package com.example.phoneshop.feature_shopping;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.feature_shopping.adapters.ProductAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

public class ProductSearchFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private ShoppingViewModel viewModel;
    private ProductAdapter productAdapter;
    private NavController navController;
    
    // Search delay handler
    private Handler searchHandler;
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 500; // 500ms delay

    // Views
    private MaterialToolbar toolbar;
    private TextInputEditText etSearch;
    private ImageView ivClearSearch;
    private MaterialSwitch switchNameOnly;
    private ChipGroup chipGroupSuggestions;
    private TextView tvResultsCount;
    private LinearLayout layoutEmptyState;
    private TextView tvEmptyMessage;
    private RecyclerView rvSearchResults;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        navController = Navigation.findNavController(view);
        
        // Initialize search handler
        searchHandler = new Handler(Looper.getMainLooper());

        bindViews(view);
        setupRecyclerView();
        setupListeners();
        observeViewModel();

        // Clear any previous search results
        viewModel.clearSearchResults();

        // Focus on search input
        etSearch.requestFocus();
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        etSearch = view.findViewById(R.id.etSearch);
        ivClearSearch = view.findViewById(R.id.ivClearSearch);
        switchNameOnly = view.findViewById(R.id.switchNameOnly);
        chipGroupSuggestions = view.findViewById(R.id.chipGroupSuggestions);
        tvResultsCount = view.findViewById(R.id.tvResultsCount);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(getContext(), null, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvSearchResults.setLayoutManager(gridLayoutManager);
        rvSearchResults.setAdapter(productAdapter);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        // Search input listener with auto-search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivClearSearch.setVisibility(View.VISIBLE);
                } else {
                    ivClearSearch.setVisibility(View.GONE);
                    // Clear search results when search is empty
                    viewModel.clearSearchResults();
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    rvSearchResults.setVisibility(View.GONE);
                    tvResultsCount.setVisibility(View.GONE);
                    tvEmptyMessage.setText("Nhập tên sản phẩm hoặc loại sản phẩm\nVí dụ: iPhone, Samsung, điện thoại...");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                String query = s.toString().trim();
                if (!query.isEmpty() && query.length() >= 2) {
                    // Create new search runnable
                    searchRunnable = () -> performSearch(query);
                    // Delay search by 500ms
                    searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
                }
            }
        });

        // Search on keyboard action
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                performSearch(query);
                return true;
            }
            return false;
        });

        // Clear search button
        ivClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            viewModel.clearSearchResults();
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
            tvResultsCount.setVisibility(View.GONE);
            tvEmptyMessage.setText("Nhập tên sản phẩm hoặc loại sản phẩm\nVí dụ: iPhone, Samsung, điện thoại...");
        });

        // Setup suggestion chips
        setupSuggestionChips();
        
        // Setup name-only search switch
        setupNameOnlySwitch();
    }
    
    private void setupNameOnlySwitch() {
        switchNameOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etSearch.setHint("Tìm theo tên sản phẩm...");
                android.util.Log.d("ProductSearchFragment", "Switched to name-only search mode");
            } else {
                etSearch.setHint("Tìm theo tên, loại sản phẩm...");
                android.util.Log.d("ProductSearchFragment", "Switched to general search mode");
            }
            
            // Re-search if there's existing text
            String currentQuery = etSearch.getText().toString().trim();
            if (!currentQuery.isEmpty()) {
                performSearch(currentQuery);
            }
        });
    }

    private void setupSuggestionChips() {
        for (int i = 0; i < chipGroupSuggestions.getChildCount(); i++) {
            View child = chipGroupSuggestions.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setOnClickListener(v -> {
                    String suggestion = chip.getText().toString();
                    etSearch.setText(suggestion);
                    etSearch.setSelection(suggestion.length()); // Move cursor to end
                    performSearch(suggestion);
                });
            }
        }
    }

    private void performSearch(String query) {
        if (query == null || query.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hide keyboard
        if (getActivity() != null) {
            android.view.inputmethod.InputMethodManager imm = 
                (android.view.inputmethod.InputMethodManager) getActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null && getView() != null) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }

        // Check if name-only search is enabled
        if (switchNameOnly.isChecked()) {
            android.util.Log.d("ProductSearchFragment", "Performing name-only search for: " + query);
            viewModel.searchProductsByName(query);
        } else {
            android.util.Log.d("ProductSearchFragment", "Performing general search for: " + query);
            viewModel.searchProducts(query);
        }
    }
    
    // Overloaded method for backward compatibility
    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        performSearch(query);
    }

    private void observeViewModel() {
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.updateProducts(products);
                
                if (products.isEmpty()) {
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    rvSearchResults.setVisibility(View.GONE);
                    tvResultsCount.setVisibility(View.GONE);
                    tvEmptyMessage.setText("Không tìm thấy sản phẩm nào");
                } else {
                    layoutEmptyState.setVisibility(View.GONE);
                    rvSearchResults.setVisibility(View.VISIBLE);
                    tvResultsCount.setVisibility(View.VISIBLE);
                    tvResultsCount.setText("Tìm thấy " + products.size() + " kết quả");
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
                rvSearchResults.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                layoutEmptyState.setVisibility(View.VISIBLE);
                rvSearchResults.setVisibility(View.GONE);
                tvEmptyMessage.setText("Có lỗi xảy ra. Vui lòng thử lại");
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putString("product_id", product.getId());
        navController.navigate(R.id.action_productSearchFragment_to_productDetailFragment, bundle);
    }

    @Override
    public void onAddToCartClick(Product product) {
        Toast.makeText(getContext(), "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up search handler to prevent memory leaks
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}

