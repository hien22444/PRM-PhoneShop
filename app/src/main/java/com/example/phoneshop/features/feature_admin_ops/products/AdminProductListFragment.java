package com.example.phoneshop.features.feature_admin_ops.products;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.phoneshop.R;
import com.example.phoneshop.common.base.BaseFragment;
import com.example.phoneshop.data.model.Product;
import java.util.ArrayList;
import java.util.List;

public class AdminProductListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private AdminProductAdapter adapter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_admin_product_list; // Đảm bảo layout này tồn tại
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo RecyclerView và Adapter
        recyclerView = view.findViewById(R.id.recycler_products);
        adapter = new AdminProductAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Xử lý sự kiện Xóa/Sửa (Chỉ dùng Xóa theo yêu cầu)
        adapter.setOnProductActionListener(new AdminProductAdapter.OnProductActionListener() {
            @Override
            public void onDeleteClick(Product product) {
                // Logic gọi API xóa sản phẩm
                showToast("Đã gửi yêu cầu xóa sản phẩm: " + product.getName());
            }

            @Override
            public void onEditClick(Product product) {
                // Theo yêu cầu chỉ có Xem & Xóa, nhưng giữ lại logic này nếu bạn muốn mở rộng
                showToast("Tính năng Sửa không được bật cho Admin Ops.");
            }
        });

        // Load mock data
        adapter.setProducts(createMockProducts());
    }

    private List<Product> createMockProducts() {
        // Cần truyền đủ 7 tham số theo Model Product: id, name, brand, price, stock, visible, images
        List<Product> products = new ArrayList<>();
        // List.of() yêu cầu API level 9+, nếu dùng API cũ hơn, dùng Arrays.asList() hoặc tạo ArrayList<String>
        products.add(new Product("P01", "iPhone 15 Pro Max", "Apple", 28990000.0, 50, true, List.of("url1", "url2")));
        products.add(new Product("P02", "Samsung Galaxy S24 Ultra", "Samsung", 25990000.0, 30, true, List.of("url3")));
        products.add(new Product("P03", "Xiaomi 14", "Xiaomi", 18000000.0, 15, false, List.of("url4")));
        return products;
    }
}