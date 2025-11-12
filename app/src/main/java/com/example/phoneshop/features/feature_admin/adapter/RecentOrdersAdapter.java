package com.example.phoneshop.features.feature_admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phoneshop.R;
import com.example.phoneshop.features.feature_admin.model.AdminStatsResponse;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying recent orders in admin dashboard
 */
public class RecentOrdersAdapter extends RecyclerView.Adapter<RecentOrdersAdapter.RecentOrderViewHolder> {

    private List<AdminStatsResponse.RecentOrder> recentOrders;
    private OnRecentOrderClickListener listener;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public interface OnRecentOrderClickListener {
        void onRecentOrderClick(AdminStatsResponse.RecentOrder order);
    }

    public RecentOrdersAdapter(List<AdminStatsResponse.RecentOrder> recentOrders, OnRecentOrderClickListener listener) {
        this.recentOrders = recentOrders;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
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
        AdminStatsResponse.RecentOrder order = recentOrders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return recentOrders != null ? recentOrders.size() : 0;
    }

    public void updateOrders(List<AdminStatsResponse.RecentOrder> newOrders) {
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
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onRecentOrderClick(recentOrders.get(getAdapterPosition()));
                }
            });
        }

        public void bind(AdminStatsResponse.RecentOrder order) {
            tvOrderId.setText("#" + order.getId());
            tvCustomerName.setText(order.getCustomerName());
            
            // Format currency
            String formattedAmount = formatCurrency(order.getTotalAmount());
            tvTotalAmount.setText(formattedAmount);
            
            tvStatus.setText(order.getStatus());
            
            // Format date
            try {
                String formattedDate = formatDate(order.getCreatedAt());
                tvCreatedAt.setText(formattedDate);
            } catch (Exception e) {
                tvCreatedAt.setText(order.getCreatedAt());
            }
            
            tvItemCount.setText(order.getItemCount() + " sản phẩm");
            
            // Set status color
            setStatusColor(order.getStatus());
        }

        private String formatCurrency(long amount) {
            try {
                return currencyFormat.format(amount).replace("₫", "VND");
            } catch (Exception e) {
                return String.format("%,d VND", amount);
            }
        }

        private String formatDate(String dateString) {
            try {
                // Assuming ISO format from server
                Date date = new Date(dateString);
                return dateFormat.format(date);
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
