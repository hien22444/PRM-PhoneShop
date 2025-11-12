package com.example.phoneshop.features.feature_admin_ops.users;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.phoneshop.R;
import com.example.phoneshop.common.base.BaseFragment; // Đã thêm
import com.example.phoneshop.data.model.User; // Đã thêm
import java.util.ArrayList;
import java.util.List;

public class AdminUserListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private AdminUserAdapter adapter; // Cần tạo AdminUserAdapter

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_admin_user_list; // Đảm bảo layout này tồn tại
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_users); // Đảm bảo ID này tồn tại
        adapter = new AdminUserAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Xử lý sự kiện Chặn/Bỏ chặn
        adapter.setOnUserActionListener(user -> {
            boolean isBlocked = !user.isBlocked();
            // Cập nhật trạng thái trong model ngay lập tức để phản ánh trên UI
            user.setBlocked(isBlocked);
            adapter.notifyDataSetChanged();

            String action = isBlocked ? "Chặn" : "Bỏ chặn";
            // Logic gọi API blockUser
            showToast(action + " người dùng: " + user.getUsername());
        });

        // Load mock data
        adapter.setUsers(createMockUsers());
    }

    private List<User> createMockUsers() {
        // Cần truyền đủ 4 tham số theo Model User: id, username, rules, isBlocked
        List<User> users = new ArrayList<>();
        users.add(new User("U001", "admin_user", "Admin", false));
        users.add(new User("U002", "john_customer", "User", false));
        users.add(new User("U003", "blocked_user", "User", true));
        users.add(new User("U004", "vip_customer", "User", false));
        return users;
    }
}