package com.example.phoneshop.feature_shopping;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.data.model.ProductResponse;
import com.example.phoneshop.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class ShoppingViewModel extends ViewModel {
    
    private final ProductRepository repository;
    
    // LiveData cho danh sách sản phẩm
    private final MutableLiveData<List<Product>> _products = new MutableLiveData<>();
    public LiveData<List<Product>> getProducts() { return _products; }
    
    // LiveData cho Flash Sale
    private final MutableLiveData<List<Product>> _flashSaleProducts = new MutableLiveData<>();
    public LiveData<List<Product>> getFlashSaleProducts() { return _flashSaleProducts; }
    
    // LiveData cho chi tiết sản phẩm
    private final MutableLiveData<Product> _selectedProduct = new MutableLiveData<>();
    public LiveData<Product> getSelectedProduct() { return _selectedProduct; }
    
    // LiveData cho trạng thái loading
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading() { return _isLoading; }
    
    // LiveData cho error
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> getError() { return _error; }
    
    // LiveData cho empty state
    private final MutableLiveData<Boolean> _isEmpty = new MutableLiveData<>();
    public LiveData<Boolean> getIsEmpty() { return _isEmpty; }
    
    // Pagination
    private int currentPage = 0;
    private final int pageSize = 20;
    private boolean isLastPage = false;
    
    public ShoppingViewModel() {
        repository = ProductRepository.getInstance();
        loadProducts();
        loadFlashSaleProducts();
    }
    
    // Load danh sách sản phẩm
    public void loadProducts() {
        _isLoading.setValue(true);
        
        LiveData<ProductResponse> responseLiveData = repository.getProducts(
                currentPage, pageSize, null, null, null
        );
        
        responseLiveData.observeForever(response -> {
            _isLoading.setValue(false);
            if (response != null && response.getContent() != null) {
                List<Product> currentList = _products.getValue();
                if (currentList == null) {
                    currentList = new ArrayList<>();
                }
                currentList.addAll(response.getContent());
                _products.setValue(currentList);
                _isEmpty.setValue(currentList.isEmpty());
                
                isLastPage = currentPage >= response.getTotalPages() - 1;
            } else {
                _error.setValue("Không thể tải danh sách sản phẩm");
                _isEmpty.setValue(_products.getValue() == null || _products.getValue().isEmpty());
            }
        });
    }
    
    // Load Flash Sale products (giả lập - lấy 5 sản phẩm đầu tiên)
    public void loadFlashSaleProducts() {
        LiveData<ProductResponse> responseLiveData = repository.getProducts(
                0, 5, null, null, null
        );
        
        responseLiveData.observeForever(response -> {
            if (response != null && response.getContent() != null) {
                List<Product> flashSales = new ArrayList<>();
                for (Product p : response.getContent()) {
                    // Giả lập flash sale
                    p.setFlashSale(true);
                    p.setFlashSalePrice((long) (p.getPrice() * 0.7)); // Giảm 30%
                    flashSales.add(p);
                }
                _flashSaleProducts.setValue(flashSales);
            }
        });
    }
    
    // Load thêm sản phẩm (pagination)
    public void loadMoreProducts() {
        if (!isLastPage && _isLoading.getValue() != Boolean.TRUE) {
            currentPage++;
            loadProducts();
        }
    }
    
    // Tìm kiếm sản phẩm
    public void searchProducts(String query) {
        _isLoading.setValue(true);
        currentPage = 0;
        
        LiveData<ProductResponse> responseLiveData = repository.searchProducts(
                query, currentPage, pageSize
        );
        
        responseLiveData.observeForever(response -> {
            _isLoading.setValue(false);
            if (response != null && response.getContent() != null) {
                _products.setValue(response.getContent());
                _isEmpty.setValue(response.getContent().isEmpty());
                isLastPage = currentPage >= response.getTotalPages() - 1;
            } else {
                _error.setValue("Không tìm thấy sản phẩm");
                _products.setValue(new ArrayList<>());
                _isEmpty.setValue(true);
            }
        });
    }
    
    // Lọc theo thương hiệu
    public void filterByBrand(String brand) {
        _isLoading.setValue(true);
        currentPage = 0;
        
        LiveData<ProductResponse> responseLiveData = repository.getProductsByBrand(
                brand, currentPage, pageSize
        );
        
        responseLiveData.observeForever(response -> {
            _isLoading.setValue(false);
            if (response != null && response.getContent() != null) {
                _products.setValue(response.getContent());
                _isEmpty.setValue(response.getContent().isEmpty());
                isLastPage = currentPage >= response.getTotalPages() - 1;
            } else {
                _error.setValue("Không thể lọc sản phẩm");
            }
        });
    }
    
    // Load chi tiết sản phẩm
    public void loadProductDetail(String productId) {
        _isLoading.setValue(true);
        
        LiveData<Product> productLiveData = repository.getProductById(productId);
        
        productLiveData.observeForever(product -> {
            _isLoading.setValue(false);
            if (product != null) {
                _selectedProduct.setValue(product);
            } else {
                _error.setValue("Không thể tải chi tiết sản phẩm");
            }
        });
    }
    
    // Reset danh sách
    public void resetProducts() {
        currentPage = 0;
        isLastPage = false;
        _products.setValue(new ArrayList<>());
        loadProducts();
    }
}

