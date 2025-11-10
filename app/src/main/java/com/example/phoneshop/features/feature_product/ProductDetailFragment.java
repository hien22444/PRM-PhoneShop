package com.example.phoneshop.features.feature_product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.features.feature_cart.CartViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailFragment extends Fragment {

    private CartViewModel cartViewModel;
    private TextView tvProductName, tvProductPrice;
    private MaterialButton btnAddToCart, btnBuyNow;
    private android.widget.ImageView imgProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.initialize(requireContext());

        // Map views
        tvProductName = view.findViewById(R.id.tvProductName);
        tvProductPrice = view.findViewById(R.id.tvProductPrice);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
        btnBuyNow = view.findViewById(R.id.btnBuyNow);
        imgProduct = view.findViewById(R.id.imgProduct);

        // Create mock product for testing
        Product mockProduct = createMockProduct();
        
        // Display product info
        tvProductName.setText(mockProduct.getName());
        tvProductPrice.setText(String.format("%,.0f₫", mockProduct.getPrice()));
        
        // Load product image with Glide
        if (mockProduct.getImages() != null && !mockProduct.getImages().isEmpty()) {
            String imageUrl = mockProduct.getImages().get(0);
            android.util.Log.d("ProductDetail", "Loading image: " + imageUrl);
            
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .into(imgProduct);
        } else {
            android.util.Log.d("ProductDetail", "No images available, using placeholder");
            imgProduct.setImageResource(R.drawable.placeholder_product);
        }

        // Add to cart button
        btnAddToCart.setOnClickListener(v -> {
            cartViewModel.addProductToCart(mockProduct, 1);
            Toast.makeText(getContext(), "Đã thêm " + mockProduct.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
        
        // Buy now button
        btnBuyNow.setOnClickListener(v -> {
            // Add to cart first
            cartViewModel.addProductToCart(mockProduct, 1);
            
            // Navigate to cart immediately
            try {
                androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(v);
                navController.navigate(R.id.cartFragment);
                Toast.makeText(getContext(), "Đã thêm vào giỏ hàng và chuyển đến thanh toán", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // Navigation fallback
                Toast.makeText(getContext(), "Đã thêm " + mockProduct.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Product createMockProduct() {
        Product product = new Product();
        product.setId("test_product_1");
        product.setName("iPhone 15 Pro Max");
        product.setPrice(29990000);
        product.setDescription("Điện thoại iPhone 15 Pro Max 256GB");
        
        List<String> images = new ArrayList<>();
        // Add real iPhone 15 Pro Max image URL
        images.add("https://cdn.tgdd.vn/Products/Images/42/305658/iphone-15-pro-max-blue-thumbnew-600x600.jpg");
        product.setImages(images);
        
        return product;
    }
}
