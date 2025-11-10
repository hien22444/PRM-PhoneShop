package com.example.phoneshop.features.feature_cart;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.CartItem;
import com.example.phoneshop.features.feature_cart.adapters.CartAdapter;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

// (Ghi chú: Bạn nên kế thừa từ BaseFragment nếu có)
// Chúng ta implement Interface của Adapter để lắng nghe sự kiện
public class CartFragment extends Fragment implements CartAdapter.CartItemListener {

    // 1. Khai báo các biến
    private CartViewModel viewModel;
    private CartAdapter cartAdapter;
    private NavController navController;
    private SharedPreferences sharedPreferences;

    // Các View trong XML
    private RecyclerView rvCartItems;
    private TextView tvSubtotalPrice;
    private TextView tvTotalPrice;
    private TextView tvEmptyCart;
    private MaterialButton btnCheckout;
    private View bottomSummaryCard; // Khung tổng tiền

    // Bộ format tiền tệ
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate (nạp) layout XML
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo NavController trước
        navController = Navigation.findNavController(view);
        
        // Kiểm tra đăng nhập
        if (!checkLoginStatus()) {
            return;
        }

        // 2. Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        
        // Initialize cart with context
        viewModel.initialize(requireContext());

        // 3. Ánh xạ Views (Tìm các view từ XML)
        rvCartItems = view.findViewById(R.id.rvCartItems);
        tvSubtotalPrice = view.findViewById(R.id.tvSubtotalPrice);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        bottomSummaryCard = view.findViewById(R.id.bottomSummaryCard);

        // 4. Cài đặt RecyclerView
        setupRecyclerView();

        // 5. Lắng nghe (Observe) dữ liệu từ ViewModel
        observeViewModel();

        // 6. Cài đặt các sự kiện Click
        btnCheckout.setOnClickListener(v -> {
            // Kiểm tra lại đăng nhập trước khi checkout
            if (!checkLoginStatus()) {
                return;
            }
            
            // Check if cart is empty
            if (viewModel.getCartItems().getValue() == null || 
                viewModel.getCartItems().getValue().isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Navigate to checkout
            try {
                navController.navigate(R.id.action_cartFragment_to_checkoutFragment);
            } catch (Exception e) {
                // Fallback if navigation fails
                Toast.makeText(getContext(), "Đang phát triển tính năng thanh toán", Toast.LENGTH_SHORT).show();
                android.util.Log.e("CartFragment", "Navigation error: " + e.getMessage());
            }
        });

        // (Bạn cũng có thể cài đặt sự kiện cho nút back trên toolbar ở đây)
    }

    @Override
    public void onResume() {
        super.onResume();
        // Kiểm tra lại đăng nhập khi fragment hiển thị lại
        if (!checkLoginStatus()) {
            return;
        }
        // Reload cart khi quay lại fragment để đồng bộ với server
        if (viewModel != null) {
            viewModel.loadCartItems();
        }
    }

    private boolean checkLoginStatus() {
        sharedPreferences = requireActivity().getSharedPreferences("PhoneShopPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        
        if (!isLoggedIn) {
            // Nếu chưa đăng nhập, quay về màn hình trước và hiển thị thông báo
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();
            if (navController != null && getView() != null) {
                // Quay về màn hình trước (thường là homeFragment hoặc productDetailFragment)
                if (navController.getCurrentDestination() != null) {
                    navController.popBackStack();
                }
            }
            return false;
        }
        return true;
    }

    private void setupRecyclerView() {
        // "this" ở đây nghĩa là "CartFragment" sẽ lắng nghe các sự kiện click
        cartAdapter = new CartAdapter(getContext(), new ArrayList<>(), this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCartItems.setAdapter(cartAdapter);
    }

    private void observeViewModel() {
        // A. Lắng nghe danh sách sản phẩm
        viewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems != null) {
                // Khi danh sách thay đổi, cập nhật Adapter
                cartAdapter.updateCartItems(cartItems);
            }
        });

        // B. Lắng nghe tổng tiền
        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> {
            if (totalPrice != null) {
                // Khi tổng tiền thay đổi, cập nhật giao diện
                String formattedPrice = currencyFormat.format(totalPrice);
                tvSubtotalPrice.setText(formattedPrice);
                tvTotalPrice.setText(formattedPrice);
            }
        });

        // C. Lắng nghe trạng thái giỏ hàng (trống/không)
        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            if (isEmpty != null) {
                if (isEmpty) {
                    tvEmptyCart.setVisibility(View.VISIBLE);
                    rvCartItems.setVisibility(View.GONE);
                    bottomSummaryCard.setVisibility(View.GONE);
                } else {
                    tvEmptyCart.setVisibility(View.GONE);
                    rvCartItems.setVisibility(View.VISIBLE);
                    bottomSummaryCard.setVisibility(View.VISIBLE);
                }
            }
        });
        
        // D. Lắng nghe trạng thái loading
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Có thể hiển thị progress bar nếu cần
        });
        
        // E. Lắng nghe lỗi
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty() && !error.equals("")) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 7. Xử lý các sự kiện click từ Adapter (Vì đã implement Interface)

    @Override
    public void onIncreaseClick(CartItem item) {
        // KHÔNG xử lý logic ở đây.
        // Chỉ "báo" cho ViewModel biết.
        viewModel.onIncreaseClick(item);
    }

    @Override
    public void onDecreaseClick(CartItem item) {
        // Báo cho ViewModel
        viewModel.onDecreaseClick(item);
    }

    @Override
    public void onDeleteClick(CartItem item) {
        // Báo cho ViewModel
        viewModel.onDeleteClick(item);
        Toast.makeText(getContext(), "Đã xóa " + item.getProductName(), Toast.LENGTH_SHORT).show();
    }
}