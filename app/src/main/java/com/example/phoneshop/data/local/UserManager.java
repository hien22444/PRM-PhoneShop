package com.example.phoneshop.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * UserManager để quản lý việc lưu trữ và xác thực thông tin người dùng
 * Lưu trữ thông tin trong SharedPreferences dưới dạng JSON
 */
public class UserManager {
    private static UserManager instance;
    private static final String PREFS_NAME = "UserDataPrefs";
    private static final String KEY_USERS = "registered_users";
    private static final String KEY_USER_COUNT = "user_count";
    
    private SharedPreferences sharedPreferences;

    private UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Đăng ký người dùng mới
     * @param fullName Họ tên
     * @param email Email
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return true nếu đăng ký thành công, false nếu username đã tồn tại
     */
    public boolean registerUser(String fullName, String email, String username, String password) {
        // Kiểm tra username đã tồn tại chưa
        if (isUsernameExists(username)) {
            return false;
        }

        try {
            // Lấy danh sách users hiện tại
            List<UserData> users = getAllUsers();
            
            // Tạo user mới
            UserData newUser = new UserData(
                generateUserId(),
                fullName,
                email,
                username,
                password // Trong thực tế nên hash password
            );
            
            users.add(newUser);
            
            // Lưu lại danh sách users
            saveUsers(users);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra đăng nhập
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return UserData nếu đăng nhập thành công, null nếu sai
     */
    public UserData loginUser(String username, String password) {
        List<UserData> users = getAllUsers();
        
        for (UserData user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        
        return null;
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     * @param username Tên đăng nhập
     * @return true nếu đã tồn tại
     */
    public boolean isUsernameExists(String username) {
        List<UserData> users = getAllUsers();
        
        for (UserData user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Kiểm tra email đã tồn tại chưa
     * @param email Email
     * @return true nếu đã tồn tại
     */
    public boolean isEmailExists(String email) {
        List<UserData> users = getAllUsers();
        
        for (UserData user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Lấy thông tin user theo username
     * @param username Tên đăng nhập
     * @return UserData nếu tìm thấy, null nếu không
     */
    public UserData getUserByUsername(String username) {
        List<UserData> users = getAllUsers();
        
        for (UserData user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        
        return null;
    }

    /**
     * Lấy tất cả users
     * @return Danh sách users
     */
    private List<UserData> getAllUsers() {
        List<UserData> users = new ArrayList<>();
        String usersJson = sharedPreferences.getString(KEY_USERS, "[]");
        
        try {
            JSONArray jsonArray = new JSONArray(usersJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                UserData user = UserData.fromJson(jsonObject);
                users.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return users;
    }

    /**
     * Lưu danh sách users
     * @param users Danh sách users
     */
    private void saveUsers(List<UserData> users) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (UserData user : users) {
                jsonArray.put(user.toJson());
            }
            
            sharedPreferences.edit()
                .putString(KEY_USERS, jsonArray.toString())
                .putInt(KEY_USER_COUNT, users.size())
                .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tạo User ID mới
     * @return User ID
     */
    private String generateUserId() {
        int count = sharedPreferences.getInt(KEY_USER_COUNT, 0);
        return "user_" + (count + 1) + "_" + System.currentTimeMillis();
    }

    /**
     * Class để lưu trữ thông tin người dùng
     */
    public static class UserData {
        private String userId;
        private String fullName;
        private String email;
        private String username;
        private String password;

        public UserData(String userId, String fullName, String email, String username, String password) {
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.username = username;
            this.password = password;
        }

        public String getUserId() {
            return userId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("userId", userId);
            json.put("fullName", fullName);
            json.put("email", email);
            json.put("username", username);
            json.put("password", password);
            return json;
        }

        public static UserData fromJson(JSONObject json) throws JSONException {
            return new UserData(
                json.getString("userId"),
                json.getString("fullName"),
                json.getString("email"),
                json.getString("username"),
                json.getString("password")
            );
        }
    }
}

