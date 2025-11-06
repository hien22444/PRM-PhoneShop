package com.example.phoneshop.feature_shopping.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FlashSaleAdapter extends RecyclerView.Adapter<FlashSaleAdapter.FlashSaleViewHolder> {

    private Context context;
    private List<Product> flashSaleList;
    private OnFlashSaleClickListener listener;
    private NumberFormat currencyFormat;

    public interface OnFlashSaleClickListener {
        void onFlashSaleClick(Product product);
    }

    public FlashSaleAdapter(Context context, List<Product> flashSaleList, OnFlashSaleClickListener listener) {
        this.context = context;
        this.flashSaleList = flashSaleList != null ? flashSaleList : new ArrayList<>();
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public FlashSaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flash_sale, parent, false);
        return new FlashSaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashSaleViewHolder holder, int position) {
        Product product = flashSaleList.get(position);
        
        holder.tvProductName.setText(product.getName());
        holder.tvFlashPrice.setText(currencyFormat.format(product.getFlashSalePrice()));
        holder.tvOriginalPrice.setText(currencyFormat.format(product.getPrice()));
        holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        
        // Tính phần trăm giảm giá
        int discount = (int) (((product.getPrice() - product.getFlashSalePrice()) * 100.0) / product.getPrice());
        holder.tvDiscount.setText("-" + discount + "%");
        
        // Load hình ảnh
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(context)
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.ivFlashSaleImage);
        }
        
        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFlashSaleClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return flashSaleList.size();
    }

    public void updateFlashSales(List<Product> newFlashSales) {
        this.flashSaleList = newFlashSales != null ? newFlashSales : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class FlashSaleViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFlashSaleImage;
        TextView tvProductName;
        TextView tvFlashPrice;
        TextView tvOriginalPrice;
        TextView tvDiscount;

        public FlashSaleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFlashSaleImage = itemView.findViewById(R.id.ivFlashSaleImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvFlashPrice = itemView.findViewById(R.id.tvFlashPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
        }
    }
}
