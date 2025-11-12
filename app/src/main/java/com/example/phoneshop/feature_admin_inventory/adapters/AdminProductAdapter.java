package com.example.phoneshop.feature_admin_inventory.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.VH> {

    public interface Listener {
        void onToggleVisible(Product p, boolean newState);
        void onEdit(Product p);
        void onDelete(Product p); // Fragment sẽ xử lý dialog xác nhận
    }

    private final List<Product> items = new ArrayList<>();
    private final Listener listener;

    public AdminProductAdapter(Listener l){ this.listener = l; }

    public Product getItem(int position){ return items.get(position); }

    public void removeAt(int position){
        if (position >= 0 && position < items.size()){
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void submit(List<Product> data){
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Product p = items.get(pos);

        h.name.setText(p.getName());
        h.brand.setText(p.getBrand());

        // SỬA LỖI NHỎ: Dùng %,d (cho long) thay vì %,.0f (cho float)
        h.price.setText(String.format("%,d ₫", p.getPrice()));

        h.stock.setText("Tồn: " + p.getStock());

        h.visible.setOnCheckedChangeListener(null);
        h.visible.setChecked(p.isVisible());
        h.visible.setOnCheckedChangeListener((b, checked) -> {
            if (listener != null) listener.onToggleVisible(p, checked);
        });

        // ảnh
        if (p.getImages()!=null && !p.getImages().isEmpty() && p.getImages().get(0) != null){
            Glide.with(h.image.getContext())
                    .load(p.getImages().get(0))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(h.image);
        } else {
            h.image.setImageResource(R.drawable.ic_image_placeholder);
        }

        // click item -> sửa
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(p);
        });

        // click nút thùng rác -> xin xác nhận ở Fragment
        h.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(p);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, brand, price, stock;
        CheckBox visible;
        ImageView btnDelete; // thùng rác trong item_admin_product.xml

        VH(@NonNull View v){
            super(v);
            image = v.findViewById(R.id.imgThumb);
            name = v.findViewById(R.id.tvName);
            brand = v.findViewById(R.id.tvBrand);
            price = v.findViewById(R.id.tvPrice);
            stock = v.findViewById(R.id.tvStock);
            visible = v.findViewById(R.id.cbVisible);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}