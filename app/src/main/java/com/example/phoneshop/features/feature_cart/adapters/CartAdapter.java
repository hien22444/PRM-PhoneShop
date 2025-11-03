package com.example.phoneshop.features.feature_cart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.phoneshop.R;
// Giả sử bạn có model tên là CartItem trong gói data.model
import com.example.phoneshop.data.model.CartItem;
import java.util.List;
// Đừng quên thêm thư viện Glide hoặc Picasso để load ảnh
// import com.bumptech.glide.Glide;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private Context context;
    // Giao diện (Interface) để xử lý các sự kiện click
    private CartItemListener listener;

    // Interface để Fragment lắng nghe
    public interface CartItemListener {
        void onIncreaseClick(CartItem item);
        void onDecreaseClick(CartItem item);
        void onDeleteClick(CartItem item);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvProductName.setText(item.getProductName());
        holder.tvProductPrice.setText(String.format("%,dđ", item.getPrice())); // Format giá tiền
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Load ảnh (ví dụ dùng Glide)
        // Glide.with(context).load(item.getImageUrl()).into(holder.imgProduct);

        // Bắt sự kiện click
        holder.btnIncrease.setOnClickListener(v -> listener.onIncreaseClick(item));
        holder.btnDecrease.setOnClickListener(v -> listener.onDecreaseClick(item));
        holder.imgDelete.setOnClickListener(v -> listener.onDeleteClick(item));

        // Không cho giảm khi số lượng là 1
        holder.btnDecrease.setEnabled(item.getQuantity() > 1);
    }

    @Override
    public int getItemCount() {
        return cartItems == null ? 0 : cartItems.size();
    }

    // Hàm để cập nhật danh sách khi ViewModel có dữ liệu mới
    public void updateCartItems(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }

    // Lớp ViewHolder
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgDelete;
        TextView tvProductName, tvProductPrice, tvQuantity;
        Button btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
}