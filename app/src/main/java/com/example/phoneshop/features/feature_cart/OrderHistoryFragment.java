package com.example.phoneshop.features.feature_cart;

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
import com.example.phoneshop.features.feature_cart.adapters.OrderHistoryAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class OrderHistoryFragment extends Fragment implements OrderHistoryAdapter.OnOrderClickListener {

    private OrderHistoryViewModel viewModel;
    private OrderHistoryAdapter adapter;
    private RecyclerView rvOrders;
    private TextView tvEmptyOrder;
    private NavController navController;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo NavController trước
        navController = Navigation.findNavController(view);
        
        // Kiểm tra đăng nhập
        if (!checkLoginStatus()) {
            return;
        }

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(OrderHistoryViewModel.class);

        // Ánh xạ Views
        rvOrders = view.findViewById(R.id.rvOrders);
        tvEmptyOrder = view.findViewById(R.id.tvEmptyOrder);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);

        // Cài đặt RecyclerView
        setupRecyclerView();

        // Lắng nghe dữ liệu
        observeViewModel();

        // Cài đặt nút Back trên Toolbar
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Kiểm tra lại đăng nhập khi fragment hiển thị lại
        if (!checkLoginStatus()) {
            return;
        }
    }

    private boolean checkLoginStatus() {
        sharedPreferences = requireActivity().getSharedPreferences("PhoneShopPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        
        if (!isLoggedIn) {
            // Nếu chưa đăng nhập, quay về màn hình trước và hiển thị thông báo
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
            if (navController != null && getView() != null) {
                // Quay về màn hình trước (thường là profileFragment hoặc homeFragment)
                if (navController.getCurrentDestination() != null) {
                    navController.popBackStack();
                }
            }
            return false;
        }
        return true;
    }

    private void setupRecyclerView() {
        // 'this' là Fragment này, vì nó implement OnOrderClickListener
        adapter = new OrderHistoryAdapter(getContext(), new ArrayList<>(), this);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Lắng nghe danh sách đơn hàng
        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                adapter.updateOrders(orders);
            }
        });

        // Lắng nghe trạng thái (trống/không)
        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            if (isEmpty != null) {
                tvEmptyOrder.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                rvOrders.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            }
        });
        
        // Lắng nghe trạng thái loading
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Có thể hiển thị progress bar nếu cần
        });
        
        // Lắng nghe lỗi
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý sự kiện khi người dùng click vào 1 đơn hàng
    @Override
    public void onOrderClick(Order order) {
        // Bắn thông báo (hoặc chuyển sang màn hình Chi tiết Đơn hàng)
        Toast.makeText(getContext(), "Bạn đã chọn đơn: " + order.getOrderId(), Toast.LENGTH_SHORT).show();

        // --- NÂNG CAO (CHO TƯƠNG LAI) ---
        // Đây là code để chuyển sang màn hình Chi tiết đơn hàng
        //
        // Bundle bundle = new Bundle();
        // bundle.putString("ORDER_ID", order.getOrderId());
        // navController.navigate(R.id.action_orderHistoryFragment_to_orderDetailFragment, bundle);
    }
}