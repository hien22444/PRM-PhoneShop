package com.example.phoneshop.features.feature_cart;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.example.phoneshop.R;

public class PaymentWebViewFragment extends Fragment {

    // URL trả về mà bạn đăng ký với PayOS
    private static final String RETURN_URL_SUCCESS = "phoneshop://payment-success";
    private static final String RETURN_URL_CANCEL = "phoneshop://payment-cancel";

    private WebView webView;
    private ProgressBar progressBar;
    private TextView tvTotalAmount; // Hiển thị tổng tiền
    private NavController navController;
    private CartViewModel cartViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp layout XML bạn vừa tạo
        return inflater.inflate(R.layout.fragment_payment_webview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        // Ánh xạ View từ layout
        webView = view.findViewById(R.id.webViewPayment);
        progressBar = view.findViewById(R.id.progressBar);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);

        // Check if essential views are found
        if (webView == null) {
            Toast.makeText(getContext(), "Lỗi: Không thể tải giao diện thanh toán", Toast.LENGTH_SHORT).show();
            navController.popBackStack();
            return;
        }

        // Cài đặt WebView
        webView.getSettings().setJavaScriptEnabled(true);

        // Lấy dữ liệu từ CheckoutFragment
        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(getContext(), "Lỗi: Không có thông tin thanh toán", Toast.LENGTH_SHORT).show();
            navController.popBackStack();
            return;
        }
        
        String paymentUrl = args.getString("payment_url");
        String orderId = args.getString("order_id");
        double totalAmount = args.getDouble("total_amount", 0);
        
        if (paymentUrl == null) {
            Toast.makeText(getContext(), "Lỗi: Không có link thanh toán", Toast.LENGTH_SHORT).show();
            navController.popBackStack();
            return;
        }
        
        // Hiển thị tổng tiền
        if (tvTotalAmount != null) {
            tvTotalAmount.setText(String.format("Tổng tiền: %,.0f₫", totalAmount));
        } else {
            android.util.Log.w("PaymentWebView", "tvTotalAmount not found in layout");
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi tải
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(RETURN_URL_SUCCESS)) {
                    handlePaymentSuccess();
                    return true;
                } else if (url.startsWith(RETURN_URL_CANCEL)) {
                    Toast.makeText(getContext(), "Thanh toán đã bị hủy", Toast.LENGTH_SHORT).show();
                    navController.popBackStack();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE); // Ẩn ProgressBar khi tải xong
                }
            }
        });

        // Tải link thanh toán
        webView.loadUrl(paymentUrl);
    }

    private void handlePaymentSuccess() {
        Toast.makeText(getContext(), "Thanh toán thành công!", Toast.LENGTH_LONG).show();
        
        // Get order ID from arguments to update status
        Bundle args = getArguments();
        if (args != null) {
            String orderId = args.getString("order_id");
            if (orderId != null) {
                // Update order status to "Đã thanh toán"
                com.example.phoneshop.service.OrderStorageService orderStorage = 
                    com.example.phoneshop.service.OrderStorageService.getInstance();
                orderStorage.initialize(requireContext());
                orderStorage.updateOrderStatus(orderId, "Đã thanh toán");
                
                android.util.Log.d("PaymentWebView", "Updated order status: " + orderId + " -> Đã thanh toán");
            }
        }
        
        // Clear cart after successful payment
        cartViewModel.clearCart();

        // Show review prompt dialog
        showReviewPromptDialog(args);
    }

    private void showReviewPromptDialog(Bundle args) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đánh giá sản phẩm")
                .setMessage("Bạn có muốn đánh giá sản phẩm vừa mua không? Đánh giá của bạn sẽ giúp người khác có thêm thông tin tham khảo.")
                .setPositiveButton("Đánh giá ngay", (dialog, which) -> {
                    // Navigate to review fragment
                    navigateToReview(args);
                })
                .setNegativeButton("Để sau", (dialog, which) -> {
                    // Navigate to order history
                    navigateToOrderHistory();
                })
                .setCancelable(false)
                .show();
    }

    private void navigateToReview(Bundle args) {
        if (args != null) {
            String orderId = args.getString("order_id");
            // For simplicity, we'll navigate to review for the first product
            // In a real app, you might show a list of products to review
            
            Bundle reviewBundle = new Bundle();
            reviewBundle.putString("order_id", orderId);
            reviewBundle.putString("product_id", "sample_product_id"); // Should get from order items
            reviewBundle.putString("product_name", "Sản phẩm vừa mua"); // Should get from order items
            reviewBundle.putString("product_image_url", ""); // Should get from order items
            
            try {
                navController.navigate(R.id.action_paymentWebViewFragment_to_reviewFragment, reviewBundle);
            } catch (Exception e) {
                android.util.Log.e("PaymentWebView", "Error navigating to review", e);
                navigateToOrderHistory();
            }
        } else {
            navigateToOrderHistory();
        }
    }

    private void navigateToOrderHistory() {
        try {
            navController.navigate(R.id.orderHistoryFragment);
        } catch (Exception e) {
            // Fallback navigation
            Toast.makeText(getContext(), "Đơn hàng đã được thanh toán thành công!", Toast.LENGTH_SHORT).show();
            navController.popBackStack(R.id.homeFragment, false);
        }
    }
}