package com.example.phoneshop.features.feature_admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phoneshop.R;
import com.example.phoneshop.features.feature_admin.model.AdminStatsResponse;
import com.example.phoneshop.features.feature_admin.model.AdminOrdersResponse;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying recent orders in admin dashboard
 */
public class RecentOrdersAdapter extends RecyclerView.Adapter<RecentOrdersAdapter.RecentOrderViewHolder> {

    private List<AdminOrdersResponse.EnrichedOrder> recentOrders;
    private OnOrderStatusUpdateListener statusUpdateListener;
    private OnCustomerClickListener customerClickListener;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public interface OnOrderStatusUpdateListener {
        void onOrderStatusUpdate(String orderId, String currentStatus);
    }
    
    public interface OnCustomerClickListener {
        void onCustomerClick(String orderId);
    }

    public RecentOrdersAdapter(List<AdminOrdersResponse.EnrichedOrder> recentOrders, 
                              OnOrderStatusUpdateListener statusUpdateListener,
                              OnCustomerClickListener customerClickListener) {
        this.recentOrders = recentOrders;
        this.statusUpdateListener = statusUpdateListener;
        this.customerClickListener = customerClickListener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        this.dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public RecentOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_order, parent, false);
        return new RecentOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentOrderViewHolder holder, int position) {
        AdminOrdersResponse.EnrichedOrder order = recentOrders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return recentOrders != null ? recentOrders.size() : 0;
    }

    public void updateOrders(List<AdminOrdersResponse.EnrichedOrder> newOrders) {
        this.recentOrders = newOrders;
        notifyDataSetChanged();
    }

    class RecentOrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId;
        private TextView tvCustomerName;
        private TextView tvTotalAmount;
        private TextView tvStatus;
        private TextView tvCreatedAt;
        private TextView tvItemCount;

        public RecentOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvTotalAmount = itemView.findViewById(R.id.tvOrderTotal);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvCreatedAt = itemView.findViewById(R.id.tvOrderDate);

            // Set click listeners for different actions
            itemView.findViewById(R.id.tvOrderStatus).setOnClickListener(v -> {
                if (statusUpdateListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    AdminOrdersResponse.EnrichedOrder order = recentOrders.get(getAdapterPosition());
                    statusUpdateListener.onOrderStatusUpdate(order.getId(), order.getStatus());
                }
            });
            
            itemView.findViewById(R.id.tvCustomerName).setOnClickListener(v -> {
                if (customerClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    AdminOrdersResponse.EnrichedOrder order = recentOrders.get(getAdapterPosition());
                    customerClickListener.onCustomerClick(order.getId());
                }
            });
        }

        public void bind(AdminOrdersResponse.EnrichedOrder order) {
            tvOrderId.setText("#" + order.getId());
            tvCustomerName.setText(order.getCustomerName() != null ? order.getCustomerName() : "Khách vãng lai");
            
            // Use formatted price from server if available
            if (order.getFormattedPrice() != null) {
                tvTotalAmount.setText(order.getFormattedPrice());
            } else {
                String formattedAmount = formatCurrency(order.getTotalPrice());
                tvTotalAmount.setText(formattedAmount);
            }
            
            tvStatus.setText(order.getStatus() != null ? order.getStatus() : "Đang xử lý");
            
            // Use formatted date from server if available
            if (order.getFormattedDate() != null) {
                tvCreatedAt.setText(order.getFormattedDate());
            } else {
                try {
                    String formattedDate = formatDate(order.getOrderDate());
                    tvCreatedAt.setText(formattedDate);
                } catch (Exception e) {
                    tvCreatedAt.setText(order.getOrderDate());
                }
            }
            
            // Set status color
            setStatusColor(order.getStatus());
        }

        private String formatCurrency(double amount) {
            try {
                return currencyFormat.format(amount).replace("₫", "VND");
            } catch (Exception e) {
                return String.format("%,.0f VND", amount);
            }
        }

        private String formatDate(String dateString) {
            try {
                // Simple date formatting - just return the date part
                if (dateString != null && dateString.length() >= 10) {
                    return dateString.substring(0, 10);
                }
                return dateString;
            } catch (Exception e) {
                // If parsing fails, return original string
                return dateString;
            }
        }

        private void setStatusColor(String status) {
            int colorResId;
            switch (status) {
                case "Đang xử lý":
                    colorResId = R.color.status_processing;
                    break;
                case "Đã xác nhận":
                    colorResId = R.color.status_confirmed;
                    break;
                case "Đang giao":
                    colorResId = R.color.status_shipping;
                    break;
                case "Hoàn thành":
                    colorResId = R.color.status_completed;
                    break;
                case "Đã hủy":
                    colorResId = R.color.status_cancelled;
                    break;
                default:
                    colorResId = R.color.status_processing;
                    break;
            }
            
            try {
                int color = itemView.getContext().getResources().getColor(colorResId, null);
                tvStatus.setTextColor(color);
            } catch (Exception e) {
                // Fallback to default color if resource not found
            }
        }
    }
}
