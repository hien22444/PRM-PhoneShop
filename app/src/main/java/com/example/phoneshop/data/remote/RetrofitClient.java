package com.example.phoneshop.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static String BASE_URL;
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    private static RetrofitClient instance = null;

    public static String getBaseUrl() {
        return BASE_URL;
    }

    private RetrofitClient() {
        // Check if running on emulator or real device
        if (isEmulator()) {
            android.util.Log.d("RetrofitClient", "Running on emulator, using 10.0.2.2");
            BASE_URL = "http://10.0.2.2:8080/";
        } else {
            android.util.Log.d("RetrofitClient", "Running on real device, using local network IP");
            BASE_URL = "http://192.168.1.7:8080/"; // Update this to your actual IP
        }
        android.util.Log.d("RetrofitClient", "Using BASE_URL: " + BASE_URL);
    }

    private boolean isEmulator() {
        return android.os.Build.PRODUCT.contains("sdk") ||
                android.os.Build.MODEL.contains("Emulator") ||
                android.os.Build.MODEL.toLowerCase().contains("sdk");
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        if (apiService == null) {
            retrofit = createRetrofit();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    private static Retrofit createRetrofit() {
        // Logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                message -> android.util.Log.d("RetrofitClient", "API Call: " + message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttp client
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    android.util.Log.d("RetrofitClient", "Making request to: " + chain.request().url());
                    return chain.proceed(chain.request());
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Method to change base URL if needed (for physical device testing)
    public static void setBaseUrl(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }
}
