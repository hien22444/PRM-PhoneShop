package com.example.phoneshop.features.feature_review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
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
import com.example.phoneshop.features.feature_review.ReviewViewModel;


import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Order;
import com.example.phoneshop.features.feature_review.adapters.ReviewProductAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ReviewFragment extends Fragment implements ReviewProductAdapter.ReviewProductListener {

    private ReviewViewModel viewModel;
    private ReviewProductAdapter adapter;
    private NavController navController;

    // Views
    private MaterialToolbar toolbar;
    private TextView tvOrderInfo;
    private RecyclerView rvReviewProducts;
    private MaterialButton btnSubmitReviews;

    private String orderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Get order ID from arguments
        if (getArguments() != null) {
            orderId = getArguments().getString("order_id");
        }

        if (orderId == null) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            navController.popBackStack();
            return;
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        // Map views
        toolbar = view.findViewById(R.id.toolbar);
        tvOrderInfo = view.findViewById(R.id.tvOrderInfo);
        rvReviewProducts = view.findViewById(R.id.rvReviewProducts);
        btnSubmitReviews = view.findViewById(R.id.btnSubmitReviews);

        // Setup toolbar
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        // Setup RecyclerView
        setupRecyclerView();

        // Setup button
        btnSubmitReviews.setOnClickListener(v -> submitReviews());

        // Observe ViewModel
        observeViewModel();

        // Load order details
        viewModel.loadOrderForReview(orderId);
    }

    private void setupRecyclerView() {
        adapter = new ReviewProductAdapter(getContext(), new ArrayList<>(), this);
        rvReviewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReviewProducts.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Observe order
        viewModel.getOrder().observe(getViewLifecycleOwner(), order -> {
            if (order != null) {
                tvOrderInfo.setText("Đánh giá đơn hàng #" + order.getOrderId());
                // Load products for this order
                viewModel.loadOrderProducts(orderId);
            }
        });

        // Observe products
        viewModel.getOrderProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                adapter.updateProducts(products);
            }
        });

        // Observe submit result
        viewModel.getSubmitResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(), "Đánh giá thành công!", Toast.LENGTH_SHORT).show();
                    navController.popBackStack();
                } else {
                    Toast.makeText(getContext(), "Không thể gửi đánh giá, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReviews() {
        // Get all reviews from adapter
        if (adapter.validateReviews()) {
            viewModel.submitReviews(orderId, adapter.getReviews());
        } else {
            Toast.makeText(getContext(), "Vui lòng đánh giá tất cả sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRatingChanged(String productId, float rating) {
        // Handle rating change if needed
    }

    @Override
    public void onCommentChanged(String productId, String comment) {
        // Handle comment change if needed
    }
}
