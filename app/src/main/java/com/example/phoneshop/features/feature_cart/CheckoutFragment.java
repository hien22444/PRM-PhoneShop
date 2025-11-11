package com.example.phoneshop.features.feature_cart;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.R;
import com.example.phoneshop.service.PayOSService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class CheckoutFragment extends Fragment {

    // 1. Khai báo các biến
    // Chúng ta sẽ dùng CartViewModel để lấy thông tin giỏ hàng
    private CartViewModel cartViewModel;
    private NavController navController;

    // Các View trong XML
    private TextInputLayout tilFullName, tilPhoneNumber, tilAddress;
    private TextInputEditText etFullName, etPhoneNumber, etAddress;

    private RadioGroup rgPaymentMethod;
    private MaterialButton btnPlaceOrder;
    private MaterialToolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. Khởi tạo ViewModel (Dùng chung với CartFragment)
        // Dùng "requireActivity()" để 2 Fragment dùng chung 1 ViewModel
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        // Khởi tạo NavController
        navController = Navigation.findNavController(view);

        // 3. Ánh xạ Views
        toolbar = view.findViewById(R.id.toolbar);
        tilFullName = view.findViewById(R.id.tilFullName);
        tilPhoneNumber = view.findViewById(R.id.tilPhoneNumber);
        tilAddress = view.findViewById(R.id.tilAddress);
        etFullName = view.findViewById(R.id.etFullName);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etAddress = view.findViewById(R.id.etAddress);
        rgPaymentMethod = view.findViewById(R.id.rgPaymentMethod);
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);

        // 4. Cài đặt sự kiện
        // Nút Back trên Toolbar
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        // Nút "Xác nhận Đặt hàng"
        btnPlaceOrder.setOnClickListener(v -> {
            placeOrder();
        });
    }

    // 5. Hàm xử lý logic đặt hàng
