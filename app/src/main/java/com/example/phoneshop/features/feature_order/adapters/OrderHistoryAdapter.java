package com.example.phoneshop.features.feature_order.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;
    private OrderHistoryListener listener;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());

    public interface OrderHistoryListener {
        void onOrderClick(Order order);
        void onReviewClick(Order order);
        void onProductClick(Order order, String productId);
    }

    public OrderHistoryAdapter(Context context, List<Order> orders, OrderHistoryListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        // Basic order info
        holder.tvOrderId.setText("Đơn hàng #" + (order.getOrderId() != null ? order.getOrderId() : "N/A"));
        
        // Use formatted date if available, otherwise format the raw date
        String displayDate = order.getFormattedDate() != null ? 
            order.getFormattedDate() : 
            (order.getOrderDate() != null ? order.getOrderDate() : "N/A");
        holder.tvOrderDate.setText(displayDate);
        
        holder.tvOrderStatus.setText(order.getStatus() != null ? order.getStatus() : "Đang xử lý");
        
        // Use formatted total amount if available
        String displayPrice = order.getFormattedTotalAmount() != null ? 
            order.getFormattedTotalAmount() : 
            currencyFormat.format(order.getTotalPrice());
        holder.tvTotalPrice.setText(displayPrice);
        
        holder.tvItemCount.setText((order.getItemCount() > 0 ? order.getItemCount() : 1) + " sản phẩm");

        // Product preview - use preview image and name if available
        if (order.getPreviewImage() != null && !order.getPreviewImage().isEmpty()) {
            Glide.with(context)
                .load(order.getPreviewImage())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(holder.ivProductPreview);
        } else {
            holder.ivProductPreview.setImageResource(R.drawable.ic_image_placeholder);
        }
        
        if (order.getPreviewName() != null && !order.getPreviewName().isEmpty()) {
            holder.tvProductName.setText(order.getPreviewName());
        } else {
            holder.tvProductName.setText("Sản phẩm");
        }

        // Set status color and background
        setStatusColor(holder.tvOrderStatus, order.getStatus());

        // Show/hide review button based on order status
        if ("Hoàn thành".equals(order.getStatus())) {
            holder.btnReview.setVisibility(View.VISIBLE);
        } else {
            holder.btnReview.setVisibility(View.GONE);
        }

        // Click listeners
        holder.cardOrder.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
        
        // Product preview click - navigate to product detail
        holder.ivProductPreview.setOnClickListener(v -> {
            if (listener != null) {
                // Try to get product ID from order items
                String productId = getFirstProductId(order);
                if (productId != null) {
                    listener.onProductClick(order, productId);
                } else {
                    // Fallback to a demo product ID
                    listener.onProductClick(order, "1");
                }
            }
        });
        
        // Product name click - same as image click
        holder.tvProductName.setOnClickListener(v -> {
            if (listener != null) {
                String productId = getFirstProductId(order);
                if (productId != null) {
                    listener.onProductClick(order, productId);
                } else {
                    listener.onProductClick(order, "1");
                }
            }
        });
        
        holder.btnReview.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReviewClick(order);
            }
        });
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
                colorRes = android.R.color.holo_green_dark;
                break;
            case "Đã hủy":
                colorRes = android.R.color.holo_red_dark;
                break;
            default:
                colorRes = android.R.color.darker_gray;
                break;
        }
        tvStatus.setTextColor(context.getResources().getColor(colorRes));
    }

    @Override
    public int getItemCount() {
        return orders == null ? 0 : orders.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }
    
    private String getFirstProductId(Order order) {
        try {
            // Try to get product ID from order items
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                Order.OrderItem firstItem = order.getItems().get(0);
                if (firstItem.getProductId() != null) {
                    return firstItem.getProductId();
                }
            }
            
            // Fallback: try to extract from order ID or use default
            if (order.getOrderId() != null) {
                // For demo purposes, map order IDs to product IDs
                String orderId = order.getOrderId();
                if (orderId.contains("1") || orderId.contains("iphone")) return "1";
                if (orderId.contains("2") || orderId.contains("samsung")) return "2";
                if (orderId.contains("3") || orderId.contains("xiaomi")) return "3";
                if (orderId.contains("4") || orderId.contains("oppo")) return "4";
                if (orderId.contains("5") || orderId.contains("vivo")) return "5";
            }
            
            return null; // Will use fallback "1" in click listener
        } catch (Exception e) {
            Log.e("OrderHistoryAdapter", "Error getting product ID: " + e.getMessage());
            return null;
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardOrder;
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvTotalPrice, tvItemCount, tvProductName;
        ImageView ivProductPreview;
        MaterialButton btnReview;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardOrder = itemView.findViewById(R.id.cardOrder);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            ivProductPreview = itemView.findViewById(R.id.ivProductPreview);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}
