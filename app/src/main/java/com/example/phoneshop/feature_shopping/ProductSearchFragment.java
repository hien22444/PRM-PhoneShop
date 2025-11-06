package com.example.phoneshop.feature_shopping;

import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputEditText;

public class ProductSearchFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private ShoppingViewModel viewModel;
    private ProductAdapter productAdapter;
    private NavController navController;

    // Views
    private MaterialToolbar toolbar;
    private TextInputEditText etSearch;
    private ImageView ivClearSearch;
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

        bindViews(view);
        setupRecyclerView();
        setupListeners();
        observeViewModel();

        // Focus on search input
        etSearch.requestFocus();
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        etSearch = view.findViewById(R.id.etSearch);
        ivClearSearch = view.findViewById(R.id.ivClearSearch);
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

        // Search input listener
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
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Search on keyboard action
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // Clear search button
        ivClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
            tvResultsCount.setVisibility(View.GONE);
            tvEmptyMessage.setText("Nhập từ khóa để tìm kiếm sản phẩm");
        });
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        
        if (query.isEmpty()) {
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

        viewModel.searchProducts(query);
    }

    private void observeViewModel() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
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
}

