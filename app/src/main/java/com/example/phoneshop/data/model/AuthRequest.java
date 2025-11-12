package com.example.phoneshop.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("currentPassword")
    private String currentPassword;
    
    @SerializedName("newPassword")
    private String newPassword;

    // Default constructor
    public AuthRequest() {}

    // Static factory methods to avoid constructor conflicts
    public static AuthRequest forRegistration(String fullName, String email, String username, String password) {
        AuthRequest request = new AuthRequest();
        request.fullName = fullName;
        request.email = email;
        request.username = username;
        request.password = password;
        return request;
    }
    
    public static AuthRequest forLogin(String username, String password) {
        AuthRequest request = new AuthRequest();
        request.username = username;
        request.password = password;
        return request;
    }
    
    public static AuthRequest forPasswordChange(String currentPassword, String newPassword) {
        AuthRequest request = new AuthRequest();
        request.currentPassword = currentPassword;
        request.newPassword = newPassword;
        return request;
    }
    
    public static AuthRequest forProfileUpdate(String fullName, String email) {
        AuthRequest request = new AuthRequest();
        request.fullName = fullName;
        request.email = email;
        return request;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
