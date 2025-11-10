package com.example.phoneshop.features.feature_order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.R;
import com.google.android.material.appbar.MaterialToolbar;

public class OrderDetailFragment extends Fragment {

    private NavController navController;
    private String orderId;

    // Views
    private MaterialToolbar toolbar;
    private TextView tvOrderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Get order ID from arguments
        if (getArguments() != null) {
            orderId = getArguments().getString("order_id");
        }

        // Map views
        toolbar = view.findViewById(R.id.toolbar);
        tvOrderId = view.findViewById(R.id.tvOrderId);

        // Setup toolbar
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        // Display order info
        if (orderId != null) {
            tvOrderId.setText("Chi tiết đơn hàng #" + orderId);
        }
    }
}
