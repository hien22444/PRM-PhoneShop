package com.example.phoneshop.data.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.phoneshop.data.model.ApiResponse;
import com.example.phoneshop.data.model.DashboardStats;
import com.example.phoneshop.data.model.DashboardStats.RevenueData; // Cần import inner class
import com.example.phoneshop.data.remote.AdminApiService;
import com.example.phoneshop.data.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardRepository {

    private static final String TAG = "DashboardRepository";
    private final AdminApiService adminApiService;
    private final String DUMMY_TOKEN = "jwt_access_token_for_admin";

    public AdminDashboardRepository() {
        this.adminApiService = RetrofitClient.getInstance().getAdminApiService();
    }

    /**
     * Tải dữ liệu Dashboard từ API và cập nhật trạng thái LiveData.
     */
    public void fetchDashboardStats(
            MutableLiveData<DashboardStats> data,
            MutableLiveData<Boolean> isLoading,
            MutableLiveData<String> error) {

        isLoading.setValue(true);

        String headerToken = "Bearer " + DUMMY_TOKEN;

        // Bắt đầu lệnh gọi API thật
        adminApiService.getDashboardStats(headerToken).enqueue(new Callback<ApiResponse<DashboardStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStats>> call, Response<ApiResponse<DashboardStats>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<DashboardStats> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        data.setValue(apiResponse.getData());
                        error.setValue(null);
                        Log.d(TAG, "Dashboard data fetched successfully.");
                    } else {
                        String errorMessage = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lỗi không xác định từ server.";
                        error.setValue("Lỗi API: " + errorMessage);
                        data.setValue(null);
                        Log.e(TAG, "API Error: " + errorMessage);
                    }
                } else {
                    String errorMessage = "Lỗi HTTP: " + response.code() + (response.message() != null ? " - " + response.message() : "");
                    error.setValue(errorMessage);
                    data.setValue(null);
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStats>> call, Throwable t) {
                isLoading.setValue(false);

                String errorMessage = "Lỗi mạng: " + t.getLocalizedMessage();
                error.setValue(errorMessage);
                data.setValue(null);
                Log.e(TAG, "API call failed due to network error", t);
            }
        });
    }

    // =============================================================
    // PHƯƠNG THỨC TẠO DỮ LIỆU MẪU (MOCK DATA) - Đặt bên ngoài fetchDashboardStats
    // =============================================================

    /**
     * Phương thức này chỉ dùng để tạo dữ liệu mẫu cho mục đích kiểm thử UI.
     * Nó không được gọi trong fetchDashboardStats ở phiên bản này.
     */
    private ApiResponse<DashboardStats> createMockApiResponse() {
        DashboardStats stats = new DashboardStats();

        // Gán các chỉ số tổng quan. (Cần setters trong DashboardStats)
        stats.setTotalUsers(1560);
        stats.setTotalOrders(852);
        stats.setTotalRevenue(1250000000.55);
        stats.setPendingOrders(45);

        // Tạo dữ liệu biểu đồ
        List<RevenueData> mockRevenue = new ArrayList<>();
        mockRevenue.add(createRevenueData("Thg 6", 150000000.00f));
        mockRevenue.add(createRevenueData("Thg 7", 220000000.00f));
        mockRevenue.add(createRevenueData("Thg 8", 185000000.00f));
        mockRevenue.add(createRevenueData("Thg 9", 250000000.00f));
        mockRevenue.add(createRevenueData("Thg 10", 310000000.00f));
        mockRevenue.add(createRevenueData("Thg 11", 350000000.00f));

        // Gán danh sách doanh thu (Cần setter cho monthlyRevenueData)
        stats.setMonthlyRevenueData(mockRevenue);

        // Tạo đối tượng ApiResponse
        ApiResponse<DashboardStats> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Mock data loaded successfully.");
        apiResponse.setData(stats);

        return apiResponse;
    }

    /**
     * Phương thức helper để tạo đối tượng RevenueData mẫu.
     * Cần setters cho month và revenueAmount trong DashboardStats.RevenueData.
     */
    private RevenueData createRevenueData(String month, float revenueAmount) {
        RevenueData data = new RevenueData();
        data.setMonth(month);
        data.setRevenueAmount(revenueAmount);
        return data;
    }
}