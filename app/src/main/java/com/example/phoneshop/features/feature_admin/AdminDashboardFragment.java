package com.example.phoneshop.features.feature_admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.phoneshop.R;
import com.example.phoneshop.features.feature_admin.adapter.RecentOrdersAdapter;
import com.example.phoneshop.features.feature_admin.model.AdminStatsResponse;
import com.example.phoneshop.features.feature_admin.model.AdminOrdersResponse;
import com.example.phoneshop.features.feature_admin.model.AdminCustomerResponse;
import com.example.phoneshop.features.feature_admin.viewmodel.AdminViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
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
        
        try {
            initViews(view);
            setupViewModel();
            setupRecyclerView();
            setupListeners();
            observeViewModel();
            
            // Load initial data
            loadDashboardData();
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error in onViewCreated: " + e.getMessage(), e);
            // Show error message to user
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi khởi tạo dashboard: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initViews(View view) {
        try {
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
            currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
        }
    }

    private void setupViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupRecyclerView() {
        try {
            if (rvRecentOrders != null) {
                recentOrdersAdapter = new RecentOrdersAdapter(
                    new ArrayList<>(), 
                    this::onOrderStatusUpdate,
                    this::onCustomerClick
                );
                rvRecentOrders.setLayoutManager(new LinearLayoutManager(getContext()));
                rvRecentOrders.setAdapter(recentOrdersAdapter);
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
        }
    }

    private void setupListeners() {
        // Swipe to refresh
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> loadDashboardData());
        }
        
        // Card click listeners for navigation
        if (cardUsers != null) {
            cardUsers.setOnClickListener(v -> navigateToUserManagement());
        }
        if (cardOrders != null) {
            cardOrders.setOnClickListener(v -> navigateToOrderManagement());
        }
        if (cardProducts != null) {
            cardProducts.setOnClickListener(v -> navigateToProductManagement());
        }
        if (cardRevenue != null) {
            cardRevenue.setOnClickListener(v -> navigateToStatistics());
        }
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

        // Observe admin orders
        adminViewModel.getAdminOrders().observe(getViewLifecycleOwner(), ordersResponse -> {
            if (ordersResponse != null && ordersResponse.isSuccess()) {
                updateRecentOrders(ordersResponse);
            }
        });

        // Observe customer details
        adminViewModel.getCustomerDetails().observe(getViewLifecycleOwner(), customerResponse -> {
            if (customerResponse != null && customerResponse.isSuccess()) {
                showCustomerDetailsDialog(customerResponse);
            }
        });

        // Observe order update result
        adminViewModel.getOrderUpdateResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Cập nhật trạng thái đơn hàng thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDashboardData() {
        try {
            if (adminViewModel != null) {
                // Load all statistics
                adminViewModel.loadAllStats();
                // Load recent orders for dashboard
                adminViewModel.loadAdminOrders(1, 5, null, null);
            }
            
            // Update welcome message with current time
            if (tvWelcomeMessage != null) {
                String timeOfDay = getTimeOfDay();
                tvWelcomeMessage.setText("Quản lý hệ thống PhoneShop - " + timeOfDay);
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error loading dashboard data: " + e.getMessage(), e);
        }
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
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(isLoading);
        }
        if (progressIndicator != null) {
            progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void updateDashboardStats(AdminStatsResponse stats) {
        AdminStatsResponse.StatsData data = stats.getData();
        if (data == null) return;
        
        // Update overview statistics
        AdminStatsResponse.Overview overview = data.getOverview();
        if (overview != null) {
            if (tvTotalUsers != null) {
                tvTotalUsers.setText(String.valueOf(overview.getTotalUsers()));
            }
            if (tvTotalOrders != null) {
                tvTotalOrders.setText(String.valueOf(overview.getTotalOrders()));
            }
            if (tvTotalProducts != null) {
                tvTotalProducts.setText(String.valueOf(overview.getTotalProducts()));
            }
            
            // Format revenue
            if (tvTotalRevenue != null) {
                String revenueText = formatCurrency(overview.getTotalRevenue());
                tvTotalRevenue.setText(revenueText);
            }
            
            // Average order value (commented out if not in layout)
            // String avgOrderValue = formatCurrency(overview.getAverageOrderValue());
            // tvAverageOrderValue.setText("Giá trị TB: " + avgOrderValue);
        }
        
        // Update order status statistics
        AdminStatsResponse.OrdersByStatus ordersByStatus = data.getOrdersByStatus();
        if (ordersByStatus != null) {
            if (tvProcessingOrders != null) {
                tvProcessingOrders.setText(String.valueOf(ordersByStatus.getProcessing()));
            }
            if (tvCompletedOrders != null) {
                tvCompletedOrders.setText(String.valueOf(ordersByStatus.getCompleted()));
            }
        }
        
        // Note: Recent orders will be updated separately via AdminOrdersResponse
        // This dashboard stats only shows overview numbers
    }

    private String formatCurrency(double amount) {
        try {
            return currencyFormat.format(amount).replace("₫", "VND");
        } catch (Exception e) {
            return String.format("%,.0f VND", amount);
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
    
    // ==================== ORDER MANAGEMENT METHODS ====================
    
    private void updateRecentOrders(AdminOrdersResponse ordersResponse) {
        if (ordersResponse.getData() != null && ordersResponse.getData().getOrders() != null) {
            List<AdminOrdersResponse.EnrichedOrder> orders = 
                ordersResponse.getData().getOrders();
            
            if (orders.isEmpty()) {
                if (rvRecentOrders != null) {
                    rvRecentOrders.setVisibility(View.GONE);
                }
                if (tvNoRecentOrders != null) {
                    tvNoRecentOrders.setVisibility(View.VISIBLE);
                }
            } else {
                if (rvRecentOrders != null) {
                    rvRecentOrders.setVisibility(View.VISIBLE);
                }
                if (tvNoRecentOrders != null) {
                    tvNoRecentOrders.setVisibility(View.GONE);
                }
                
                // Setup adapter if not already done
                if (recentOrdersAdapter == null && rvRecentOrders != null) {
                    recentOrdersAdapter = new RecentOrdersAdapter(
                        orders,
                        this::onOrderStatusUpdate,
                        this::onCustomerClick
                    );
                    rvRecentOrders.setAdapter(recentOrdersAdapter);
                    rvRecentOrders.setLayoutManager(new LinearLayoutManager(getContext()));
                } else if (recentOrdersAdapter != null) {
                    recentOrdersAdapter.updateOrders(orders);
                }
            }
        }
    }
    
    private void onOrderStatusUpdate(String orderId, String currentStatus) {
        // Show status selection dialog
        String[] statusOptions = {
            "Đang xử lý",
            "Đã xác nhận", 
            "Đang giao",
            "Đã thanh toán",
            "Hoàn thành",
            "Đã hủy"
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cập nhật trạng thái đơn hàng")
               .setItems(statusOptions, (dialog, which) -> {
                   String newStatus = statusOptions[which];
                   if (!newStatus.equals(currentStatus)) {
                       adminViewModel.updateOrderStatus(orderId, newStatus);
                   }
               })
               .setNegativeButton("Hủy", null)
               .show();
    }
    
    private void onCustomerClick(String orderId) {
        // Load customer details
        adminViewModel.loadCustomerDetails(orderId);
    }
    
    private void showCustomerDetailsDialog(AdminCustomerResponse customerResponse) {
        if (customerResponse.getData() == null || customerResponse.getData().getCustomer() == null) {
            Toast.makeText(getContext(), "Không thể tải thông tin khách hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AdminCustomerResponse.CustomerInfo customer = 
            customerResponse.getData().getCustomer();
        
        // Create dialog content
        StringBuilder content = new StringBuilder();
        content.append("Họ tên: ").append(customer.getName()).append("\n");
        content.append("Số điện thoại: ").append(customer.getPhone()).append("\n");
        content.append("Email: ").append(customer.getEmail()).append("\n");
        content.append("Địa chỉ: ").append(customer.getAddress()).append("\n\n");
        content.append("Thông tin đơn hàng:\n");
        content.append("Tổng đơn hàng: ").append(customer.getTotalOrders()).append("\n");
        content.append("Tổng chi tiêu: ").append(customer.getFormattedTotalSpent()).append("\n");
        content.append("Ngày đăng ký: ").append(customer.getRegistrationDate()).append("\n");
        
        if (customer.isGuest()) {
            content.append("\n(Khách vãng lai)");
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thông tin khách hàng")
               .setMessage(content.toString())
               .setPositiveButton("Đóng", null);
        
        // Add "View All Orders" button if customer has multiple orders
        if (customer.getTotalOrders() > 1) {
            builder.setNeutralButton("Xem tất cả đơn hàng", (dialog, which) -> {
                // Navigate to customer orders view
                Toast.makeText(getContext(), "Chức năng xem tất cả đơn hàng đang phát triển", Toast.LENGTH_SHORT).show();
            });
        }
        
        builder.show();
    }
}
