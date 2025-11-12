package com.example.phoneshop.features.feature_admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.features.feature_admin.adapter.AdminProductsAdapter;
import com.example.phoneshop.features.feature_admin.viewmodel.AdminViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Admin Product Management Fragment
 * Manages products - view, search, filter, create, update, delete
 */
public class AdminProductManagementFragment extends Fragment implements AdminProductsAdapter.OnProductActionListener {

    private static final String TAG = "AdminProductManagement";
    private static final int PAGE_SIZE = 20;
    
    // Brand options
    private static final String[] BRANDS = {
        "Samsung", "Apple", "Xiaomi", "OPPO", "Vivo", "Realme", "Nokia"
    };
    
    // Views
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircularProgressIndicator progressIndicator;
    private TextInputEditText etSearch;
    private MaterialButton btnSearch, btnClearSearch;
    private ChipGroup chipGroupBrand;
    private RecyclerView rvProducts;
    private FloatingActionButton fabAddProduct;
    
    // Adapter
    private AdminProductsAdapter productsAdapter;
    
    // ViewModel
    private AdminViewModel adminViewModel;
    
    // Pagination and filtering
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private String currentQuery = "";
    private String currentBrand = "";
    
    // Formatters
    private NumberFormat currencyFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_product_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupBrandChips();
        setupListeners();
        observeViewModel();
        
