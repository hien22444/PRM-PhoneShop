package com.example.phoneshop.feature_shopping.ar_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.phoneshop.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Fragment hiển thị AR View cho sản phẩm
 * 
 * Lưu ý: Đây là implementation cơ bản để demo UI
 * Để sử dụng AR thực sự, cần:
 * 1. Thêm ARCore dependency
 * 2. Tạo 3D models (.glb hoặc .gltf)
 * 3. Implement SceneView với ARCore
 * 4. Xử lý camera permissions và AR session
 */
public class ArViewFragment extends Fragment {

    private NavController navController;
    private NumberFormat currencyFormat;

    // Views
    private MaterialToolbar toolbar;
    private ImageView ivArPreview;
    private TextView tvArDescription;
    private MaterialButton btnStartAr;
    private MaterialCardView cardArInfo;
    private TextView tvProductNameAr;
    private TextView tvProductPriceAr;
    private MaterialButton btnAddToCartAr;
    private MaterialButton btnBuyNowAr;

    // Demo data
    private String productName = "iPhone 15 Pro";
    private long productPrice = 29990000L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ar_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        bindViews(view);
        setupListeners();
        loadProductData();
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        ivArPreview = view.findViewById(R.id.ivArPreview);
        tvArDescription = view.findViewById(R.id.tvArDescription);
        btnStartAr = view.findViewById(R.id.btnStartAr);
        cardArInfo = view.findViewById(R.id.cardArInfo);
        tvProductNameAr = view.findViewById(R.id.tvProductNameAr);
        tvProductPriceAr = view.findViewById(R.id.tvProductPriceAr);
        btnAddToCartAr = view.findViewById(R.id.btnAddToCartAr);
        btnBuyNowAr = view.findViewById(R.id.btnBuyNowAr);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        btnStartAr.setOnClickListener(v -> startArExperience());

        btnAddToCartAr.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        btnBuyNowAr.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chuyển đến thanh toán", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProductData() {
        // Get product data from arguments if available
        if (getArguments() != null) {
            productName = getArguments().getString("product_name", productName);
            productPrice = getArguments().getLong("product_price", productPrice);
        }

        // Load preview image (giả lập AR preview)
        Glide.with(this)
                .load("https://picsum.photos/seed/ar-phone/800/600")
                .into(ivArPreview);

        // Update product info
        tvProductNameAr.setText(productName);
        tvProductPriceAr.setText(currencyFormat.format(productPrice));
    }

    private void startArExperience() {
        // Hiển thị thông báo về AR
        Toast.makeText(getContext(), 
                "Đang khởi động AR...\n\nLưu ý: Tính năng AR thực tế cần:\n" +
                "- ARCore SDK\n" +
                "- 3D Models (.glb/.gltf)\n" +
                "- Camera permissions", 
                Toast.LENGTH_LONG).show();

        // Hiển thị card thông tin sản phẩm
        cardArInfo.setVisibility(View.VISIBLE);

        // Trong thực tế, đây là nơi bạn sẽ:
        // 1. Kiểm tra ARCore availability
        // 2. Request camera permission
        // 3. Initialize AR Session
        // 4. Load 3D model
        // 5. Place model in AR scene
        
        /*
        Example ARCore implementation:
        
        if (isArCoreSupported()) {
            ArFragment arFragment = (ArFragment) getSupportFragmentManager()
                .findFragmentById(R.id.arFragment);
            
            ModelRenderable.builder()
                .setSource(this, Uri.parse("model.glb"))
                .build()
                .thenAccept(renderable -> {
                    // Place model in AR
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Error loading model", Toast.LENGTH_SHORT).show();
                    return null;
                });
        }
        */
    }

    /**
     * Hướng dẫn implement AR thực sự:
     * 
     * 1. Thêm dependencies vào build.gradle:
     *    implementation 'com.google.ar:core:1.40.0'
     *    implementation 'com.google.ar.sceneform:core:1.17.1'
     * 
     * 2. Thêm permissions vào AndroidManifest.xml:
     *    <uses-permission android:name="android.permission.CAMERA" />
     *    <uses-feature android:name="android.hardware.camera.ar" android:required="true"/>
     * 
     * 3. Thêm ARCore metadata:
     *    <meta-data android:name="com.google.ar.core" android:value="required" />
     * 
     * 4. Tạo 3D models:
     *    - Sử dụng Blender hoặc công cụ 3D khác
     *    - Export sang định dạng .glb hoặc .gltf
     *    - Đặt vào thư mục assets/models/
     * 
     * 5. Implement AR Scene:
     *    - Sử dụng ArFragment hoặc ArSceneView
     *    - Load và render 3D models
     *    - Xử lý touch events để xoay/scale model
     *    - Implement plane detection để đặt model
     */
}
