package com.example.phoneshop.features.feature_admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.features.feature_admin.adapter.AdminOrdersAdapter;
import com.example.phoneshop.features.feature_admin.viewmodel.AdminViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Admin Order Management Fragment
 * Manages orders - view, search, filter, update status
 */
public class AdminOrderManagementFragment extends Fragment implements AdminOrdersAdapter.OnOrderActionListener {

    private static final String TAG = "AdminOrderManagement";
    private static final int PAGE_SIZE = 20;
    
    // Order status options
    private static final String[] ORDER_STATUSES = {
        "Đang xử lý", "Đã xác nhận", "Đang giao", "Hoàn thành", "Đã hủy"
    };
    
    // Views
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircularProgressIndicator progressIndicator;
    private TextInputEditText etSearch;
    private MaterialButton btnSearch, btnClearSearch;
    private ChipGroup chipGroupStatus;
    private RecyclerView rvOrders;
    
    // Adapter
    private AdminOrdersAdapter ordersAdapter;
    
    // ViewModel
    private AdminViewModel adminViewModel;
    
    // Pagination and filtering
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private String currentQuery = "";
    private String currentStatus = "all";
    
    // Formatters
    private NumberFormat currencyFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_order_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupStatusChips();
        setupListeners();
        observeViewModel();
        
