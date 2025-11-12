package com.example.phoneshop.feature_admin_inventory;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.common.utils.UiState;
import com.example.phoneshop.data.model.PagedResponse;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.model.ProductResponse;
import com.example.phoneshop.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminInventoryViewModel extends ViewModel {

    private final ProductRepository repo;

    public AdminInventoryViewModel() {
        Log.e("AdminInventoryVM", "==========================================");
        Log.e("AdminInventoryVM", "CONSTRUCTOR được gọi!");
        Log.e("AdminInventoryVM", "Instance hashCode: " + this.hashCode());
        Log.e("AdminInventoryVM", "==========================================");

        this.repo = ProductRepository.getInstance();
        Log.e("AdminInventoryVM", "Repository instance: " + repo);

        // KIỂM TRA REPOSITORY
        if (repo == null) {
            Log.e("AdminInventoryVM", "❌ REPOSITORY LÀ NULL!");
        } else {
            Log.e("AdminInventoryVM", "✓ Repository OK");
        }
    }

    public void deleteProduct(String id, Runnable onSuccess, Runnable onError) {
        repo.deleteProduct(id).observeForever(success -> {
            if (success != null && success) {
                refresh();
                if (onSuccess != null) onSuccess.run();
            } else {
                if (onError != null) onError.run();
            }
        });
    }

    private final MutableLiveData<UiState<PagedResponse<Product>>> _products = new MutableLiveData<>();
    public final LiveData<UiState<PagedResponse<Product>>> products = _products;

    private final MediatorLiveData<ProductResponse> repoData = new MediatorLiveData<>();
    private LiveData<ProductResponse> lastDataSource;

    private final List<Product> cache = new ArrayList<>();

    private int page = 0;
    private final int size = 20;
    private String q = null;
    private String brand = null;
    private String sort = null;

    public void refresh() {
        // LOG NGAY ĐẦU HÀM - DÙNG System.out ĐỂ CHẮC CHẮN
        System.out.println("########## AdminInventoryVM.refresh() CALLED ##########");
        System.out.println("########## Thread: " + Thread.currentThread().getName());
        System.out.println("########## Instance: " + this);
        System.out.println("########## HashCode: " + this.hashCode());

        Log.e("AdminInventoryVM", "==========================================");
        Log.e("AdminInventoryVM", "❗❗❗ refresh() ĐƯỢC GỌI! ❗❗❗");
        Log.e("AdminInventoryVM", "Instance hashCode: " + this.hashCode());
        Log.e("AdminInventoryVM", "page=" + page + ", size=" + size);
        Log.e("AdminInventoryVM", "q=" + q + ", brand=" + brand + ", sort=" + sort);
        Log.e("AdminInventoryVM", "==========================================");

        page = 0;
        cache.clear();
        Log.e("AdminInventoryVM", "Cache cleared, chuẩn bị gọi loadInternal(true)");
        loadInternal(true);
        Log.e("AdminInventoryVM", "Đã gọi loadInternal(true)");
    }

    public void loadNextPage() {
        page += 1;
        loadInternal(false);
    }

    public void setSearch(String q) {
        this.q = (q == null || q.trim().isEmpty()) ? null : q.trim();
        Log.e("AdminInventoryVM", "setSearch: " + this.q);
    }

    public void setBrand(String brand) {
        this.brand = brand;
        Log.e("AdminInventoryVM", "setBrand: " + this.brand);
    }

    public void setSort(String sort) {
        this.sort = sort;
        Log.e("AdminInventoryVM", "setSort: " + this.sort);
    }

    private void loadInternal(boolean firstPage) {
        System.out.println("########## loadInternal() ĐƯỢC GỌI! ##########");
        System.out.println("########## firstPage=" + firstPage);

        Log.e("AdminInventoryVM", "==========================================");
        Log.e("AdminInventoryVM", "loadInternal() ĐƯỢC GỌI!");
        Log.e("AdminInventoryVM", "firstPage=" + firstPage);
        Log.e("AdminInventoryVM", "==========================================");

        if (firstPage) {
            System.out.println("########## POST LOADING STATE ##########");
            Log.e("AdminInventoryVM", "POST LOADING STATE");
            _products.postValue(UiState.loading());
        }

        if (lastDataSource != null) {
            System.out.println("########## Remove source cũ ##########");
            Log.e("AdminInventoryVM", "Remove source cũ");
            repoData.removeSource(lastDataSource);
        }

        System.out.println("########## Chuẩn bị gọi repo.getProducts() ##########");
        System.out.println("########## Params: page=" + page + ", size=" + size + ", q=" + q + ", brand=" + brand + ", sort=" + sort);

        Log.e("AdminInventoryVM", "Chuẩn bị gọi repo.getProducts()");
        Log.e("AdminInventoryVM", "Params: page=" + page + ", size=" + size + ", q=" + q + ", brand=" + brand + ", sort=" + sort);

        // ===== FIX CHÍNH Ở ĐÂY =====
        try {
            lastDataSource = repo.getProducts(page, size, q, brand, sort);

            System.out.println("########## ✓ Đã nhận lastDataSource: " + lastDataSource);
            Log.e("AdminInventoryVM", "✓ Đã nhận lastDataSource: " + lastDataSource);

            if (lastDataSource == null) {
                System.out.println("########## ❌ lastDataSource LÀ NULL! ##########");
                Log.e("AdminInventoryVM", "❌ lastDataSource LÀ NULL!");
                _products.postValue(UiState.error("Repository trả về null"));
                return;
            }

        } catch (Exception e) {
            System.out.println("########## ❌ EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            Log.e("AdminInventoryVM", "❌ EXCEPTION khi gọi repo.getProducts():", e);
            _products.postValue(UiState.error("Lỗi: " + e.getMessage()));
            return;
        }

        System.out.println("########## Chuẩn bị addSource... ##########");
        Log.e("AdminInventoryVM", "Chuẩn bị addSource...");

        repoData.addSource(lastDataSource, resp -> {
            try {
                System.out.println("########## ===== RESPONSE CALLBACK TRIGGERED ===== ##########");
                System.out.println("########## Response null? " + (resp == null));

                Log.e("AdminInventoryVM", "===== BẮT ĐẦU XỬ LÝ RESPONSE =====");
                Log.e("AdminInventoryVM", "Response null? " + (resp == null));

                if (resp == null) {
                    System.out.println("########## ❌ Response NULL! ##########");
                    Log.e("AdminInventoryVM", "❌ Response NULL từ repository!");
                    _products.postValue(UiState.error("Tải dữ liệu thất bại (resp == null)"));
                    return;
                }

                System.out.println("########## ✓ Response nhận được! ##########");
                System.out.println("########## Content null? " + (resp.getContent() == null));

                Log.e("AdminInventoryVM", "✓ Response nhận được!");
                Log.e("AdminInventoryVM", "Content null? " + (resp.getContent() == null));

                if (resp.getContent() != null) {
                    System.out.println("########## Content size: " + resp.getContent().size());
                    Log.e("AdminInventoryVM", "Content size: " + resp.getContent().size());

                    // LOG CHI TIẾT TỪNG SẢN PHẨM
                    for (int i = 0; i < Math.min(3, resp.getContent().size()); i++) {
                        Product p = resp.getContent().get(i);
                        System.out.println("  Product[" + i + "]: " + p.getName() + " - " + p.getPrice());
                        Log.e("AdminInventoryVM", "  Product[" + i + "]: " + p.getName() + " - " + p.getPrice());
                    }
                } else {
                    System.out.println("########## Content is NULL ##########");
                    Log.e("AdminInventoryVM", "Content is NULL");
                }

                System.out.println("########## Page: " + resp.getPage() + ", TotalElements: " + resp.getTotalElements());
                Log.e("AdminInventoryVM", "Page: " + resp.getPage());
                Log.e("AdminInventoryVM", "TotalElements: " + resp.getTotalElements());

                ProductResponse data = resp;

                if (firstPage) {
                    cache.clear();
                    System.out.println("########## CLEAR CACHE (firstPage) ##########");
                    Log.e("AdminInventoryVM", "CLEAR CACHE (firstPage)");
                }

                if (data.getContent() != null && !data.getContent().isEmpty()) {
                    cache.addAll(data.getContent());
                    System.out.println("########## ✓ Đã thêm " + data.getContent().size() + " items vào cache");
                    System.out.println("########## Cache size hiện tại: " + cache.size());
                    Log.e("AdminInventoryVM", "✓ Đã thêm " + data.getContent().size() + " items vào cache");
                    Log.e("AdminInventoryVM", "Cache size hiện tại: " + cache.size());
                } else {
                    System.out.println("########## ⚠ Content EMPTY hoặc NULL! ##########");
                    Log.w("AdminInventoryVM", "⚠ Content EMPTY hoặc NULL!");
                }

                PagedResponse<Product> merged = new PagedResponse<>();
                merged.content = new ArrayList<>(cache);
                merged.page = data.getPage();
                merged.size = data.getSize();
                merged.totalPages = data.getTotalPages();
                merged.totalElements = data.getTotalElements();

                System.out.println("########## Merged content size: " + merged.content.size());
                Log.e("AdminInventoryVM", "Merged content size: " + merged.content.size());

                if (merged.content == null || merged.content.isEmpty()) {
                    System.out.println("########## POST EMPTY STATE ##########");
                    Log.w("AdminInventoryVM", "POST EMPTY STATE");
                    _products.postValue(UiState.empty());
                } else {
                    System.out.println("########## ✓✓✓ POST SUCCESS với " + merged.content.size() + " items ##########");
                    Log.e("AdminInventoryVM", "✓✓✓ POST SUCCESS với " + merged.content.size() + " items");
                    _products.postValue(UiState.success(merged));
                }

            } catch (Exception e) {
                System.out.println("########## ❌ CRASH: " + e.getMessage());
                e.printStackTrace();
                Log.e("AdminInventoryVM", "❌ CRASH KHI XỬ LÝ:", e);
                _products.postValue(UiState.error("Lỗi xử lý dữ liệu: " + e.getMessage()));

            } finally {
                repoData.removeSource(lastDataSource);
                System.out.println("########## removeSource hoàn tất ##########");
                Log.e("AdminInventoryVM", "removeSource hoàn tất");
            }
        });

        System.out.println("########## Đã addSource xong, đợi response... ##########");
        Log.e("AdminInventoryVM", "Đã addSource xong, đợi response...");
    }

    public void toggleVisible(Product p, boolean newState) {
        p.setVisible(newState);
        PagedResponse<Product> current = new PagedResponse<>();
        current.content = new ArrayList<>(cache);
        _products.postValue(UiState.success(current));
    }

    public void deleteProduct(Product p) {
        repo.deleteProduct(p.getId()).observeForever(success -> {
            if (success != null && success) {
                refresh();
            } else {
                Log.e("AdminInventoryVM", "Xóa thất bại");
            }
        });
    }
}