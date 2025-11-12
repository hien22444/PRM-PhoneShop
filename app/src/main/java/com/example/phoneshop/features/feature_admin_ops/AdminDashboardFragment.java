package com.example.phoneshop.features.feature_admin_ops;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.Fragment; // Cần thiết nếu BaseFragment không có
import com.example.phoneshop.R;
import com.example.phoneshop.common.base.BaseFragment;
// Imports cần thiết cho Model và Chart
import com.example.phoneshop.data.model.DashboardStats;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminDashboardFragment extends BaseFragment {

    private static final String TAG = "AdminDashboardFragment";

    private AdminDashboardViewModel viewModel;
    private TextView tvUsers, tvOrders, tvRevenue, tvPending;
    private LineChart lineChartData; // THÊM LẠI: Biến cho Line Chart

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_admin_dashboard;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminDashboardViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ Views (Metrics và Chart)
        tvUsers = view.findViewById(R.id.tv_total_users);
        tvOrders = view.findViewById(R.id.tv_total_orders);
        tvRevenue = view.findViewById(R.id.tv_total_revenue);
        tvPending = view.findViewById(R.id.tv_pending_orders);
        lineChartData = view.findViewById(R.id.line_chart_data); // Ánh xạ Line Chart

        // 2. Thiết lập cấu hình Chart ban đầu
        setupChart();

        // 3. Theo dõi dữ liệu
        observeViewModel();

        // Bắt đầu tải dữ liệu khi Fragment được tạo
        viewModel.fetchDashboardData();
    }

    private void observeViewModel() {
        viewModel.getDashboardStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                // Cập nhật Metrics
                tvUsers.setText(String.valueOf(stats.getTotalUsers()));
                tvOrders.setText(String.valueOf(stats.getTotalOrders()));

                // Định dạng tiền tệ
                // Sử dụng DecimalFormat để kiểm soát định dạng tốt hơn
                DecimalFormat revenueFormat = new DecimalFormat("#,###");
                String formattedRevenue = revenueFormat.format(stats.getTotalRevenue()) + " VNĐ";
                tvRevenue.setText(formattedRevenue);

                tvPending.setText(String.valueOf(stats.getPendingOrders()));

                // Cập nhật Chart
                updateChart(stats.getMonthlyRevenueData());

            } else {
                // Xử lý trường hợp dữ liệu null (lỗi tải hoặc API rỗng)
                tvUsers.setText("N/A");
                tvOrders.setText("N/A");
                tvRevenue.setText("N/A");
                tvPending.setText("N/A");
                // Xóa dữ liệu biểu đồ
                lineChartData.clear();
                lineChartData.invalidate();
                Log.w(TAG, "DashboardStats received is null. Displaying N/A.");
            }
        });

        // Có thể theo dõi thêm LiveData cho trạng thái Loading/Error nếu cần
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Thiết lập các thuộc tính cơ bản của LineChart.
     */
    private void setupChart() {
        lineChartData.getDescription().setEnabled(false); // Ẩn mô tả
        lineChartData.setTouchEnabled(true);
        lineChartData.setDragEnabled(true);
        lineChartData.setScaleEnabled(true);
        lineChartData.setDrawGridBackground(false);
        lineChartData.setExtraBottomOffset(5f);

        // Cấu hình trục X
        lineChartData.getXAxis().setDrawGridLines(false);
        lineChartData.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        lineChartData.getXAxis().setTextSize(10f);
        lineChartData.getXAxis().setTextColor(getResources().getColor(R.color.black));

        // Cấu hình trục Y bên trái
        lineChartData.getAxisLeft().setDrawGridLines(true);
        lineChartData.getAxisLeft().setTextSize(10f);
        lineChartData.getAxisLeft().setTextColor(getResources().getColor(R.color.black));

        // Ẩn trục Y bên phải
        lineChartData.getAxisRight().setEnabled(false);

        // Cấu hình Legend (Chú thích)
        lineChartData.getLegend().setEnabled(true);
        lineChartData.getLegend().setDrawInside(false);
        lineChartData.getLegend().setTextColor(getResources().getColor(R.color.black));

        lineChartData.animateX(1500); // Thêm animation
    }

    /**
     * Cập nhật LineChart với dữ liệu doanh thu.
     */
    private void updateChart(List<DashboardStats.RevenueData> data) {
        if (data == null || data.isEmpty()) {
            Toast.makeText(getContext(), "Không có dữ liệu biểu đồ.", Toast.LENGTH_SHORT).show();
            lineChartData.clear();
            lineChartData.invalidate();
            return;
        }

        ArrayList<Entry> entries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            // Y-Value là revenueAmount, X-Value là index (thứ tự)
            entries.add(new Entry(i, data.get(i).getRevenueAmount()));
            xLabels.add(data.get(i).getMonth()); // Label là tên tháng
        }

        // Cấu hình DataSet
        LineDataSet dataSet = new LineDataSet(entries, "Doanh Thu Hàng Tháng");
        dataSet.setColor(getResources().getColor(R.color.md_theme_tertiary));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(getResources().getColor(R.color.md_theme_tertiary));
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false); // Ẩn giá trị trên điểm

        // Cập nhật dữ liệu và Trục X
        LineData lineData = new LineData(dataSet);
        lineChartData.setData(lineData);

        // Đặt tên cho trục X (tháng)
        lineChartData.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));
        lineChartData.getXAxis().setGranularity(1f);

        lineChartData.invalidate(); // Vẽ lại biểu đồ
    }
}