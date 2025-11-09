package com.example.phoneshop.feature_shopping;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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

    public LiveData<List<Product>> getProducts() {
        return _products;
    }

    // LiveData cho Flash Sale
    private final MutableLiveData<List<Product>> _flashSaleProducts = new MutableLiveData<>();

    public LiveData<List<Product>> getFlashSaleProducts() {
        return _flashSaleProducts;
    }

    // LiveData cho chi tiết sản phẩm
    private final MutableLiveData<Product> _selectedProduct = new MutableLiveData<>();

    public LiveData<Product> getSelectedProduct() {
        return _selectedProduct;
    }

    // LiveData cho trạng thái loading
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    // LiveData cho error
    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public LiveData<String> getError() {
        return _error;
    }

    // LiveData cho empty state
    private final MutableLiveData<Boolean> _isEmpty = new MutableLiveData<>();

    public LiveData<Boolean> getIsEmpty() {
        return _isEmpty;
    }

    // Pagination
    private int currentPage = 0;
    private final int pageSize = 20;
    private boolean isLastPage = false;

    public ShoppingViewModel() {
        repository = ProductRepository.getInstance();
        _isLoading.setValue(false);
        _error.setValue("");
        loadProducts();
        loadFlashSaleProducts();
    }

    // Load danh sách sản phẩm
    public void loadProducts() {
        if (_isLoading.getValue() == Boolean.TRUE) {
            return; // Đang load rồi, không load lại
        }

        _isLoading.setValue(true);
        _error.setValue("");

        LiveData<ProductResponse> responseLiveData = repository.getProducts(
                currentPage, pageSize, null, null, null);

        Observer<ProductResponse> observer = new Observer<ProductResponse>() {
            private boolean hasBeenCalled = false;

            @Override
            public void onChanged(ProductResponse response) {
                if (hasBeenCalled)
                    return; // Chỉ xử lý một lần
                hasBeenCalled = true;

                _isLoading.setValue(false);
                if (response != null && response.getContent() != null) {
                    List<Product> currentList = _products.getValue();
                    if (currentList == null || currentPage == 0) {
                        currentList = new ArrayList<>();
                    }
                    if (currentPage == 0) {
                        // Nếu là trang đầu, thay thế toàn bộ danh sách
                        currentList = new ArrayList<>(response.getContent());
                    } else {
                        // Nếu không phải trang đầu, thêm vào danh sách
                        currentList.addAll(response.getContent());
                    }
                    _products.setValue(currentList);
                    _isEmpty.setValue(currentList.isEmpty());

                    isLastPage = currentPage >= response.getTotalPages() - 1;
                    _error.setValue("");
                } else {
                    _error.setValue("Không thể tải danh sách sản phẩm");
                    if (_products.getValue() == null || _products.getValue().isEmpty()) {
                        _isEmpty.setValue(true);
                    }
                }
                // Remove observer sau khi nhận được response
                responseLiveData.removeObserver(this);
            }
        };

        responseLiveData.observeForever(observer);
    }

    // Load Flash Sale products (giả lập - lấy 5 sản phẩm đầu tiên)
    public void loadFlashSaleProducts() {
        LiveData<ProductResponse> responseLiveData = repository.getProducts(
                0, 5, null, null, null);

        Observer<ProductResponse> observer = new Observer<ProductResponse>() {
            private boolean hasBeenCalled = false;

            @Override
            public void onChanged(ProductResponse response) {
                if (hasBeenCalled)
                    return; // Chỉ xử lý một lần
                hasBeenCalled = true;

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
                // Remove observer sau khi nhận được response
                responseLiveData.removeObserver(this);
            }
        };

        responseLiveData.observeForever(observer);
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
        if (query == null || query.trim().isEmpty()) {
            _error.setValue("Vui lòng nhập từ khóa tìm kiếm");
            return;
        }

        _isLoading.setValue(true);
        _error.setValue("");
        currentPage = 0;
        isLastPage = false;

        LiveData<ProductResponse> responseLiveData = repository.searchProducts(
                query.trim(), currentPage, pageSize);

        Observer<ProductResponse> observer = new Observer<ProductResponse>() {
            private boolean hasBeenCalled = false;

            @Override
            public void onChanged(ProductResponse response) {
                if (hasBeenCalled)
                    return; // Chỉ xử lý một lần
                hasBeenCalled = true;

                _isLoading.setValue(false);
                if (response != null && response.getContent() != null) {
                    List<Product> products = response.getContent();
                    _products.setValue(products);
                    _isEmpty.setValue(products.isEmpty());
                    isLastPage = currentPage >= response.getTotalPages() - 1;
                    if (products.isEmpty()) {
                        _error.setValue("Không tìm thấy sản phẩm nào với từ khóa: " + query);
                    } else {
                        _error.setValue("");
                    }
                } else {
                    _error.setValue("Không tìm thấy sản phẩm");
                    _products.setValue(new ArrayList<>());
                    _isEmpty.setValue(true);
                }
                // Remove observer sau khi nhận được response
                responseLiveData.removeObserver(this);
            }
        };

        responseLiveData.observeForever(observer);
    }

    // Lọc theo thương hiệu
    public void filterByBrand(String brand) {
        _isLoading.setValue(true);
        _error.setValue("");
        currentPage = 0;
        isLastPage = false;

        LiveData<ProductResponse> responseLiveData = repository.getProductsByBrand(
                brand, currentPage, pageSize);

        Observer<ProductResponse> observer = new Observer<ProductResponse>() {
            private boolean hasBeenCalled = false;

            @Override
            public void onChanged(ProductResponse response) {
                if (hasBeenCalled)
                    return; // Chỉ xử lý một lần
                hasBeenCalled = true;

                _isLoading.setValue(false);
                if (response != null && response.getContent() != null) {
                    _products.setValue(response.getContent());
                    _isEmpty.setValue(response.getContent().isEmpty());
                    isLastPage = currentPage >= response.getTotalPages() - 1;
                    _error.setValue("");
                } else {
                    _error.setValue("Không thể lọc sản phẩm");
                }
                // Remove observer sau khi nhận được response
                responseLiveData.removeObserver(this);
            }
        };

        responseLiveData.observeForever(observer);
    }

    // Load chi tiết sản phẩm
    public void loadProductDetail(String productId) {
        if (productId == null || productId.isEmpty()) {
            _error.setValue("Mã sản phẩm không hợp lệ");
            return;
        }

        _isLoading.setValue(true);
        _error.setValue("");

        android.util.Log.d("ShoppingViewModel", "Loading product details for ID: " + productId);

        LiveData<Product> productLiveData = repository.getProductById(productId);

        Observer<Product> observer = new Observer<Product>() {
            private boolean hasBeenCalled = false;

            @Override
            public void onChanged(Product product) {
                if (hasBeenCalled)
                    return; // Chỉ xử lý một lần
                hasBeenCalled = true;

                _isLoading.setValue(false);
                if (product != null) {
                    android.util.Log.d("ShoppingViewModel", "Successfully loaded product: " + product.getName());
                    _selectedProduct.setValue(product);
                    _error.setValue("");
                } else {
                    android.util.Log.e("ShoppingViewModel", "Failed to load product details for ID: " + productId);
                    _error.setValue("Không thể tải chi tiết sản phẩm. Vui lòng kiểm tra kết nối mạng và thử lại.");
                    _selectedProduct.setValue(null);
                }
                // Remove observer sau khi nhận được response
                productLiveData.removeObserver(this);
            }
        };

        productLiveData.observeForever(observer);
    }

    // Reset danh sách
    public void resetProducts() {
        currentPage = 0;
        isLastPage = false;
        _products.setValue(new ArrayList<>());
        _error.setValue("");
        loadProducts();
    }
}
