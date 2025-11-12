package com.example.phoneshop.data.remote;

import android.util.Log;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static String BASE_URL;
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    private static RetrofitClient instance = null;

    private RetrofitClient() {
        // Check if running on emulator or real device
        if (isEmulator()) {
            android.util.Log.d("RetrofitClient", "Running on emulator, using 10.0.2.2");
            BASE_URL = "http://10.0.2.2:8080/";
        } else {
            android.util.Log.d("RetrofitClient", "Running on real device, using local network IP");
            BASE_URL = "http://192.168.88.110:8080/"; // Update this to your actual IP
        }
        android.util.Log.d("RetrofitClient", "Using BASE_URL: " + BASE_URL);
    }

    private boolean isEmulator() {
        return android.os.Build.FINGERPRINT.contains("generic")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86");
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
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                message -> Log.d("RetrofitClient", message)
        );
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    Log.d("RetrofitClient", "➡️ Request URL: " + chain.request().url());
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

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        Log.d("RetrofitClient", "Base URL changed to: " + baseUrl);
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
