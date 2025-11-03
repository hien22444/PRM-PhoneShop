package com.example.phoneshop.features.feature_cart.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Order;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;
    private OnOrderClickListener listener;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // Interface để Fragment lắng nghe sự kiện click
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderHistoryAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
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
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Đơn hàng #" + order.getOrderId());
        holder.tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
        holder.tvItemCount.setText("Số lượng: " + order.getItemCount() + " sản phẩm");
        holder.tvOrderTotal.setText(currencyFormat.format(order.getTotalPrice()));
        holder.chipOrderStatus.setText(order.getStatus());

        // --- Logic đổi màu cho Trạng thái ---
        // (Đây là phần "thân thiện" bạn muốn)
        // --- Logic đổi màu cho Trạng thái ---
        int statusColor;
        switch (order.getStatus()) {
            case "Hoàn thành":
                // Dùng màu status_completed bạn vừa thêm
                statusColor = ContextCompat.getColor(context, R.color.status_completed);
                break;
            case "Đã hủy":
                // Dùng màu status_cancelled bạn vừa thêm
                statusColor = ContextCompat.getColor(context, R.color.status_cancelled);
                break;
            case "Đang giao":
                // Dùng màu purple_500 (đã có sẵn)
                statusColor = ContextCompat.getColor(context, R.color.purple_500);
                break;
            default: // "Đang xử lý"
                // Dùng màu status_processing bạn vừa thêm
                statusColor = ContextCompat.getColor(context, R.color.status_processing);
                break;
        }
        holder.chipOrderStatus.setChipBackgroundColor(ColorStateList.valueOf(statusColor));

        // Bắt sự kiện click vào cả CardView
        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orderList = newOrders;
        notifyDataSetChanged();
    }

    // Lớp ViewHolder
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvItemCount, tvOrderTotal;
        Chip chipOrderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            chipOrderStatus = itemView.findViewById(R.id.chipOrderStatus);
        }
    }
}