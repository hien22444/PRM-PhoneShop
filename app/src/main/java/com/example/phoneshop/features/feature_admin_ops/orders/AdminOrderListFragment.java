package com.example.phoneshop.features.feature_admin_ops.orders;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation; // <<< Dòng BỔ SUNG CẦN THIẾT
import com.example.phoneshop.R;
import com.example.phoneshop.common.base.BaseFragment;
import com.example.phoneshop.data.model.Order;
import java.util.ArrayList;

public class AdminOrderListFragment extends BaseFragment implements AdminOrderAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private AdminOrderAdapter adapter;
    private AdminOrderViewModel viewModel;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_admin_order_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminOrderViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_orders);

        setupRecyclerView();
        observeViewModel();

        viewModel.fetchOrders();
    }

    private void setupRecyclerView() {
        adapter = new AdminOrderAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null && adapter != null) {
                adapter.setOrders(orders);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi tải đơn hàng: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(Order order) {
        // ... Logic điều hướng đã đúng
        Bundle bundle = new Bundle();
        bundle.putString("orderId", order.getId());

        if (getView() != null) {
            // Lớp Navigation đã được tìm thấy nhờ import
            Navigation.findNavController(getView()).navigate(R.id.adminOrderDetailFragment, bundle);
        } else {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy NavController.", Toast.LENGTH_SHORT).show();
        }
    }
}