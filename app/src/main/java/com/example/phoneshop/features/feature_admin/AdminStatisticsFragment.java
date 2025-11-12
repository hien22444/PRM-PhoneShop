package com.example.phoneshop.features.feature_admin;

import android.graphics.Color;
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
import com.example.phoneshop.features.feature_admin.adapter.TopProductsAdapter;
import com.example.phoneshop.features.feature_admin.model.AdminStatsResponse;
import com.example.phoneshop.features.feature_admin.viewmodel.AdminViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Admin Statistics Fragment
 * Displays detailed statistics and analytics
 */
public class AdminStatisticsFragment extends Fragment {

    private static final String TAG = "AdminStatisticsFragment";
    
    // Views
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircularProgressIndicator progressIndicator;
    
    // Overview Cards
    private MaterialCardView cardTotalRevenue, cardAverageOrder, cardTopProduct, cardSystemInfo;
    private TextView tvTotalRevenue, tvAverageOrder, tvTopProduct, tvSystemUptime;
    
    // Monthly Revenue Section
    private TextView tvMonthlyRevenueTitle;
    private RecyclerView rvMonthlyRevenue;
    
    // Top Products Section
    private TextView tvTopProductsTitle;
    private RecyclerView rvTopProducts;
    private TextView tvNoTopProducts;
    
    // Order Status Breakdown
    private TextView tvProcessingCount, tvConfirmedCount, tvShippingCount, tvCompletedCount, tvCancelledCount;
    
    // System Information
    private TextView tvServerUptime, tvDataFile, tvLastBackup;
    
    // Adapters
    private TopProductsAdapter topProductsAdapter;
    
    // ViewModel
    private AdminViewModel adminViewModel;
    
    // Formatters
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupViewModel();
        setupRecyclerViews();
        setupListeners();
        observeViewModel();
        
        // Initialize formatters
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        // Load initial data
        loadStatistics();
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        
        // Overview Cards
        cardTotalRevenue = view.findViewById(R.id.cardTotalRevenue);
        cardAverageOrder = view.findViewById(R.id.cardAverageOrder);
        cardTopProduct = view.findViewById(R.id.cardTopProduct);
        cardSystemInfo = view.findViewById(R.id.cardSystemInfo);
        
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        tvAverageOrder = view.findViewById(R.id.tvAverageOrder);
        tvTopProduct = view.findViewById(R.id.tvTopProduct);
        tvSystemUptime = view.findViewById(R.id.tvSystemUptime);
        
        // Monthly Revenue
        tvMonthlyRevenueTitle = view.findViewById(R.id.tvMonthlyRevenueTitle);
        rvMonthlyRevenue = view.findViewById(R.id.rvMonthlyRevenue);
        
        // Top Products
        tvTopProductsTitle = view.findViewById(R.id.tvTopProductsTitle);
        rvTopProducts = view.findViewById(R.id.rvTopProducts);
        tvNoTopProducts = view.findViewById(R.id.tvNoTopProducts);
        
        // Order Status Breakdown
        tvProcessingCount = view.findViewById(R.id.tvProcessingCount);
        tvConfirmedCount = view.findViewById(R.id.tvConfirmedCount);
        tvShippingCount = view.findViewById(R.id.tvShippingCount);
        tvCompletedCount = view.findViewById(R.id.tvCompletedCount);
        tvCancelledCount = view.findViewById(R.id.tvCancelledCount);
        
