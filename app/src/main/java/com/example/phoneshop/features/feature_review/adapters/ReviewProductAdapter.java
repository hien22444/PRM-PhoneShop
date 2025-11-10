package com.example.phoneshop.features.feature_review.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewProductAdapter extends RecyclerView.Adapter<ReviewProductAdapter.ReviewViewHolder> {

    // Lớp nội bộ để lưu trữ trạng thái đánh giá
    private static class ReviewData {
        float rating = 0;
        String comment = "";
    }

    private Context context;
    private List<Product> productList;
    private ReviewProductListener listener;

    // Map để lưu trữ dữ liệu đánh giá, Key là productId
    private Map<String, ReviewData> reviewDataMap;

    /**
     * Interface này được implement bởi ReviewFragment
     */
    public interface ReviewProductListener {
        void onRatingChanged(String productId, float rating);
        void onCommentChanged(String productId, String comment);
    }

    public ReviewProductAdapter(Context context, List<Product> productList, ReviewProductListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.reviewDataMap = new HashMap<>();
        initializeReviewMap();
    }

    private void initializeReviewMap() {
        reviewDataMap.clear();
        for (Product product : productList) {
            reviewDataMap.put(product.getId(), new ReviewData());
        }
    }

    /**
     * Cập nhật danh sách sản phẩm từ ViewModel
     */
    public void updateProducts(List<Product> newProducts) {
        this.productList = newProducts;
        initializeReviewMap();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // GIẢ ĐỊNH: Tên layout của bạn là 'item_review_product.xml'
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_product, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Product product = productList.get(position);
        // Lấy dữ liệu đánh giá hiện tại cho sản phẩm này
        ReviewData reviewData = reviewDataMap.get(product.getId());

        if (product == null || reviewData == null) return;

        // Set product name
        holder.tvProductName.setText(product.getName());
        
        // Set product price
        holder.tvProductPrice.setText(String.format("%,.0f₫", product.getPrice()));

        // Load product image
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(context)
                    .load(product.getImages().get(0))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // --- Quan trọng: Xử lý trạng thái của ViewHolder ---

        // 1. Gỡ listener cũ trước khi set giá trị mới (tránh trigger sự kiện)
        holder.ratingBar.setOnRatingBarChangeListener(null);
        holder.etComment.removeTextChangedListener(holder.commentWatcher);

        // 2. Set giá trị (rating/comment) hiện tại từ map
        holder.ratingBar.setRating(reviewData.rating);
        holder.etComment.setText(reviewData.comment);

        // 3. Gắn lại listener
        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                // Lưu rating mới vào map
                reviewData.rating = rating;
                // Báo cho Fragment biết
                if (listener != null) {
                    listener.onRatingChanged(product.getId(), rating);
                }
            }
        });

        // Cập nhật productId cho TextWatcher trước khi gắn
        holder.commentWatcher.updateProductId(product.getId());
        holder.etComment.addTextChangedListener(holder.commentWatcher);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ==========================================================
    // CÁC HÀM PUBLIC ĐƯỢC GỌI TỪ FRAGMENT
    // ==========================================================

    /**
     * Kiểm tra xem tất cả sản phẩm đã được đánh giá (rating > 0) chưa.
     */
    public boolean validateReviews() {
        if (reviewDataMap.isEmpty() && !productList.isEmpty()) return false;

        for (ReviewData data : reviewDataMap.values()) {
            if (data.rating <= 0) {
                return false; // Tìm thấy sản phẩm chưa được rate
            }
        }
        return true;
    }

    /**
     * Lấy danh sách tất cả các đánh giá để gửi lên server.
     */
    public List<ReviewInput> getReviews() {
        List<ReviewInput> reviews = new ArrayList<>();
        for (Map.Entry<String, ReviewData> entry : reviewDataMap.entrySet()) {
            ReviewData data = entry.getValue();
            // Chỉ thêm vào danh sách nếu có rating
            if (data.rating > 0) {
                reviews.add(new ReviewInput(
                        entry.getKey(),
                        data.rating,
                        data.comment
                ));
            }
        }
        return reviews;
    }

    /**
     * Model POJO đơn giản để gửi dữ liệu review đi
     * (ViewModel của bạn sẽ nhận List<ReviewInput>)
     */
    public static class ReviewInput {
        private String productId;
        private float rating;
        private String comment;

        public ReviewInput(String productId, float rating, String comment) {
            this.productId = productId;
            this.rating = rating;
            this.comment = comment;
        }

        // Cần Getter để thư viện (Gson/Moshi) serialize
        public String getProductId() { return productId; }
        public float getRating() { return rating; }
        public String getComment() { return comment; }
    }


    // ==========================================================
    // VIEWHOLDER VÀ TEXTWATCHER
    // ==========================================================

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice;
        RatingBar ratingBar;
        TextInputEditText etComment;
        CustomTextWatcher commentWatcher;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            // Map với ID trong layout item_review_product.xml
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            etComment = itemView.findViewById(R.id.etComment);

            commentWatcher = new CustomTextWatcher(reviewDataMap, listener);
        }
    }

    /**
     * TextWatcher tùy chỉnh để xử lý việc tái sử dụng ViewHolder
     */
    private static class CustomTextWatcher implements TextWatcher {
        private String productId;
        private Map<String, ReviewData> reviewDataMap;
        private ReviewProductListener listener;

        public CustomTextWatcher(Map<String, ReviewData> reviewDataMap, ReviewProductListener listener) {
            this.reviewDataMap = reviewDataMap;
            this.listener = listener;
        }

        // Cập nhật productId mỗi khi onBindViewHolder được gọi
        public void updateProductId(String productId) {
            this.productId = productId;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (productId != null) {
                // Lưu bình luận mới vào map
                ReviewData data = reviewDataMap.get(productId);
                if (data != null) {
                    data.comment = s.toString();
                }
                // Báo cho Fragment biết
                if (listener != null) {
                    listener.onCommentChanged(productId, s.toString());
                }
            }
        }
    }
}