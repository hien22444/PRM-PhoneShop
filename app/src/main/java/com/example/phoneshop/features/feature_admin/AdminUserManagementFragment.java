package com.example.phoneshop.features.feature_admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.User;
import com.example.phoneshop.features.feature_admin.adapter.AdminUsersAdapter;
import com.example.phoneshop.features.feature_admin.viewmodel.AdminViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin User Management Fragment
 * Manages user accounts - view, search, delete users
 */
public class AdminUserManagementFragment extends Fragment implements AdminUsersAdapter.OnUserActionListener {

    private static final String TAG = "AdminUserManagement";
    private static final int PAGE_SIZE = 20;
    
    // Views
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircularProgressIndicator progressIndicator;
    private TextInputEditText etSearch;
    private MaterialButton btnSearch, btnClearSearch;
    private RecyclerView rvUsers;
    
    // Adapter
    private AdminUsersAdapter usersAdapter;
    
    // ViewModel
    private AdminViewModel adminViewModel;
    
    // Pagination
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private String currentQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupListeners();
        observeViewModel();
        
        // Load initial data
        loadUsers(true);
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        rvUsers = view.findViewById(R.id.rvUsers);
    }

    private void setupViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupRecyclerView() {
        usersAdapter = new AdminUsersAdapter(new ArrayList<>(), this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(layoutManager);
        rvUsers.setAdapter(usersAdapter);
        
        // Add scroll listener for pagination
        rvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMoreData) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                        loadUsers(false);
                    }
                }
            }
        });
    }

    private void setupListeners() {
        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 0;
            hasMoreData = true;
            loadUsers(true);
        });
        
        // Search button
        btnSearch.setOnClickListener(v -> performSearch());
        
        // Clear search button
        btnClearSearch.setOnClickListener(v -> clearSearch());
        
        // Search on enter key
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
    }

    private void observeViewModel() {
        // Observe loading state
        adminViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            isLoading = loading;
            updateLoadingState(loading);
        });
        
        // Observe users list
        adminViewModel.getUsersList().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                updateUsersList(users);
            }
        });
        
        // Observe operation results (delete, etc.)
        adminViewModel.getOperationResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    // Refresh list after successful operation
                    currentPage = 0;
                    hasMoreData = true;
                    loadUsers(true);
                } else {
                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        
        // Observe error messages
        adminViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadUsers(boolean refresh) {
        if (isLoading) return;
        
        if (refresh) {
            currentPage = 0;
            hasMoreData = true;
        }
        
        adminViewModel.loadUsers(currentPage, PAGE_SIZE, currentQuery);
        currentPage++;
    }

    private void updateLoadingState(boolean loading) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(loading);
        } else if (currentPage == 1) {
            // Show main progress indicator only for first page
            progressIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    private void updateUsersList(List<User> users) {
        if (currentPage == 1) {
            // First page - replace all data
            usersAdapter.updateUsers(users);
        } else {
            // Additional pages - append data
            usersAdapter.addUsers(users);
        }
        
        // Check if we have more data
        hasMoreData = users.size() == PAGE_SIZE;
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        currentQuery = query;
        currentPage = 0;
        hasMoreData = true;
        
        // Update UI
        btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
        
        loadUsers(true);
    }

    private void clearSearch() {
        etSearch.setText("");
        currentQuery = "";
        currentPage = 0;
        hasMoreData = true;
        btnClearSearch.setVisibility(View.GONE);
        
        loadUsers(true);
    }

    // AdminUsersAdapter.OnUserActionListener implementation
    @Override
    public void onUserClick(User user) {
        // Show user detail dialog or navigate to detail fragment
        showUserDetailDialog(user);
    }

    @Override
    public void onDeleteUserClick(User user) {
        showDeleteUserConfirmation(user);
    }

    @Override
    public void onViewOrdersClick(User user) {
        // Navigate to orders filtered by this user
        Toast.makeText(getContext(), "Xem đơn hàng của " + user.getFullName(), Toast.LENGTH_SHORT).show();
        // You can implement navigation to order management with user filter here
    }

    private void showUserDetailDialog(User user) {
        if (getContext() == null) return;
        
        // Create a simple user detail dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chi tiết người dùng");
        
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(user.getId()).append("\n");
        details.append("Họ tên: ").append(user.getFullName()).append("\n");
        details.append("Email: ").append(user.getEmail()).append("\n");
        details.append("Username: ").append(user.getUsername()).append("\n");
        details.append("Ngày tạo: ").append(user.getCreatedAt()).append("\n");
        details.append("Trạng thái: ").append(user.isActive() ? "Hoạt động" : "Không hoạt động");
        
        builder.setMessage(details.toString());
        builder.setPositiveButton("Đóng", null);
        
        // Add action buttons
        builder.setNeutralButton("Xem đơn hàng", (dialog, which) -> {
            onViewOrdersClick(user);
        });
        
        if (user.isActive()) {
            builder.setNegativeButton("Vô hiệu hóa", (dialog, which) -> {
                onDeleteUserClick(user);
            });
        }
        
        builder.show();
    }

    private void showDeleteUserConfirmation(User user) {
        if (getContext() == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận xóa người dùng");
        builder.setMessage("Bạn có chắc chắn muốn vô hiệu hóa tài khoản của " + user.getFullName() + "?\n\n" +
                "Hành động này sẽ ngăn người dùng đăng nhập vào ứng dụng.");
        
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            adminViewModel.deleteUser(user.getId());
        });
        
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        if (usersAdapter.getItemCount() == 0) {
            loadUsers(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references to prevent memory leaks
        swipeRefreshLayout = null;
        progressIndicator = null;
        etSearch = null;
        btnSearch = null;
        btnClearSearch = null;
        rvUsers = null;
        usersAdapter = null;
    }
}
