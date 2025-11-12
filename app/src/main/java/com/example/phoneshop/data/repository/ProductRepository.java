package com.example.phoneshop.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.model.ProductResponse;
import com.example.phoneshop.data.remote.ApiService;
import com.example.phoneshop.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static ProductRepository instance;
    private final ApiService apiService;

    private ProductRepository() {
        System.out.println("########## ProductRepository CONSTRUCTOR ##########");
        apiService = RetrofitClient.getInstance().getApiService();
        System.out.println("########## ApiService: " + apiService);
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            System.out.println("########## Tạo ProductRepository instance mới ##########");
            instance = new ProductRepository();
        }
        System.out.println("########## Return ProductRepository: " + instance);
        return instance;
    }

    public LiveData<ProductResponse> getProducts(int page, int size, String query, String brand, String sort) {
        System.out.println("########## ProductRepository.getProducts() ĐƯỢC GỌI ##########");
        System.out.println("########## page=" + page + ", size=" + size);
        System.out.println("########## query=" + query + ", brand=" + brand + ", sort=" + sort);

        MutableLiveData<ProductResponse> data = new MutableLiveData<>();
        System.out.println("########## MutableLiveData created: " + data);

        Log.e("ProductRepository", "==========================================");
        Log.e("ProductRepository", "getProducts() ĐƯỢC GỌI");
        Log.e("ProductRepository", "page=" + page + ", size=" + size);
        Log.e("ProductRepository", "query=" + query + ", brand=" + brand + ", sort=" + sort);
        Log.e("ProductRepository", "==========================================");

        try {
            System.out.println("########## Gọi apiService.getProducts()... ##########");
            Call<ProductResponse> call = apiService.getProducts(page, size, query, brand, sort);
            System.out.println("########## Call object: " + call);

            if (call == null) {
                System.out.println("########## ❌ Call object is NULL! ##########");
                Log.e("ProductRepository", "❌ Call object is NULL!");
                data.setValue(null);
                return data;
            }

            System.out.println("########## Chuẩn bị enqueue()... ##########");

            call.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                    System.out.println("########## ===== API RESPONSE ===== ##########");
                    System.out.println("########## Success? " + response.isSuccessful());
                    System.out.println("########## Code: " + response.code());
                    System.out.println("########## Body null? " + (response.body() == null));

                    Log.e("ProductRepository", "===== API RESPONSE =====");
                    Log.e("ProductRepository", "Success? " + response.isSuccessful());
                    Log.e("ProductRepository", "Code: " + response.code());
                    Log.e("ProductRepository", "Body null? " + (response.body() == null));

                    if (response.isSuccessful() && response.body() != null) {
                        ProductResponse body = response.body();

                        System.out.println("########## Content null? " + (body.getContent() == null));

                        if (body.getContent() != null) {
                            System.out.println("########## Content size: " + body.getContent().size());
                            Log.e("ProductRepository", "Content size: " + body.getContent().size());

                            // Log 3 sản phẩm đầu
                            for (int i = 0; i < Math.min(3, body.getContent().size()); i++) {
                                Product p = body.getContent().get(i);
                                System.out.println("  Product[" + i + "]: " + p.getName());
                            }
                        } else {
                            System.out.println("########## Content is NULL ##########");
                        }

                        System.out.println("########## Gọi data.postValue()... ##########");
                        data.postValue(body);
                        System.out.println("########## ✓ postValue() hoàn tất ##########");

                    } else {
                        System.out.println("########## ❌ Response not successful or body is null ##########");
                        Log.e("ProductRepository", "Response not successful or body is null");
                        data.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<ProductResponse> call, Throwable t) {
                    System.out.println("########## ===== API FAILURE ===== ##########");
                    System.out.println("########## Error: " + t.getMessage());
                    t.printStackTrace();

                    Log.e("ProductRepository", "===== API FAILURE =====");
                    Log.e("ProductRepository", "Error: " + t.getMessage());
                    t.printStackTrace();

                    data.setValue(null);
                }
            });

            System.out.println("########## enqueue() đã được gọi ##########");

        } catch (Exception e) {
            System.out.println("########## ❌ EXCEPTION trong getProducts(): " + e.getMessage());
            e.printStackTrace();
            Log.e("ProductRepository", "❌ EXCEPTION: " + e.getMessage(), e);
            data.setValue(null);
        }

        System.out.println("########## Return LiveData: " + data);
        Log.e("ProductRepository", "Return LiveData, đợi API response...");
        return data;
    }

    public LiveData<Product> getProductById(String id) {
        MutableLiveData<Product> data = new MutableLiveData<>();
        apiService.getProductById(id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ProductResponse> searchProducts(String query, int page, int size) {
        MutableLiveData<ProductResponse> data = new MutableLiveData<>();
        apiService.searchProducts(query, page, size).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                data.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Product> createProduct(Product p) {
        MutableLiveData<Product> data = new MutableLiveData<>();
        apiService.createProduct(p).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                data.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Product> updateProduct(String id, Product p) {
        MutableLiveData<Product> data = new MutableLiveData<>();
        apiService.updateProduct(id, p).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                data.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Boolean> deleteProduct(String id) {
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        apiService.deleteProduct(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                success.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                success.setValue(false);
            }
        });
        return success;
    }
}