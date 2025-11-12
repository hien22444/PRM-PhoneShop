package com.example.phoneshop.feature_admin_main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.phoneshop.R;

// File "giả" để test giao diện
public class AdminPlaceholderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Trỏ đến file layout "giả" bạn vừa tạo ở Bước 1
        return inflater.inflate(R.layout.fragment_admin_placeholder, container, false);
    }
}