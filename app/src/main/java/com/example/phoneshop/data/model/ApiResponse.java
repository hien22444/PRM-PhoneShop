package com.example.phoneshop.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Model chung để đóng gói phản hồi từ API (Generic API Response).
 * T đại diện cho kiểu dữ liệu của trường 'data' (ví dụ: DashboardStats, List<User>).
 */
public class ApiResponse<T> implements Serializable {

    // Trạng thái thành công/thất bại logic của API (không phải HTTP status code)
    @SerializedName("success")
    private boolean success;

    // Thông báo lỗi hoặc thành công từ server
    @SerializedName("message")
    private String message;

    // Dữ liệu thực tế, có kiểu Generic T
    @SerializedName("data")
    private T data;

    // ================== GETTERS ==================

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // ================== SETTERS (Tùy chọn, thêm vào nếu cần cho Unit Test) ==================

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }
}