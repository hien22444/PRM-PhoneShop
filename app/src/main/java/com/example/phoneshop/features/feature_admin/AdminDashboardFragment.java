package com.example.phoneshop.features.feature_admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.phoneshop.R;
import com.example.phoneshop.features.feature_admin.adapter.RecentOrdersAdapter;
import com.example.phoneshop.features.feature_admin.model.AdminStatsResponse;
import com.example.phoneshop.features.feature_admin.viewmodel.AdminViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Admin Dashboard Fragment
 * Displays overview statistics and recent activities
 */
public class AdminDashboardFragment extends Fragment {

    private static final String TAG = "AdminDashboardFragment";
    
    // Views
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircularProgressIndicator progressIndicator;
    private MaterialCardView cardUsers, cardOrders, cardRevenue, cardProducts;
    private TextView tvTotalUsers, tvTotalOrders, tvTotalRevenue, tvTotalProducts;
    private TextView tvProcessingOrders, tvCompletedOrders;
    private RecyclerView rvRecentOrders;
    private TextView tvNoRecentOrders;
    private TextView tvWelcomeMessage;
    private RecentOrdersAdapter recentOrdersAdapter;
    
    // Additional Stats
    private TextView tvAverageOrderValue;
    
    // ViewModel
    private AdminViewModel adminViewModel;
    
    // Formatters
    private NumberFormat currencyFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupListeners();
        observeViewModel();
        
        // Load initial data
        loadDashboardData();
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        
        // Welcome message
        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage);
        
        // Statistics Cards
        cardUsers = view.findViewById(R.id.cardUsers);
        cardOrders = view.findViewById(R.id.cardOrders);
        cardRevenue = view.findViewById(R.id.cardRevenue);
        cardProducts = view.findViewById(R.id.cardProducts);
        
        // Statistics TextViews
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        tvTotalProducts = view.findViewById(R.id.tvTotalProducts);
        
        // Order Status TextViews
        tvProcessingOrders = view.findViewById(R.id.tvProcessingOrders);
        tvCompletedOrders = view.findViewById(R.id.tvCompletedOrders);
        
        // Recent Orders
        rvRecentOrders = view.findViewById(R.id.rvRecentOrders);
        tvNoRecentOrders = view.findViewById(R.id.tvNoRecentOrders);
        
        // Initialize currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    private void setupViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupRecyclerView() {
        recentOrdersAdapter = new RecentOrdersAdapter(new ArrayList<>(), this::onRecentOrderClick);
        rvRecentOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentOrders.setAdapter(recentOrdersAdapter);
    }

    private void setupListeners() {
        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadDashboardData);
        
        // Card click listeners for navigation
        cardUsers.setOnClickListener(v -> navigateToUserManagement());
        cardOrders.setOnClickListener(v -> navigateToOrderManagement());
        cardProducts.setOnClickListener(v -> navigateToProductManagement());
        cardRevenue.setOnClickListener(v -> navigateToStatistics());
    }

    private void observeViewModel() {
        // Observe loading state
        adminViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            updateLoadingState(isLoading);
        });
        
        // Observe dashboard statistics
        adminViewModel.getDashboardStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null && stats.isSuccess()) {
                updateDashboardStats(stats);
            }
        });
        
        // Observe error messages
        adminViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadDashboardData() {
        if (adminViewModel != null) {
            adminViewModel.loadDashboardStats();
        }
        
        // Update welcome message with current time
        String timeOfDay = getTimeOfDay();
        tvWelcomeMessage.setText("Quản lý hệ thống PhoneShop - " + timeOfDay);
    }
    
    private String getTimeOfDay() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            return "Buổi sáng";
        } else if (hour < 18) {
            return "Buổi chiều";
        } else {
            return "Buổi tối";
        }
    }

    private void updateLoadingState(boolean isLoading) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(isLoading);
        } else {
            progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void updateDashboardStats(AdminStatsResponse stats) {
        AdminStatsResponse.StatsData data = stats.getData();
        if (data == null) return;
        
        // Update overview statistics
        AdminStatsResponse.Overview overview = data.getOverview();
        if (overview != null) {
            tvTotalUsers.setText(String.valueOf(overview.getTotalUsers()));
            tvTotalOrders.setText(String.valueOf(overview.getTotalOrders()));
            tvTotalProducts.setText(String.valueOf(overview.getTotalProducts()));
            
            // Format revenue
            String revenueText = formatCurrency(overview.getTotalRevenue());
            tvTotalRevenue.setText(revenueText);
            
            // Average order value
            String avgOrderValue = formatCurrency(overview.getAverageOrderValue());
            tvAverageOrderValue.setText("Giá trị TB: " + avgOrderValue);
        }
        
        // Update order status statistics
        AdminStatsResponse.OrdersByStatus ordersByStatus = data.getOrdersByStatus();
        if (ordersByStatus != null) {
            tvProcessingOrders.setText(String.valueOf(ordersByStatus.getProcessing()));
            tvCompletedOrders.setText(String.valueOf(ordersByStatus.getCompleted()));
        }
        
        // Update recent orders
        if (data.getRecentOrders() != null && !data.getRecentOrders().isEmpty()) {
            recentOrdersAdapter.updateOrders(data.getRecentOrders());
            rvRecentOrders.setVisibility(View.VISIBLE);
            tvNoRecentOrders.setVisibility(View.GONE);
        } else {
            rvRecentOrders.setVisibility(View.GONE);
            tvNoRecentOrders.setVisibility(View.VISIBLE);
        }
    }

    private String formatCurrency(long amount) {
        try {
            return currencyFormat.format(amount).replace("₫", "VND");
        } catch (Exception e) {
            return String.format("%,d VND", amount);
        }
    }

    private void onRecentOrderClick(AdminStatsResponse.RecentOrder order) {
        // Navigate to order detail
        if (getActivity() instanceof AdminActivity) {
            // You can implement navigation to order detail here
            Toast.makeText(getContext(), "Xem chi tiết đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToUserManagement() {
        if (getActivity() instanceof AdminActivity) {
            AdminActivity activity = (AdminActivity) getActivity();
            // Navigate to user management - you'll implement this in AdminActivity
            Toast.makeText(getContext(), "Chuyển đến quản lý người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToOrderManagement() {
        if (getActivity() instanceof AdminActivity) {
            AdminActivity activity = (AdminActivity) getActivity();
            // Navigate to order management
            Toast.makeText(getContext(), "Chuyển đến quản lý đơn hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToProductManagement() {
        if (getActivity() instanceof AdminActivity) {
            AdminActivity activity = (AdminActivity) getActivity();
            // Navigate to product management
            Toast.makeText(getContext(), "Chuyển đến quản lý sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToStatistics() {
        if (getActivity() instanceof AdminActivity) {
            AdminActivity activity = (AdminActivity) getActivity();
            // Navigate to detailed statistics
            Toast.makeText(getContext(), "Chuyển đến thống kê chi tiết", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        loadDashboardData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references to prevent memory leaks
        swipeRefreshLayout = null;
        progressIndicator = null;
        cardUsers = null;
        cardOrders = null;
        cardRevenue = null;
        cardProducts = null;
        tvTotalUsers = null;
        tvTotalOrders = null;
        tvTotalRevenue = null;
        tvTotalProducts = null;
        rvRecentOrders = null;
        tvNoRecentOrders = null;
        recentOrdersAdapter = null;
    }
}
