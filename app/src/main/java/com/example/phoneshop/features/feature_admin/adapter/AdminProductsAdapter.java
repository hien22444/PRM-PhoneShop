package com.example.phoneshop.features.feature_admin.adapter;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying products in admin product management
 */
public class AdminProductsAdapter extends RecyclerView.Adapter<AdminProductsAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnProductActionListener listener;
    private NumberFormat currencyFormat;

    public interface OnProductActionListener {
        void onProductClick(Product product);
        void onEditProductClick(Product product);
        void onDeleteProductClick(Product product);
        void onToggleVisibilityClick(Product product);
    }

    public AdminProductsAdapter(List<Product> products, OnProductActionListener listener) {
        this.products = products;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> newProducts) {
        if (this.products != null && newProducts != null) {
            int startPosition = this.products.size();
            this.products.addAll(newProducts);
            notifyItemRangeInserted(startPosition, newProducts.size());
        }
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardProduct;
        private ImageView ivProductImage;
        private TextView tvProductId;
        private TextView tvProductName;
        private TextView tvProductBrand;
        private TextView tvProductPrice;
        private TextView tvProductStock;
        private Chip chipVisibility;
        private MaterialButton btnEdit;
        private MaterialButton btnDelete;
        private MaterialButton btnToggleVisibility;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProduct = itemView.findViewById(R.id.cardProduct);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductBrand = itemView.findViewById(R.id.tvProductBrand);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductStock = itemView.findViewById(R.id.tvProductStock);
            chipVisibility = itemView.findViewById(R.id.chipVisibility);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnToggleVisibility = itemView.findViewById(R.id.btnToggleVisibility);

            setupClickListeners();
        }

        private void setupClickListeners() {
            cardProduct.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onProductClick(products.get(getAdapterPosition()));
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onEditProductClick(products.get(getAdapterPosition()));
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onDeleteProductClick(products.get(getAdapterPosition()));
                }
            });

            btnToggleVisibility.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onToggleVisibilityClick(products.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Product product) {
            tvProductId.setText("ID: " + product.getId());
            tvProductName.setText(product.getName());
            tvProductBrand.setText(product.getBrand());
            
            // Format price
            String formattedPrice = formatCurrency(product.getPrice());
            tvProductPrice.setText(formattedPrice);
            
            // Stock with color coding
            tvProductStock.setText("Tồn kho: " + product.getStock());
            setStockColor(product.getStock());
            
            // Visibility chip
            setVisibilityChip(product.isVisible());
            
            // Load product image
            loadProductImage(product);
            
            // Update button states
            updateButtonStates(product);
        }

        private String formatCurrency(long amount) {
            try {
                return currencyFormat.format(amount).replace("₫", "VND");
            } catch (Exception e) {
                return String.format("%,d VND", amount);
            }
        }

        private void setStockColor(int stock) {
            int colorResId;
            if (stock == 0) {
                colorResId = R.color.status_cancelled; // Red for out of stock
            } else if (stock < 10) {
                colorResId = R.color.status_processing; // Orange for low stock
            } else {
                colorResId = R.color.status_completed; // Green for good stock
            }
            
            try {
                int color = itemView.getContext().getResources().getColor(colorResId, null);
                tvProductStock.setTextColor(color);
            } catch (Exception e) {
                // Fallback to default color
            }
        }

        private void setVisibilityChip(boolean isVisible) {
            chipVisibility.setText(isVisible ? "Hiển thị" : "Ẩn");
            
            int chipBackgroundColorResId = isVisible ? R.color.status_completed_bg : R.color.status_cancelled_bg;
            int chipTextColorResId = isVisible ? R.color.status_completed : R.color.status_cancelled;
            
            try {
                int textColor = itemView.getContext().getResources().getColor(chipTextColorResId, null);
                chipVisibility.setChipBackgroundColorResource(chipBackgroundColorResId);
                chipVisibility.setTextColor(textColor);
            } catch (Exception e) {
                // Fallback to default colors
            }
        }

        private void loadProductImage(Product product) {
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                String imageUrl = product.getImages().get(0);
                
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_error)
                        .centerCrop()
                        .into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        private void updateButtonStates(Product product) {
            // Update toggle visibility button text
            btnToggleVisibility.setText(product.isVisible() ? "Ẩn" : "Hiện");
            
            // All buttons are enabled by default
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
            btnToggleVisibility.setEnabled(true);
        }
    }
}
