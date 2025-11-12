package com.example.phoneshop.features.feature_admin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Admin Login Response Model
 */
public class AdminLoginResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("admin")
    private AdminInfo admin;
    
    @SerializedName("token")
    private String token;

    public AdminLoginResponse() {}

    public AdminLoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AdminInfo getAdmin() {
        return admin;
    }

    public void setAdmin(AdminInfo admin) {
        this.admin = admin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Admin Info nested class
     */
    public static class AdminInfo {
        @SerializedName("id")
        private String id;
        
        @SerializedName("username")
        private String username;
        
        @SerializedName("role")
        private String role;
        
        @SerializedName("fullName")
        private String fullName;
        
        @SerializedName("loginTime")
        private String loginTime;
        
        @SerializedName("permissions")
        private String[] permissions;

        public AdminInfo() {}

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(String loginTime) {
            this.loginTime = loginTime;
        }

        public String[] getPermissions() {
            return permissions;
        }

        public void setPermissions(String[] permissions) {
            this.permissions = permissions;
        }
    }
}