        // Initialize currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        // Load initial data
        loadOrders(true);
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        chipGroupStatus = view.findViewById(R.id.chipGroupStatus);
        rvOrders = view.findViewById(R.id.rvOrders);
    }

    private void setupViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupRecyclerView() {
        ordersAdapter = new AdminOrdersAdapter(new ArrayList<>(), this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvOrders.setLayoutManager(layoutManager);
        rvOrders.setAdapter(ordersAdapter);
        
        // Add scroll listener for pagination
        rvOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMoreData) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                        loadOrders(false);
                    }
                }
            }
        });
    }

    private void setupStatusChips() {
        // Add "All" chip
        Chip chipAll = new Chip(getContext());
        chipAll.setText("Tất cả");
        chipAll.setCheckable(true);
        chipAll.setChecked(true);
        chipAll.setTag("all");
        chipGroupStatus.addView(chipAll);
        
        // Add status chips
        for (String status : ORDER_STATUSES) {
            Chip chip = new Chip(getContext());
            chip.setText(status);
            chip.setCheckable(true);
            chip.setTag(status);
            chipGroupStatus.addView(chip);
        }
        
        // Set single selection
        chipGroupStatus.setSingleSelection(true);
    }

    private void setupListeners() {
        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 0;
            hasMoreData = true;
            loadOrders(true);
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
        
        // Status filter chips
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip selectedChip = group.findViewById(checkedIds.get(0));
                if (selectedChip != null) {
                    String status = (String) selectedChip.getTag();
                    if (!status.equals(currentStatus)) {
                        currentStatus = status;
                        currentPage = 0;
                        hasMoreData = true;
                        loadOrders(true);
                    }
                }
            }
        });
    }

    private void observeViewModel() {
        // Observe loading state
        adminViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            isLoading = loading;
            updateLoadingState(loading);
        });
        
        // Observe orders list
        adminViewModel.getOrdersList().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                updateOrdersList(orders);
            }
        });
        
        // Observe operation results (status update, etc.)
        adminViewModel.getOperationResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    // Refresh list after successful operation
                    currentPage = 0;
                    hasMoreData = true;
                    loadOrders(true);
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

    private void loadOrders(boolean refresh) {
        if (isLoading) return;
        
        if (refresh) {
            currentPage = 0;
            hasMoreData = true;
        }
        
        String statusFilter = currentStatus.equals("all") ? null : currentStatus;
        adminViewModel.loadOrders(currentPage, PAGE_SIZE, statusFilter, currentQuery);
        currentPage++;
    }

    private void updateLoadingState(boolean loading) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(loading);
        } else if (currentPage == 1) {
            // Show main progress indicator only for first page
            progressIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    private void updateOrdersList(List<Order> orders) {
        if (currentPage == 1) {
            // First page - replace all data
            ordersAdapter.updateOrders(orders);
        } else {
            // Additional pages - append data
            ordersAdapter.addOrders(orders);
        }
        
        // Check if we have more data
        hasMoreData = orders.size() == PAGE_SIZE;
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        currentQuery = query;
        currentPage = 0;
        hasMoreData = true;
        
        // Update UI
        btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
        
        loadOrders(true);
    }

    private void clearSearch() {
        etSearch.setText("");
        currentQuery = "";
        currentPage = 0;
        hasMoreData = true;
        btnClearSearch.setVisibility(View.GONE);
        
        loadOrders(true);
    }

    // AdminOrdersAdapter.OnOrderActionListener implementation
    @Override
    public void onOrderClick(Order order) {
        // Show order detail dialog
        showOrderDetailDialog(order);
    }

    @Override
    public void onUpdateStatusClick(Order order) {
        showUpdateStatusDialog(order);
    }

    @Override
    public void onViewCustomerClick(Order order) {
        // Show customer info dialog
        showCustomerInfoDialog(order);
    }

    private void showOrderDetailDialog(Order order) {
        if (getContext() == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chi tiết đơn hàng #" + order.getOrderId());
        
        StringBuilder details = new StringBuilder();
        details.append("Khách hàng: ").append(order.getFullName()).append("\n");
        details.append("Số điện thoại: ").append(order.getPhone()).append("\n");
        details.append("Địa chỉ: ").append(order.getAddress()).append("\n");
        details.append("Tổng tiền: ").append(formatCurrency(order.getTotalPrice())).append("\n");
        details.append("Số sản phẩm: ").append(order.getItemCount()).append("\n");
        details.append("Phương thức TT: ").append(order.getPaymentMethod()).append("\n");
        details.append("Trạng thái: ").append(order.getStatus()).append("\n");
        details.append("Ngày tạo: ").append(order.getOrderDate());
        
        builder.setMessage(details.toString());
        builder.setPositiveButton("Đóng", null);
        
        // Add action buttons
        builder.setNeutralButton("Cập nhật trạng thái", (dialog, which) -> {
            onUpdateStatusClick(order);
        });
        
        builder.setNegativeButton("Thông tin KH", (dialog, which) -> {
            onViewCustomerClick(order);
        });
        
        builder.show();
    }

    private void showUpdateStatusDialog(Order order) {
        if (getContext() == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cập nhật trạng thái đơn hàng");
        
        // Create spinner for status selection
        Spinner spinner = new Spinner(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, ORDER_STATUSES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        // Set current status
        for (int i = 0; i < ORDER_STATUSES.length; i++) {
            if (ORDER_STATUSES[i].equals(order.getStatus())) {
                spinner.setSelection(i);
                break;
            }
        }
        
        builder.setView(spinner);
        
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newStatus = (String) spinner.getSelectedItem();
            if (!newStatus.equals(order.getStatus())) {
                adminViewModel.updateOrderStatus(order.getOrderId(), newStatus);
            }
        });
        
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showCustomerInfoDialog(Order order) {
        if (getContext() == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thông tin khách hàng");
        
        StringBuilder info = new StringBuilder();
        info.append("Họ tên: ").append(order.getFullName()).append("\n");
        info.append("Số điện thoại: ").append(order.getPhone()).append("\n");
        info.append("Địa chỉ giao hàng: ").append(order.getAddress()).append("\n");
        
        // Add customer order history info if available
        info.append("\nThông tin đơn hàng này:\n");
        info.append("Mã đơn: ").append(order.getOrderId()).append("\n");
        info.append("Tổng tiền: ").append(formatCurrency(order.getTotalPrice())).append("\n");
        info.append("Ngày đặt: ").append(order.getOrderDate());
        
        builder.setMessage(info.toString());
        builder.setPositiveButton("Đóng", null);
        
        // Add action to view all orders from this customer
        builder.setNeutralButton("Xem tất cả đơn hàng", (dialog, which) -> {
            // Filter orders by customer name
            etSearch.setText(order.getFullName());
            performSearch();
        });
        
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
        // Refresh data when fragment becomes visible
        if (ordersAdapter.getItemCount() == 0) {
            loadOrders(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references to prevent memory leaks
        swipeRefreshLayout = null;
        progressIndicator = null;
        etSearch = null;
        btnSearch = null;
        btnClearSearch = null;
        chipGroupStatus = null;
        rvOrders = null;
        ordersAdapter = null;
    }
}
