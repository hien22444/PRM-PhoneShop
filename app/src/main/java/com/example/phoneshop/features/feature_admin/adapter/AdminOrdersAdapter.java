package com.example.phoneshop.features.feature_admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying orders in admin order management
 */
public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.OrderViewHolder> {

    private List<Order> orders;
    private OnOrderActionListener listener;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public interface OnOrderActionListener {
        void onOrderClick(Order order);
        void onUpdateStatusClick(Order order);
        void onViewCustomerClick(Order order);
    }

    public AdminOrdersAdapter(List<Order> orders, OnOrderActionListener listener) {
        this.orders = orders;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    public void addOrders(List<Order> newOrders) {
        if (this.orders != null && newOrders != null) {
            int startPosition = this.orders.size();
            this.orders.addAll(newOrders);
            notifyItemRangeInserted(startPosition, newOrders.size());
        }
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardOrder;
        private TextView tvOrderId;
        private TextView tvCustomerName;
        private TextView tvCustomerPhone;
        private TextView tvTotalAmount;
        private TextView tvItemCount;
        private TextView tvPaymentMethod;
        private TextView tvOrderDate;
        private Chip chipStatus;
        private MaterialButton btnUpdateStatus;
        private MaterialButton btnViewCustomer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardOrder = itemView.findViewById(R.id.cardOrder);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
            btnViewCustomer = itemView.findViewById(R.id.btnViewCustomer);

            setupClickListeners();
        }

        private void setupClickListeners() {
            cardOrder.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onOrderClick(orders.get(getAdapterPosition()));
                }
            });

            btnUpdateStatus.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onUpdateStatusClick(orders.get(getAdapterPosition()));
                }
            });

            btnViewCustomer.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onViewCustomerClick(orders.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Order order) {
            tvOrderId.setText("#" + order.getOrderId());
            tvCustomerName.setText(order.getFullName());
            tvCustomerPhone.setText(order.getPhone());
            
            // Format currency
            String formattedAmount = formatCurrency(order.getTotalPrice());
            tvTotalAmount.setText(formattedAmount);
            
            tvItemCount.setText(order.getItemCount() + " sản phẩm");
            tvPaymentMethod.setText(order.getPaymentMethod());
            
            // Format date
            try {
                String formattedDate = formatDate(order.getOrderDate());
                tvOrderDate.setText(formattedDate);
            } catch (Exception e) {
                tvOrderDate.setText(order.getOrderDate());
            }
            
            // Set status chip
            setStatusChip(order.getStatus());
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
                // Try different date parsing strategies
                Date date;
                try {
                    // Try as timestamp
                    long timestamp = Long.parseLong(dateString);
                    date = new Date(timestamp);
                } catch (NumberFormatException e) {
                    // Try as ISO string
                    date = new Date(dateString);
                }
                return dateFormat.format(date);
            } catch (Exception e) {
                return dateString;
            }
        }

        private void setStatusChip(String status) {
            chipStatus.setText(status);
            
            // Set chip color based on status
            int chipBackgroundColorResId;
            int chipTextColorResId;
            
            switch (status) {
                case "Đang xử lý":
                    chipBackgroundColorResId = R.color.status_processing_bg;
                    chipTextColorResId = R.color.status_processing;
                    break;
                case "Đã xác nhận":
                    chipBackgroundColorResId = R.color.status_confirmed_bg;
                    chipTextColorResId = R.color.status_confirmed;
                    break;
                case "Đang giao":
                    chipBackgroundColorResId = R.color.status_shipping_bg;
                    chipTextColorResId = R.color.status_shipping;
                    break;
                case "Hoàn thành":
                    chipBackgroundColorResId = R.color.status_completed_bg;
                    chipTextColorResId = R.color.status_completed;
                    break;
                case "Đã hủy":
                    chipBackgroundColorResId = R.color.status_cancelled_bg;
                    chipTextColorResId = R.color.status_cancelled;
                    break;
                default:
                    chipBackgroundColorResId = R.color.status_processing_bg;
                    chipTextColorResId = R.color.status_processing;
                    break;
            }
            
            try {
                int backgroundColor = itemView.getContext().getResources().getColor(chipBackgroundColorResId, null);
                int textColor = itemView.getContext().getResources().getColor(chipTextColorResId, null);
                
                chipStatus.setChipBackgroundColorResource(chipBackgroundColorResId);
                chipStatus.setTextColor(textColor);
            } catch (Exception e) {
                // Fallback to default colors if resources not found
            }
        }
    }
}
