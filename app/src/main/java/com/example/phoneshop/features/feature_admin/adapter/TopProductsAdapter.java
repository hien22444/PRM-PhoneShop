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
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying top products in admin statistics
 */
public class TopProductsAdapter extends RecyclerView.Adapter<TopProductsAdapter.TopProductViewHolder> {

    private List<AdminStatsResponse.TopProduct> topProducts;
    private NumberFormat currencyFormat;

    public TopProductsAdapter(List<AdminStatsResponse.TopProduct> topProducts) {
        this.topProducts = topProducts;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public TopProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_product, parent, false);
        return new TopProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopProductViewHolder holder, int position) {
        AdminStatsResponse.TopProduct topProduct = topProducts.get(position);
        holder.bind(topProduct, position + 1);
    }

    @Override
    public int getItemCount() {
        return topProducts != null ? topProducts.size() : 0;
    }

    public void updateTopProducts(List<AdminStatsResponse.TopProduct> newTopProducts) {
        this.topProducts = newTopProducts;
        notifyDataSetChanged();
    }

    class TopProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRank;
        private TextView tvProductName;
        private TextView tvProductId;
        private TextView tvTotalQuantity;
        private TextView tvTotalRevenue;

        public TopProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvTotalQuantity = itemView.findViewById(R.id.tvTotalQuantity);
            tvTotalRevenue = itemView.findViewById(R.id.tvTotalRevenue);
        }

        public void bind(AdminStatsResponse.TopProduct topProduct, int rank) {
            tvRank.setText("#" + rank);
            tvProductName.setText(topProduct.getName());
            tvProductId.setText("ID: " + topProduct.getProductId());
            tvTotalQuantity.setText("Đã bán: " + topProduct.getTotalQuantity());
            
            // Format revenue
            String formattedRevenue = formatCurrency(topProduct.getTotalRevenue());
            tvTotalRevenue.setText("Doanh thu: " + formattedRevenue);
            
            // Set rank styling
            setRankStyling(rank);
        }

        private String formatCurrency(long amount) {
            try {
                return currencyFormat.format(amount).replace("₫", "VND");
            } catch (Exception e) {
                return String.format("%,d VND", amount);
            }
        }

        private void setRankStyling(int rank) {
            // Style the rank number based on position
            int rankColorResId;
            switch (rank) {
                case 1:
                    rankColorResId = R.color.rank_first; // Gold
                    break;
                case 2:
                    rankColorResId = R.color.rank_second; // Silver
                    break;
                case 3:
                    rankColorResId = R.color.rank_third; // Bronze
                    break;
                default:
                    rankColorResId = R.color.rank_default; // Default
                    break;
            }
            
            try {
                int color = itemView.getContext().getResources().getColor(rankColorResId, null);
                tvRank.setTextColor(color);
            } catch (Exception e) {
                // Fallback to default color if resource not found
            }
        }
    }
}