        // Initialize currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        // Load initial data
        loadProducts(true);
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        chipGroupBrand = view.findViewById(R.id.chipGroupBrand);
        rvProducts = view.findViewById(R.id.rvProducts);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);
    }

    private void setupViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupRecyclerView() {
        productsAdapter = new AdminProductsAdapter(new ArrayList<>(), this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvProducts.setLayoutManager(layoutManager);
        rvProducts.setAdapter(productsAdapter);
        
        // Add scroll listener for pagination
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMoreData) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                        loadProducts(false);
                    }
                }
            }
        });
    }

    private void setupBrandChips() {
        // Add "All" chip
        Chip chipAll = new Chip(getContext());
        chipAll.setText("Tất cả");
        chipAll.setCheckable(true);
        chipAll.setChecked(true);
        chipAll.setTag("");
        chipGroupBrand.addView(chipAll);
        
        // Add brand chips
        for (String brand : BRANDS) {
            Chip chip = new Chip(getContext());
            chip.setText(brand);
            chip.setCheckable(true);
            chip.setTag(brand);
            chipGroupBrand.addView(chip);
        }
        
        // Set single selection
        chipGroupBrand.setSingleSelection(true);
    }

    private void setupListeners() {
        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 0;
            hasMoreData = true;
            loadProducts(true);
        });
        
        // Search button
        btnSearch.setOnClickListener(v -> performSearch());
        
        // Clear search button
        btnClearSearch.setOnClickListener(v -> clearSearch());
        
        // Search on enter key
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
        
        // Brand filter chips
        chipGroupBrand.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip selectedChip = group.findViewById(checkedIds.get(0));
                if (selectedChip != null) {
                    String brand = (String) selectedChip.getTag();
                    if (!brand.equals(currentBrand)) {
                        currentBrand = brand;
                        currentPage = 0;
                        hasMoreData = true;
                        loadProducts(true);
                    }
                }
            }
        });
        
        // Add product FAB
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());
    }

    private void observeViewModel() {
        // Observe loading state
        adminViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            isLoading = loading;
            updateLoadingState(loading);
        });
        
        // Observe products list
        adminViewModel.getProductsList().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                updateProductsList(products);
            }
        });
        
        // Observe operation results
        adminViewModel.getOperationResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    // Refresh list after successful operation
                    currentPage = 0;
                    hasMoreData = true;
                    loadProducts(true);
                } else {
                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        
        // Observe error messages
        adminViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadProducts(boolean refresh) {
        if (isLoading) return;
        
        if (refresh) {
            currentPage = 0;
            hasMoreData = true;
        }
        
        String brandFilter = currentBrand.isEmpty() ? null : currentBrand;
        adminViewModel.loadProducts(currentPage, PAGE_SIZE, currentQuery, brandFilter);
        currentPage++;
    }

    private void updateLoadingState(boolean loading) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(loading);
        } else if (currentPage == 1) {
            progressIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    private void updateProductsList(List<Product> products) {
        if (currentPage == 1) {
            productsAdapter.updateProducts(products);
        } else {
            productsAdapter.addProducts(products);
        }
        
        hasMoreData = products.size() == PAGE_SIZE;
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        currentQuery = query;
        currentPage = 0;
        hasMoreData = true;
        
        btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
        loadProducts(true);
    }

    private void clearSearch() {
        etSearch.setText("");
        currentQuery = "";
        currentPage = 0;
        hasMoreData = true;
        btnClearSearch.setVisibility(View.GONE);
        loadProducts(true);
    }

    // AdminProductsAdapter.OnProductActionListener implementation
    @Override
    public void onProductClick(Product product) {
        showProductDetailDialog(product);
    }

    @Override
    public void onEditProductClick(Product product) {
        showEditProductDialog(product);
    }

    @Override
    public void onDeleteProductClick(Product product) {
        showDeleteProductConfirmation(product);
    }

    @Override
    public void onToggleVisibilityClick(Product product) {
        // Toggle product visibility
        adminViewModel.updateProduct(product.getId(), product);
    }

    private void showAddProductDialog() {
        showProductFormDialog(null);
    }

    private void showEditProductDialog(Product product) {
        showProductFormDialog(product);
    }

    private void showProductFormDialog(Product existingProduct) {
        if (getContext() == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(existingProduct == null ? "Thêm sản phẩm mới" : "Chỉnh sửa sản phẩm");
        
        // Create form layout
        View formView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_product_form, null);
        
        EditText etName = formView.findViewById(R.id.etProductName);
        Spinner spinnerBrand = formView.findViewById(R.id.spinnerBrand);
        EditText etPrice = formView.findViewById(R.id.etProductPrice);
        EditText etStock = formView.findViewById(R.id.etProductStock);
        EditText etImageUrl = formView.findViewById(R.id.etProductImageUrl);
        
        // Setup brand spinner
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, BRANDS);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(brandAdapter);
        
        // Pre-fill form if editing
        if (existingProduct != null) {
            etName.setText(existingProduct.getName());
            etPrice.setText(String.valueOf(existingProduct.getPrice()));
            etStock.setText(String.valueOf(existingProduct.getStock()));
            
            if (existingProduct.getImages() != null && !existingProduct.getImages().isEmpty()) {
                etImageUrl.setText(existingProduct.getImages().get(0));
            }
            
            // Set brand selection
            for (int i = 0; i < BRANDS.length; i++) {
                if (BRANDS[i].equals(existingProduct.getBrand())) {
                    spinnerBrand.setSelection(i);
                    break;
                }
            }
        }
        
        builder.setView(formView);
        
        builder.setPositiveButton(existingProduct == null ? "Thêm" : "Cập nhật", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String brand = (String) spinnerBrand.getSelectedItem();
            String priceStr = etPrice.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();
            
            if (validateProductForm(name, brand, priceStr, stockStr)) {
                Product product = new Product();
                if (existingProduct != null) {
                    product.setId(existingProduct.getId());
                }
                product.setName(name);
                product.setBrand(brand);
                product.setPrice(Long.parseLong(priceStr));
                product.setStock(Integer.parseInt(stockStr));
                product.setVisible(true);
                
                if (!imageUrl.isEmpty()) {
                    product.setImages(Arrays.asList(imageUrl));
                }
                
                if (existingProduct == null) {
                    adminViewModel.createProduct(product);
                } else {
                    adminViewModel.updateProduct(product.getId(), product);
                }
            }
        });
        
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private boolean validateProductForm(String name, String brand, String price, String stock) {
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (brand == null || brand.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn thương hiệu", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        try {
            long priceValue = Long.parseLong(price);
            if (priceValue <= 0) {
                Toast.makeText(getContext(), "Giá sản phẩm phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Giá sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        try {
            int stockValue = Integer.parseInt(stock);
            if (stockValue < 0) {
                Toast.makeText(getContext(), "Số lượng tồn kho không được âm", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Số lượng tồn kho không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private void showProductDetailDialog(Product product) {
        if (getContext() == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chi tiết sản phẩm");
        
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(product.getId()).append("\n");
        details.append("Tên: ").append(product.getName()).append("\n");
        details.append("Thương hiệu: ").append(product.getBrand()).append("\n");
        details.append("Giá: ").append(formatCurrency(product.getPrice())).append("\n");
        details.append("Tồn kho: ").append(product.getStock()).append("\n");
        details.append("Trạng thái: ").append(product.isVisible() ? "Hiển thị" : "Ẩn").append("\n");
        
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            details.append("Hình ảnh: ").append(product.getImages().get(0));
        }
        
        builder.setMessage(details.toString());
        builder.setPositiveButton("Đóng", null);
        
        // Add action buttons
        builder.setNeutralButton("Chỉnh sửa", (dialog, which) -> {
            onEditProductClick(product);
        });
        
        builder.setNegativeButton("Xóa", (dialog, which) -> {
            onDeleteProductClick(product);
        });
        
        builder.show();
    }

    private void showDeleteProductConfirmation(Product product) {
        if (getContext() == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận xóa sản phẩm");
        builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"" + product.getName() + "\"?\n\n" +
                "Hành động này không thể hoàn tác.");
        
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            adminViewModel.deleteProduct(product.getId());
        });
        
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private String formatCurrency(long amount) {
        try {
            return currencyFormat.format(amount).replace("₫", "VND");
        } catch (Exception e) {
            return String.format("%,d VND", amount);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (productsAdapter.getItemCount() == 0) {
            loadProducts(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        swipeRefreshLayout = null;
        progressIndicator = null;
        etSearch = null;
        btnSearch = null;
        btnClearSearch = null;
        chipGroupBrand = null;
        rvProducts = null;
        fabAddProduct = null;
        productsAdapter = null;
    }
}
