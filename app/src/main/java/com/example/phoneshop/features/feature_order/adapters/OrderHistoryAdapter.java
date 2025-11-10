package com.example.phoneshop.features.feature_order.adapters;

import android.content.Context;
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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;
    private OrderHistoryListener listener;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public interface OrderHistoryListener {
        void onOrderClick(Order order);
        void onReviewClick(Order order);
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

        holder.tvOrderId.setText("Đơn hàng #" + order.getOrderId());
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvTotalPrice.setText(currencyFormat.format(order.getTotalPrice()));
        holder.tvItemCount.setText(order.getItemCount() + " sản phẩm");

        // Set status color
        setStatusColor(holder.tvOrderStatus, order.getStatus());

        // Show/hide review button based on order status
        if ("Hoàn thành".equals(order.getStatus())) {
            holder.btnReview.setVisibility(View.VISIBLE);
        } else {
            holder.btnReview.setVisibility(View.GONE);
        }

        // Click listeners
        holder.cardOrder.setOnClickListener(v -> listener.onOrderClick(order));
        holder.btnReview.setOnClickListener(v -> listener.onReviewClick(order));
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

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardOrder;
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvTotalPrice, tvItemCount;
        MaterialButton btnReview;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardOrder = itemView.findViewById(R.id.cardOrder);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}
