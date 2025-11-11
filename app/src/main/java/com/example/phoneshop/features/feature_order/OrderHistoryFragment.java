package com.example.phoneshop.features.feature_order;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.features.feature_order.adapters.OrderHistoryAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class OrderHistoryFragment extends Fragment implements OrderHistoryAdapter.OrderHistoryListener {

    private OrderHistoryViewModel viewModel;
    private OrderHistoryAdapter adapter;
    private NavController navController;
    private SharedPreferences sharedPreferences;

    // Views
    private MaterialToolbar toolbar;
    private RecyclerView rvOrderHistory;
    private TextView tvEmptyOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            // Khởi tạo NavController
            navController = Navigation.findNavController(view);
            
            // Kiểm tra đăng nhập
            if (!checkLoginStatus()) {
                return;
            }

            // Khởi tạo ViewModel
            viewModel = new ViewModelProvider(this).get(OrderHistoryViewModel.class);
            
            // Initialize ViewModel with context
            viewModel.initialize(requireContext());

            // Ánh xạ Views
            toolbar = view.findViewById(R.id.toolbar);
            rvOrderHistory = view.findViewById(R.id.rvOrderHistory);
            tvEmptyOrders = view.findViewById(R.id.tvEmptyOrders);

            // Kiểm tra views có null không
            if (rvOrderHistory == null) {
                android.util.Log.e("OrderHistoryFragment", "RecyclerView is null");
                Toast.makeText(getContext(), "Lỗi khởi tạo giao diện", Toast.LENGTH_SHORT).show();
                return;
            }

            // Setup toolbar
            if (toolbar != null) {
                toolbar.setNavigationOnClickListener(v -> {
                    if (navController != null) {
                        navController.navigateUp();
                    }
                });
            }

            // Setup RecyclerView
            setupRecyclerView();

            // Observe ViewModel
            observeViewModel();

            // Load orders
            if (viewModel != null) {
                viewModel.loadOrderHistory();
            }
            
        } catch (Exception e) {
            android.util.Log.e("OrderHistoryFragment", "Error in onViewCreated: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi tải lịch sử đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            
            // Show empty state
            if (tvEmptyOrders != null) {
                tvEmptyOrders.setVisibility(View.VISIBLE);
                tvEmptyOrders.setText("Không thể tải lịch sử đơn hàng");
            }
            if (rvOrderHistory != null) {
                rvOrderHistory.setVisibility(View.GONE);
            }
        }
    }

    private boolean checkLoginStatus() {
        sharedPreferences = requireActivity().getSharedPreferences("PhoneShopPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        
        if (!isLoggedIn) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
            navController.popBackStack();
            return false;
        }
        return true;
    }

    private void setupRecyclerView() {
        try {
            adapter = new OrderHistoryAdapter(getContext(), new ArrayList<>(), this);
            rvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));
            rvOrderHistory.setAdapter(adapter);
        } catch (Exception e) {
            android.util.Log.e("OrderHistoryFragment", "Error setting up RecyclerView: " + e.getMessage());
            // Show empty state if adapter fails
            if (tvEmptyOrders != null) {
                tvEmptyOrders.setVisibility(View.VISIBLE);
                tvEmptyOrders.setText("Không thể hiển thị danh sách đơn hàng");
            }
            if (rvOrderHistory != null) {
                rvOrderHistory.setVisibility(View.GONE);
            }
        }
    }

    private void observeViewModel() {
        try {
            // Observe orders list
            viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
                try {
                    if (orders != null && adapter != null) {
                        adapter.updateOrders(orders);
                        
                        if (orders.isEmpty()) {
                            if (tvEmptyOrders != null) tvEmptyOrders.setVisibility(View.VISIBLE);
                            if (rvOrderHistory != null) rvOrderHistory.setVisibility(View.GONE);
                        } else {
                            if (tvEmptyOrders != null) tvEmptyOrders.setVisibility(View.GONE);
                            if (rvOrderHistory != null) rvOrderHistory.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Show empty state if orders is null
                        if (tvEmptyOrders != null) {
                            tvEmptyOrders.setVisibility(View.VISIBLE);
                            tvEmptyOrders.setText("Không thể tải danh sách đơn hàng");
                        }
                        if (rvOrderHistory != null) rvOrderHistory.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    android.util.Log.e("OrderHistoryFragment", "Error updating orders UI: " + e.getMessage());
                    if (tvEmptyOrders != null) {
                        tvEmptyOrders.setVisibility(View.VISIBLE);
                        tvEmptyOrders.setText("Lỗi hiển thị đơn hàng");
                    }
                }
            });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Show/hide loading indicator if needed
        });

        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        } catch (Exception e) {
            android.util.Log.e("OrderHistoryFragment", "Error setting up observers: " + e.getMessage());
            if (tvEmptyOrders != null) {
                tvEmptyOrders.setVisibility(View.VISIBLE);
                tvEmptyOrders.setText("Lỗi khởi tạo màn hình đơn hàng");
            }
        }
    }

    @Override
    public void onOrderClick(Order order) {
        // Navigate to order detail
        try {
            Bundle bundle = new Bundle();
            bundle.putString("order_id", order.getOrderId());
            navController.navigate(R.id.action_orderHistoryFragment_to_orderDetailFragment, bundle);
        } catch (Exception e) {
            android.util.Log.e("OrderHistoryFragment", "Navigation error: " + e.getMessage());
            Toast.makeText(getContext(), "Xem chi tiết đơn hàng #" + order.getOrderId(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReviewClick(Order order) {
        // TODO: Add navigation to review fragment
        Toast.makeText(getContext(), "Đánh giá đơn hàng #" + order.getOrderId(), Toast.LENGTH_SHORT).show();
        
        // Uncomment when nav_graph.xml is updated:
        // Bundle bundle = new Bundle();
        // bundle.putString("order_id", order.getId());
        // navController.navigate(R.id.action_orderHistoryFragment_to_reviewFragment, bundle);
    }
}