//    private void placeOrder() {
//        // Lấy dữ liệu từ các ô
//        String fullName = etFullName.getText().toString().trim();
//        String phone = etPhoneNumber.getText().toString().trim();
//        String address = etAddress.getText().toString().trim();
//
//        // Kiểm tra tính hợp lệ (Validate)
//        if (!validateForm(fullName, phone, address)) {
//            return; // Dừng lại nếu form không hợp lệ
//        }
//
//        // Lấy phương thức thanh toán
//        String paymentMethod = "COD"; // Mặc định
//        int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
//        if (selectedPaymentId == R.id.rbBankTransfer) {
//            paymentMethod = "Chuyển khoản";
//        } else if (selectedPaymentId == R.id.rbOnlineWallet) {
//            paymentMethod = "Ví điện tử";
//        }
//
//        // --- ĐÂY LÀ LÚC GỌI API ĐỂ TẠO ĐƠN HÀNG ---
//        // Bạn sẽ lấy dữ liệu này, cộng với danh sách sản phẩm từ
//        // cartViewModel.getCartItems().getValue()
//        // và gửi tất cả lên server (API /orders/create)
//
//        // Giả lập thành công:
//        Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
//
//        // Xóa giỏ hàng (trong ViewModel)
//        // cartViewModel.clearCart(); // Bạn cần tự viết hàm này trong ViewModel
//
//        // Quay về màn hình Lịch sử đơn hàng (hoặc Trang chủ)
//        // Chúng ta cần thêm màn hình Lịch sử vào navgraph và tạo Action cho nó
//        // Tạm thời, chúng ta sẽ quay lại CartFragment
//        navController.popBackStack(); // Quay lại màn hình trước đó
//
//        // (Cách tốt hơn là điều hướng tới OrderHistoryFragment)
//        // navController.navigate(R.id.action_checkoutFragment_to_orderHistoryFragment);
//    }
// ... (bên trong CheckoutFragment.java)

    // Hàm xử lý logic đặt hàng (CẬP NHẬT LẠI)
    // ... (bên trong CheckoutFragment.java)

    // 6. Hàm xử lý logic đặt hàng (CẬP NHẬT LẠI)
    private void placeOrder() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Kiểm tra tính hợp lệ (Validate)
        if (!validateForm(fullName, phone, address)) {
            return; // Dừng lại nếu form không hợp lệ
        }

        // Lấy phương thức thanh toán
        String paymentMethod = "COD"; // Mặc định
        int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedPaymentId == R.id.rbBankTransfer) {
            paymentMethod = "Chuyển khoản";
        } else if (selectedPaymentId == R.id.rbOnlineWallet) {
            paymentMethod = "Ví điện tử";
        }

        // Lấy danh sách sản phẩm từ giỏ hàng
        List<com.example.phoneshop.data.model.CartItem> cartItems = cartViewModel.getCartItems().getValue();
        android.util.Log.d("CheckoutFragment", "Cart items: " + (cartItems != null ? cartItems.size() : "null"));
        
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo OrderRequest
        com.example.phoneshop.data.model.OrderRequest request = new com.example.phoneshop.data.model.OrderRequest();
        request.setFullName(fullName);
        request.setPhone(phone);
        request.setAddress(address);
        request.setPaymentMethod(paymentMethod);

        // Chuyển đổi CartItem thành OrderItemRequest
        List<com.example.phoneshop.data.model.OrderRequest.OrderItemRequest> orderItems = new java.util.ArrayList<>();
        for (com.example.phoneshop.data.model.CartItem item : cartItems) {
            android.util.Log.d("CheckoutFragment", "Adding item: " + item.getProductName() + " x" + item.getQuantity());
            orderItems.add(new com.example.phoneshop.data.model.OrderRequest.OrderItemRequest(
                item.getProductId(),
                item.getQuantity(),
                item.getPrice()
            ));
        }
        request.setItems(orderItems);

        android.util.Log.d("CheckoutFragment", "Order request created - Items: " + orderItems.size() + ", Payment: " + paymentMethod);

        // Gọi API để tạo đơn hàng
        createOrder(request, paymentMethod, cartItems);
    }

    private void createOrder(com.example.phoneshop.data.model.OrderRequest request, String paymentMethod, List<com.example.phoneshop.data.model.CartItem> cartItems) {
        // Disable button để tránh double click
        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Đang xử lý...");
        
        // Sử dụng OrderRepository để tạo đơn hàng
        com.example.phoneshop.data.repository.OrderRepository orderRepository = 
            com.example.phoneshop.data.repository.OrderRepository.getInstance();
        
        androidx.lifecycle.LiveData<com.example.phoneshop.data.model.Order> orderLiveData = 
            orderRepository.createOrder(request);
        
        orderLiveData.observe(getViewLifecycleOwner(), order -> {
            btnPlaceOrder.setEnabled(true);
            btnPlaceOrder.setText("Xác nhận đặt hàng");
            
            android.util.Log.d("CheckoutFragment", "Order result: " + (order != null ? order.getOrderId() : "null"));
            
            if (order != null) {
                // Đơn hàng được tạo thành công
                android.util.Log.d("CheckoutFragment", "Order created successfully: " + order.getOrderId());
                Toast.makeText(getContext(), "Đặt hàng thành công! Mã đơn: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
                
                // Lưu đơn hàng vào storage
                com.example.phoneshop.service.OrderStorageService orderStorage = 
                    com.example.phoneshop.service.OrderStorageService.getInstance();
                orderStorage.initialize(requireContext());
                orderStorage.saveOrder(order);
                
                // Xóa giỏ hàng sau khi đặt hàng thành công
                cartViewModel.clearCart();
                
                if (paymentMethod.equals("COD")) {
                    // COD - Thanh toán khi nhận hàng
                    Toast.makeText(getContext(), "Đặt hàng COD thành công! Bạn sẽ thanh toán khi nhận hàng.", Toast.LENGTH_LONG).show();
                    
                    // Chuyển vào đơn hàng
                    try {
                        navController.navigate(R.id.orderHistoryFragment);
                    } catch (Exception e) {
                        // Fallback nếu navigation fail
                        Toast.makeText(getContext(), "Đơn hàng đã được tạo thành công!", Toast.LENGTH_SHORT).show();
                        navController.popBackStack(R.id.homeFragment, false);
                    }
                } else {
                    // Thanh toán online với PayOS (Chuyển khoản hoặc Ví điện tử)
                    processPayOSPayment(order, cartItems);
                }
            } else {
                android.util.Log.e("CheckoutFragment", "Order creation failed - order is null");
                Toast.makeText(getContext(), "Không thể tạo đơn hàng, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // 6. Hàm kiểm tra form
    private boolean validateForm(String fullName, String phone, String address) {
        // Reset lỗi cũ
        tilFullName.setError(null);
        tilPhoneNumber.setError(null);
        tilAddress.setError(null);

        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Vui lòng nhập họ tên");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            tilPhoneNumber.setError("Vui lòng nhập số điện thoại");
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            tilAddress.setError("Vui lòng nhập địa chỉ");
            return false;
        }

        // (Bạn có thể thêm logic kiểm tra SĐT hợp lệ, v.v...)

        return true;
    }

    private void processPayOSPayment(com.example.phoneshop.data.model.Order order, List<com.example.phoneshop.data.model.CartItem> cartItems) {
        // Khởi tạo PayOS service
        PayOSService payOSService = PayOSService.getInstance();
        payOSService.initialize(requireContext());
        
        // Tính tổng tiền từ cart items
        double tempTotal = 0;
        for (com.example.phoneshop.data.model.CartItem item : cartItems) {
            tempTotal += item.getPrice() * item.getQuantity();
        }
        final double totalAmount = tempTotal; // Make it final for lambda
        
        // Tạo payment link với tổng tiền
        payOSService.createPaymentLink(order, cartItems).observe(getViewLifecycleOwner(), paymentUrl -> {
            if (paymentUrl != null && !paymentUrl.isEmpty()) {
                // Chuyển đến màn hình PayOS QR với payment URL và tổng tiền
                Toast.makeText(getContext(), "Chuyển đến màn hình thanh toán PayOS...", Toast.LENGTH_SHORT).show();
                
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("payment_url", paymentUrl);
                    bundle.putString("order_id", order.getOrderId());
                    bundle.putDouble("total_amount", totalAmount);
                    
                    // Navigate to PayOS WebView Fragment
                    navController.navigate(R.id.paymentWebViewFragment, bundle);
                } catch (Exception e) {
                    // Fallback - show payment URL in toast
                    Toast.makeText(getContext(), "Link thanh toán: " + paymentUrl, Toast.LENGTH_LONG).show();
                    android.util.Log.e("CheckoutFragment", "Navigation error: " + e.getMessage());
                    
                    // Simulate successful payment for testing
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(() -> {
                        Toast.makeText(getContext(), "Thanh toán thành công! Chuyển đến đơn hàng...", Toast.LENGTH_SHORT).show();
                        try {
                            navController.navigate(R.id.orderHistoryFragment);
                        } catch (Exception ex) {
                            navController.popBackStack(R.id.homeFragment, false);
                        }
                    }, 3000); // 3 seconds delay
                }
            } else {
                Toast.makeText(getContext(), "Không thể tạo link thanh toán, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                btnPlaceOrder.setEnabled(true);
                btnPlaceOrder.setText("Xác nhận đặt hàng");
            }
        });
    }
}