package com.example.phoneshop.features.feature_admin_ops.products;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;
import java.util.List;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onDeleteClick(Product product);
        void onEditClick(Product product); // Thêm action Edit nếu cần
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Giả định layout item_product_admin tồn tại
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_admin, parent, false);
        return new ProductViewHolder(view, listener, productList);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (productList != null) {
            holder.bind(productList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvPrice, tvStock;
        final ImageView imgDelete, imgEdit;

        ProductViewHolder(View itemView, OnProductActionListener listener, List<Product> productList) {
            super(itemView);

            // Ánh xạ Views (Giả định các ID này tồn tại)
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvStock = itemView.findViewById(R.id.tv_product_stock);
            imgDelete = itemView.findViewById(R.id.img_delete_product);
            imgEdit = itemView.findViewById(R.id.img_edit_product);

            // Xử lý sự kiện click Xóa
            imgDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && productList != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(productList.get(position));
                }
            });

            // Xử lý sự kiện click Sửa
            imgEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && productList != null && position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(productList.get(position));
                }
            });
        }

        public void bind(Product product) {
            tvName.setText(String.format("Tên: %s", product.getName()));
            tvPrice.setText(String.format(Locale.getDefault(), "Giá: %,.0f VNĐ", product.getPrice()));
            tvStock.setText(String.format("Tồn kho: %d", product.getStock()));
        }
    }
}