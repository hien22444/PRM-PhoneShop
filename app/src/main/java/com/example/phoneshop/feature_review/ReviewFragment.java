package com.example.phoneshop.feature_review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Review;
import com.example.phoneshop.data.repository.ReviewRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ReviewFragment extends Fragment {

    private ReviewRepository reviewRepository;
    private NavController navController;
    
    // Arguments from bundle
    private String orderId;
    private String productId;
    private String productName;
    private String productImageUrl;
    
    // Views
    private MaterialToolbar toolbar;
    private ImageView ivProductImage;
    private TextView tvProductName;
    private RatingBar ratingBar;
    private TextInputEditText etComment;
    private MaterialButton btnSubmitReview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        reviewRepository = new ReviewRepository(requireContext());

        // Get arguments
        if (getArguments() != null) {
            orderId = getArguments().getString("order_id");
            productId = getArguments().getString("product_id");
            productName = getArguments().getString("product_name");
            productImageUrl = getArguments().getString("product_image_url");
        }

        bindViews(view);
        setupListeners();
        displayProductInfo();
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        ivProductImage = view.findViewById(R.id.ivProductImage);
        tvProductName = view.findViewById(R.id.tvProductName);
        ratingBar = view.findViewById(R.id.ratingBar);
        etComment = view.findViewById(R.id.etComment);
        btnSubmitReview = view.findViewById(R.id.btnSubmitReview);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void displayProductInfo() {
        if (productName != null) {
            tvProductName.setText(productName);
        }

        if (productImageUrl != null && !productImageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(productImageUrl)
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .into(ivProductImage);
        } else {
            ivProductImage.setImageResource(R.drawable.placeholder_product);
        }
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        // Validation
        if (rating == 0) {
            Toast.makeText(getContext(), "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập nhận xét về sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.length() < 10) {
            Toast.makeText(getContext(), "Nhận xét phải có ít nhất 10 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create review
        Review review = new Review(orderId, productId, productName, (int) rating, comment);
        
        // Save review
        reviewRepository.addReview(review).observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Cảm ơn bạn đã đánh giá sản phẩm!", Toast.LENGTH_SHORT).show();
                navController.navigateUp();
            } else {
                Toast.makeText(getContext(), "Có lỗi xảy ra. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
