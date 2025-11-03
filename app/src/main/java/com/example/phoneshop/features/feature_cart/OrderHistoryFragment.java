package com.example.phoneshop.features.feature_cart;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(OrderHistoryViewModel.class);

        // Khởi tạo NavController
        navController = Navigation.findNavController(view);

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

    private void setupRecyclerView() {
        // 'this' là Fragment này, vì nó implement OnOrderClickListener
        adapter = new OrderHistoryAdapter(getContext(), new ArrayList<>(), this);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Lắng nghe danh sách đơn hàng
        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            adapter.updateOrders(orders);
        });

        // Lắng nghe trạng thái (trống/không)
        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            tvEmptyOrder.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            rvOrders.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
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