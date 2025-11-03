package com.example.phoneshop.features.feature_cart;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.R;

public class PaymentWebViewFragment extends Fragment {

    // URL trả về mà bạn đăng ký với PayOS
    private static final String RETURN_URL_SUCCESS = "my-app-scheme://payment-success";
    private static final String RETURN_URL_CANCEL = "my-app-scheme://payment-cancel";

    private WebView webView;
    private ProgressBar progressBar; // Thêm ProgressBar
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

        // Cài đặt WebView
        webView.getSettings().setJavaScriptEnabled(true);

        // Lấy URL từ CheckoutFragment truyền sang
        String paymentUrl = getArguments().getString("payment_url");
        if (paymentUrl == null) {
            Toast.makeText(getContext(), "Lỗi: Không có link thanh toán", Toast.LENGTH_SHORT).show();
            navController.popBackStack();
            return;
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi tải
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
                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar khi tải xong
            }
        });

        // Tải link thanh toán
        webView.loadUrl(paymentUrl);
    }

    private void handlePaymentSuccess() {
        cartViewModel.clearCart();

        // Bạn cần tạo Action này trong navgraph
        navController.navigate(R.id.action_paymentWebViewFragment_to_orderSuccessFragment);
    }
}