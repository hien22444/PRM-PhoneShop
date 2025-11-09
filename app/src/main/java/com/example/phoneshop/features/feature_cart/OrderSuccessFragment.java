package com.example.phoneshop.features.feature_cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.phoneshop.R;
import com.google.android.material.button.MaterialButton;

public class OrderSuccessFragment extends Fragment {

    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        MaterialButton btnViewOrders = view.findViewById(R.id.btnViewOrders);
        MaterialButton btnBackToHome = view.findViewById(R.id.btnBackToHome);

        btnViewOrders.setOnClickListener(v -> {
            // Kiểm tra đăng nhập trước khi điều hướng tới Lịch sử đơn hàng
            android.content.SharedPreferences sharedPreferences = 
                requireActivity().getSharedPreferences("PhoneShopPrefs", android.content.Context.MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
            
            if (isLoggedIn) {
                navController.navigate(R.id.action_orderSuccessFragment_to_orderHistoryFragment);
            } else {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_orderSuccessFragment_to_homeFragment);
            }
        });

        btnBackToHome.setOnClickListener(v -> {
            // Điều hướng về Trang chủ
            // (Bạn cần tạo action này trong navgraph)
            // Chúng ta sẽ đặt ID là: action_orderSuccessFragment_to_homeFragment
            navController.navigate(R.id.action_orderSuccessFragment_to_homeFragment);
        });

        // Xử lý nút Back cứng của điện thoại
        // Không cho phép người dùng quay lại màn hình WebView/Thanh toán
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Không làm gì cả (chặn quay lại)
                        // Hoặc điều hướng về trang chủ
                        // navController.navigate(R.id.action_orderSuccessFragment_to_homeFragment);
                        Toast.makeText(getContext(), "Đơn hàng đã hoàn tất.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}