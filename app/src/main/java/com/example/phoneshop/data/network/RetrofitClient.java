package com.example.phoneshop.data.network; // Giả định package của RetrofitClient

import com.example.phoneshop.data.remote.AdminApiService; // ĐÃ SỬA LỖI INCOMPATIBLE TYPE
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://api.yourdomain.com/";
    private static RetrofitClient instance = null;
    private final AdminApiService adminApiService;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        adminApiService = retrofit.create(AdminApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    // Phương thức này trả về AdminApiService TỪ data.remote
    public AdminApiService getAdminApiService() {
        return adminApiService;
    }
}