        // System Information
        tvServerUptime = view.findViewById(R.id.tvServerUptime);
        tvDataFile = view.findViewById(R.id.tvDataFile);
        tvLastBackup = view.findViewById(R.id.tvLastBackup);
    }

    private void setupViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupRecyclerViews() {
        // Top Products RecyclerView
        topProductsAdapter = new TopProductsAdapter(new ArrayList<>());
        rvTopProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTopProducts.setAdapter(topProductsAdapter);
        
        // Monthly Revenue RecyclerView (you can create a similar adapter)
        // For now, we'll handle it in the update method
    }

    private void setupListeners() {
        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadStatistics);
        
        // Card click listeners for detailed views
        cardTotalRevenue.setOnClickListener(v -> showRevenueDetails());
        cardAverageOrder.setOnClickListener(v -> showOrderDetails());
        cardTopProduct.setOnClickListener(v -> showProductDetails());
        cardSystemInfo.setOnClickListener(v -> showSystemDetails());
    }

    private void observeViewModel() {
        // Observe loading state
        adminViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            updateLoadingState(isLoading);
        });
        
        // Observe dashboard statistics
        adminViewModel.getDashboardStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null && stats.isSuccess()) {
                updateStatistics(stats);
            }
        });
        
        // Observe error messages
        adminViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadStatistics() {
        adminViewModel.loadDashboardStats();
    }

    private void updateLoadingState(boolean isLoading) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(isLoading);
        } else {
            progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void updateStatistics(AdminStatsResponse stats) {
        AdminStatsResponse.StatsData data = stats.getData();
        if (data == null) return;
        
        // Update overview statistics
        updateOverviewStats(data.getOverview());
        
        // Update order status breakdown
        updateOrderStatusBreakdown(data.getOrdersByStatus());
        
        // Update top products
        updateTopProducts(data.getTopProducts());
        
        // Update monthly revenue
        updateMonthlyRevenue(data.getMonthlyRevenue());
        
        // Update system information
        updateSystemInfo(data.getSystemInfo());
    }

    private void updateOverviewStats(AdminStatsResponse.Overview overview) {
        if (overview == null) return;
        
        // Total Revenue
        String revenueText = formatCurrency(overview.getTotalRevenue());
        tvTotalRevenue.setText(revenueText);
        
        // Average Order Value
        String avgOrderText = formatCurrency(overview.getAverageOrderValue());
        tvAverageOrder.setText(avgOrderText);
        
        // System uptime (placeholder for now)
        tvSystemUptime.setText("Hoạt động ổn định");
    }

    private void updateOrderStatusBreakdown(AdminStatsResponse.OrdersByStatus ordersByStatus) {
        if (ordersByStatus == null) return;
        
        tvProcessingCount.setText(String.valueOf(ordersByStatus.getProcessing()));
        tvConfirmedCount.setText(String.valueOf(ordersByStatus.getConfirmed()));
        tvShippingCount.setText(String.valueOf(ordersByStatus.getShipping()));
        tvCompletedCount.setText(String.valueOf(ordersByStatus.getCompleted()));
        tvCancelledCount.setText(String.valueOf(ordersByStatus.getCancelled()));
    }

    private void updateTopProducts(java.util.List<AdminStatsResponse.TopProduct> topProducts) {
        if (topProducts != null && !topProducts.isEmpty()) {
            topProductsAdapter.updateTopProducts(topProducts);
            rvTopProducts.setVisibility(View.VISIBLE);
            tvNoTopProducts.setVisibility(View.GONE);
            
            // Update top product card
            AdminStatsResponse.TopProduct topProduct = topProducts.get(0);
            tvTopProduct.setText(topProduct.getName() + "\n" + 
                topProduct.getTotalQuantity() + " đã bán");
        } else {
            rvTopProducts.setVisibility(View.GONE);
            tvNoTopProducts.setVisibility(View.VISIBLE);
            tvTopProduct.setText("Chưa có dữ liệu");
        }
    }

    private void updateMonthlyRevenue(java.util.List<AdminStatsResponse.MonthlyRevenue> monthlyRevenue) {
        if (monthlyRevenue == null || monthlyRevenue.isEmpty()) {
            tvMonthlyRevenueTitle.setText("Doanh thu theo tháng (Chưa có dữ liệu)");
            return;
        }
        
        tvMonthlyRevenueTitle.setText("Doanh thu 6 tháng gần đây");
        
        // For now, we'll show a simple text summary
        // You can implement a chart library like MPAndroidChart for better visualization
        StringBuilder revenueText = new StringBuilder();
        for (AdminStatsResponse.MonthlyRevenue revenue : monthlyRevenue) {
            revenueText.append(revenue.getMonth())
                      .append(": ")
                      .append(formatCurrency(revenue.getRevenue()))
                      .append("\n");
        }
        
        // You can display this in a TextView or implement a proper chart
        // For now, we'll update the title to show total
        long totalRevenue = monthlyRevenue.stream()
            .mapToLong(AdminStatsResponse.MonthlyRevenue::getRevenue)
            .sum();
        tvMonthlyRevenueTitle.setText("Doanh thu 6 tháng: " + formatCurrency(totalRevenue));
    }

    private void updateSystemInfo(AdminStatsResponse.SystemInfo systemInfo) {
        if (systemInfo == null) return;
        
        // Server uptime
        double uptimeHours = systemInfo.getServerUptime() / 3600.0;
        String uptimeText = String.format(Locale.getDefault(), "%.1f giờ", uptimeHours);
        tvServerUptime.setText(uptimeText);
        
        // Data file
        tvDataFile.setText(systemInfo.getDataFile());
        
        // Last backup
        tvLastBackup.setText(systemInfo.getLastBackup());
    }

    private String formatCurrency(long amount) {
        try {
            return currencyFormat.format(amount).replace("₫", "VND");
        } catch (Exception e) {
            return String.format("%,d VND", amount);
        }
    }

    private void showRevenueDetails() {
        Toast.makeText(getContext(), "Chi tiết doanh thu - Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        // You can implement a detailed revenue analysis dialog here
    }

    private void showOrderDetails() {
        Toast.makeText(getContext(), "Chi tiết đơn hàng - Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        // You can implement a detailed order analysis dialog here
    }

    private void showProductDetails() {
        Toast.makeText(getContext(), "Chi tiết sản phẩm - Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        // You can implement a detailed product analysis dialog here
    }

    private void showSystemDetails() {
        Toast.makeText(getContext(), "Thông tin hệ thống - Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        // You can implement a detailed system info dialog here
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        loadStatistics();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references to prevent memory leaks
        swipeRefreshLayout = null;
        progressIndicator = null;
        cardTotalRevenue = null;
        cardAverageOrder = null;
        cardTopProduct = null;
        cardSystemInfo = null;
        tvTotalRevenue = null;
        tvAverageOrder = null;
        tvTopProduct = null;
        tvSystemUptime = null;
        rvTopProducts = null;
        topProductsAdapter = null;
    }
}
