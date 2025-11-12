package com.example.phoneshop.features.feature_admin_ops;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.data.model.DashboardStats;
import com.example.phoneshop.data.repository.AdminDashboardRepository;

public class AdminDashboardViewModel extends ViewModel {

    // LiveData mà Fragment sẽ theo dõi (Dữ liệu chính)
    private final MutableLiveData<DashboardStats> _dashboardStats = new MutableLiveData<>();

    // LiveData để quản lý trạng thái tải (MỚI: Rất quan trọng cho UI)
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    // LiveData để quản lý thông báo lỗi
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();

    // Repository để xử lý logic API
    private final AdminDashboardRepository repository;

    // Biến cờ để tránh gửi lại yêu cầu API không cần thiết khi cấu hình thay đổi (Ví dụ: xoay màn hình)
    private boolean isDataLoaded = false;

    public AdminDashboardViewModel() {
        this.repository = new AdminDashboardRepository();
        // Tự động tải dữ liệu khi ViewModel được tạo
        // Tùy chọn: Có thể gọi fetchDashboardData() ở đây hoặc để Fragment gọi.
        // Nếu để ở đây, Fragment sẽ nhận được dữ liệu ngay cả sau khi cấu hình thay đổi.
        fetchDashboardData();
    }

    /**
     * Phương thức được gọi bởi Fragment để bắt đầu quá trình tải dữ liệu TỪ API.
     */
    public void fetchDashboardData() {
        // Chỉ fetch nếu chưa tải lần nào hoặc nếu bạn muốn có cơ chế Refresh
        if (isDataLoaded && _dashboardStats.getValue() != null) {
            // Nếu bạn muốn cho phép refresh, hãy xóa dòng 'return;' này
            return;
        }

        // Đánh dấu bắt đầu tải
        _isLoading.setValue(true);
        _errorMessage.setValue(null); // Xóa lỗi cũ

        // Gọi Repository để tải dữ liệu, truyền các LiveData để Repository cập nhật trực tiếp.
        // Repository sẽ chịu trách nhiệm set giá trị cuối cùng cho _isLoading, _dashboardStats, và _errorMessage.
        repository.fetchDashboardStats(_dashboardStats, _isLoading, _errorMessage);

        isDataLoaded = true;
    }

    // ================== PUBLIC GETTERS CHO LIVEDATA ==================

    public LiveData<DashboardStats> getDashboardStats() {
        return _dashboardStats;
    }

    public LiveData<String> getErrorMessage() {
        return _errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }
}