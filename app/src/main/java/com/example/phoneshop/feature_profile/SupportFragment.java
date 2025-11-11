package com.example.phoneshop.feature_profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class SupportFragment extends Fragment {

    private NavController navController;
    
    // Views
    private MaterialToolbar toolbar;
    private MaterialCardView cardFAQ;
    private MaterialCardView cardContactUs;
    private MaterialCardView cardEmailSupport;
    private MaterialCardView cardPhoneSupport;
    private MaterialCardView cardLiveChat;
    private MaterialCardView cardUserGuide;
    private MaterialCardView cardReportBug;
    private MaterialCardView cardRateApp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Map views
        toolbar = view.findViewById(R.id.toolbar);
        cardFAQ = view.findViewById(R.id.cardFAQ);
        cardContactUs = view.findViewById(R.id.cardContactUs);
        cardEmailSupport = view.findViewById(R.id.cardEmailSupport);
        cardPhoneSupport = view.findViewById(R.id.cardPhoneSupport);
        cardLiveChat = view.findViewById(R.id.cardLiveChat);
        cardUserGuide = view.findViewById(R.id.cardUserGuide);
        cardReportBug = view.findViewById(R.id.cardReportBug);
        cardRateApp = view.findViewById(R.id.cardRateApp);

        setupToolbar();
        setupClickListeners();
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
    }

    private void setupClickListeners() {
        // FAQ
        cardFAQ.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đang mở câu hỏi thường gặp...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to FAQ fragment or open web page
        });

        // Contact Us
        cardContactUs.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Thông tin liên hệ:\nĐịa chỉ: 123 Đường ABC, Quận 1, TP.HCM\nGiờ làm việc: 8:00 - 22:00", Toast.LENGTH_LONG).show();
        });

        // Email Support
        cardEmailSupport.setOnClickListener(v -> {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:support@phoneshop.vn"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ PhoneShop");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Xin chào,\n\nTôi cần hỗ trợ về:\n\n");
                startActivity(Intent.createChooser(emailIntent, "Gửi email"));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Không thể mở ứng dụng email", Toast.LENGTH_SHORT).show();
            }
        });

        // Phone Support
        cardPhoneSupport.setOnClickListener(v -> {
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:1900123456"));
                startActivity(callIntent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show();
            }
        });

        // Live Chat
        cardLiveChat.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng chat trực tuyến sẽ sớm có mặt!", Toast.LENGTH_SHORT).show();
            // TODO: Implement live chat functionality
        });

        // User Guide
        cardUserGuide.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Hướng dẫn sử dụng:\n\n1. Duyệt sản phẩm tại trang chủ\n2. Thêm vào giỏ hàng\n3. Thanh toán\n4. Theo dõi đơn hàng", Toast.LENGTH_LONG).show();
        });

        // Report Bug
        cardReportBug.setOnClickListener(v -> {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:bug@phoneshop.vn"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Báo lỗi PhoneShop App");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Mô tả lỗi:\n\nCác bước tái tạo lỗi:\n1. \n2. \n3. \n\nThiết bị: \nPhiên bản app: 1.0.0");
                startActivity(Intent.createChooser(emailIntent, "Báo lỗi"));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Không thể mở ứng dụng email", Toast.LENGTH_SHORT).show();
            }
        });

        // Rate App
        cardRateApp.setOnClickListener(v -> {
            try {
                // Try to open Play Store
                Intent rateIntent = new Intent(Intent.ACTION_VIEW);
                rateIntent.setData(Uri.parse("market://details?id=" + requireActivity().getPackageName()));
                startActivity(rateIntent);
            } catch (Exception e) {
                try {
                    // Fallback to web browser
                    Intent rateIntent = new Intent(Intent.ACTION_VIEW);
                    rateIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().getPackageName()));
                    startActivity(rateIntent);
                } catch (Exception ex) {
                    Toast.makeText(getContext(), "Cảm ơn bạn đã muốn đánh giá ứng dụng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
