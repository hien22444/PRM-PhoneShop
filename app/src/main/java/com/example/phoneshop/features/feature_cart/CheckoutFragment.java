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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

        //
        // THÊM 3 DÒNG NÀY VÀO (Hoặc đảm bảo bạn có chúng)
        //
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Kiểm tra tính hợp lệ (Validate)
        // Bây giờ dòng này sẽ hết lỗi
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

        // --- BƯỚC 1: GỌI API ĐỂ TẠO LINK THANH TOÁN ---
        // (Code giả lập từ lần trước)
        String giaLapPaymentUrl = "https://pay.payos.vn/dummy-link-12345";

        if (giaLapPaymentUrl != null) {
            Bundle bundle = new Bundle();
            bundle.putString("payment_url", giaLapPaymentUrl);
            navController.navigate(R.id.action_checkoutFragment_to_paymentWebViewFragment, bundle);
        } else {
            Toast.makeText(getContext(), "Không thể tạo đơn hàng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
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
}