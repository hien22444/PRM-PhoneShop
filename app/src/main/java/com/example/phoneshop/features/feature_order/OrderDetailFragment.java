package com.example.phoneshop.features.feature_order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.service.OrderStorageService;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderDetailFragment extends Fragment {

    private NavController navController;
    private String orderId;
    private OrderStorageService orderStorageService;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // Views
    private MaterialToolbar toolbar;
    private TextView tvOrderId, tvOrderDate, tvOrderStatus, tvFullName, tvPhone, tvAddress, tvPaymentMethod, tvTotalPrice, tvItemCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        orderStorageService = OrderStorageService.getInstance();
        orderStorageService.initialize(requireContext());

        // Get order ID from arguments
        if (getArguments() != null) {
            orderId = getArguments().getString("order_id");
        }

        // Map views
        toolbar = view.findViewById(R.id.toolbar);
        tvOrderId = view.findViewById(R.id.tvOrderId);
        tvOrderDate = view.findViewById(R.id.tvOrderDate);
        tvOrderStatus = view.findViewById(R.id.tvOrderStatus);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvPaymentMethod = view.findViewById(R.id.tvPaymentMethod);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        tvItemCount = view.findViewById(R.id.tvItemCount);

        // Setup toolbar
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        // Load and display order details
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        if (orderId == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            navController.navigateUp();
            return;
        }

        Order order = orderStorageService.getOrderById(orderId);
        if (order == null) {
            Toast.makeText(getContext(), "Không tìm thấy đơn hàng #" + orderId, Toast.LENGTH_SHORT).show();
            navController.navigateUp();
            return;
        }

        // Display order information
        if (tvOrderId != null) tvOrderId.setText("Đơn hàng #" + order.getOrderId());
        if (tvOrderDate != null) tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
        if (tvOrderStatus != null) {
            tvOrderStatus.setText(order.getStatus());
            setStatusColor(tvOrderStatus, order.getStatus());
        }
        if (tvFullName != null) tvFullName.setText("Họ tên: " + order.getFullName());
        if (tvPhone != null) tvPhone.setText("Số điện thoại: " + order.getPhone());
        if (tvAddress != null) tvAddress.setText("Địa chỉ: " + order.getAddress());
        if (tvPaymentMethod != null) tvPaymentMethod.setText("Phương thức thanh toán: " + order.getPaymentMethod());
        if (tvTotalPrice != null) tvTotalPrice.setText("Tổng tiền: " + currencyFormat.format(order.getTotalPrice()));
        if (tvItemCount != null) tvItemCount.setText("Số lượng sản phẩm: " + order.getItemCount());

        // Set toolbar title
        toolbar.setTitle("Chi tiết đơn hàng");
    }

    private void setStatusColor(TextView tvStatus, String status) {
        int colorRes;
        switch (status) {
            case "Đang xử lý":
                colorRes = android.R.color.holo_orange_dark;
                break;
            case "Đang giao":
                colorRes = android.R.color.holo_blue_dark;
                break;
            case "Hoàn thành":
            case "Đã thanh toán":
                colorRes = android.R.color.holo_green_dark;
                break;
            case "Đã hủy":
                colorRes = android.R.color.holo_red_dark;
                break;
            default:
                colorRes = android.R.color.darker_gray;
                break;
        }
        tvStatus.setTextColor(getResources().getColor(colorRes));
    }
}
