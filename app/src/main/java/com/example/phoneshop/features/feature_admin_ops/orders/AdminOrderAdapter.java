package com.example.phoneshop.features.feature_admin_ops.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Order;
import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private OnItemClickListener listener;

    // SỬA LỖI TẠI ĐÂY: Constructor phải chấp nhận List<Order>
    public AdminOrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    // Phương thức để cập nhật dữ liệu (được gọi từ Fragment)
    public void setOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    // Giao diện Listener để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Order order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Cần tạo layout item_order_admin.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_admin, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order currentOrder = orders.get(position);
        holder.bind(currentOrder);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // ==============================================
    // View Holder
    // ==============================================

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderId;
        private final TextView tvUserName;
        private final TextView tvStatus;
        private final TextView tvTotalAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các views trong item_order_admin.xml
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);

            // Thiết lập sự kiện click cho toàn bộ item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(orders.get(position));
                }
            });
        }

        public void bind(Order order) {
            tvOrderId.setText("Mã ĐH: " + order.getId());
            tvUserName.setText("Khách hàng: " + order.getUserName());
            tvStatus.setText("Trạng thái: " + order.getStatus());
            // Định dạng tiền tệ
            String amount = String.format("Tổng tiền: %,.0f VNĐ", order.getTotalAmount());
            tvTotalAmount.setText(amount);
        }
    }
}