package com.example.phoneshop.features.feature_admin_ops.orders;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.phoneshop.R;
import com.example.phoneshop.common.base.BaseFragment;

public class AdminOrderDetailFragment extends BaseFragment {

    // Đảm bảo tạo layout fragment_admin_order_detail.xml
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_admin_order_detail;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nhận Order ID được truyền từ Fragment trước
        if (getArguments() != null) {
            String orderId = getArguments().getString("orderId");
            // Ví dụ: Hiển thị ID lên TextView
            TextView tvOrderId = view.findViewById(R.id.tv_detail_order_id);
            if (tvOrderId != null) {
                tvOrderId.setText("Chi tiết đơn hàng: " + orderId);
            }
        }
        // Thêm logic tải chi tiết đơn hàng từ ViewModel/Repository tại đây
    }
}