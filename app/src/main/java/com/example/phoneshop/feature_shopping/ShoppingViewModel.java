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

    // LiveData riêng cho kết quả tìm kiếm
    private final MutableLiveData<List<Product>> _searchResults = new MutableLiveData<>();

    public LiveData<List<Product>> getSearchResults() {
        return _searchResults;
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

        android.util.Log.d("ShoppingViewModel", "Starting search with query: '" + query + "'");

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
                    List<Product> allProducts = response.getContent();
                    android.util.Log.d("ShoppingViewModel", "API returned " + allProducts.size() + " products for query: '" + query + "'");

                    // Client-side filtering with priority for product name search
                    List<Product> filteredProducts = new ArrayList<>();
                    String searchQuery = query.toLowerCase().trim();

                    android.util.Log.d("ShoppingViewModel", "Filtering products with query: '" + searchQuery + "'");

                    for (Product product : allProducts) {
                        boolean matches = false;
                        String matchReason = "";

                        // Priority 1: Check if product name contains query (most important)
                        if (product.getName() != null && product.getName().toLowerCase().contains(searchQuery)) {
                            matches = true;
                            matchReason = "name";
                        }

                        // Priority 2: Check if brand contains query
                        if (!matches && product.getBrand() != null && product.getBrand().toLowerCase().contains(searchQuery)) {
                            matches = true;
                            matchReason = "brand";
                        }

                        // Priority 3: Check if category contains query
                        if (!matches && product.getCategory() != null && product.getCategory().toLowerCase().contains(searchQuery)) {
                            matches = true;
                            matchReason = "category";
                        }

                        // Priority 4: Check if description contains query (least priority)
                        if (!matches && product.getDescription() != null && product.getDescription().toLowerCase().contains(searchQuery)) {
                            matches = true;
                            matchReason = "description";
                        }

                        if (matches) {
                            filteredProducts.add(product);
                            android.util.Log.d("ShoppingViewModel", "Product matched by " + matchReason + ": " + product.getName() + " (" + product.getBrand() + ")");
                        }
                    }

                    android.util.Log.d("ShoppingViewModel", "After client filtering: " + filteredProducts.size() + " products match query: '" + query + "'");

                    // Log first few filtered products for debugging
                    for (int i = 0; i < Math.min(3, filteredProducts.size()); i++) {
                        Product p = filteredProducts.get(i);
                        android.util.Log.d("ShoppingViewModel", "Filtered Product " + (i+1) + ": " + p.getName() + " - " + p.getBrand());
                    }

                    _searchResults.setValue(filteredProducts);
                    _isEmpty.setValue(filteredProducts.isEmpty());
                    isLastPage = currentPage >= response.getTotalPages() - 1;
                    if (filteredProducts.isEmpty()) {
                        _error.setValue("Không tìm thấy sản phẩm nào với từ khóa: \"" + query + "\"\nThử tìm kiếm với tên sản phẩm hoặc loại sản phẩm khác");
                    } else {
                        _error.setValue("");
                    }
                } else {
                    android.util.Log.e("ShoppingViewModel", "Search response is null or empty for query: '" + query + "'");
                    _error.setValue("Không tìm thấy sản phẩm");
                    _searchResults.setValue(new ArrayList<>()); // Use search results instead of main products
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
        android.util.Log.d("ShoppingViewModel", "Filtering by brand: '" + brand + "'");

        _isLoading.setValue(true);
        _error.setValue("");
        currentPage = 0;
        isLastPage = false;

        // Get all products and filter client-side
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
                    List<Product> allProducts = response.getContent();
                    android.util.Log.d("ShoppingViewModel", "API returned " + allProducts.size() + " products for brand filter: '" + brand + "'");

                    // Client-side filtering by brand
                    List<Product> filteredProducts = new ArrayList<>();

                    for (Product product : allProducts) {
                        if (product.getBrand() != null && product.getBrand().equalsIgnoreCase(brand)) {
                            filteredProducts.add(product);
                        }
                    }

                    android.util.Log.d("ShoppingViewModel", "After brand filtering: " + filteredProducts.size() + " products match brand: '" + brand + "'");

                    _products.setValue(filteredProducts);
                    _isEmpty.setValue(filteredProducts.isEmpty());
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

    // Clear search results
    public void clearSearchResults() {
        _searchResults.setValue(new ArrayList<>());
        _error.setValue("");
        _isEmpty.setValue(true);
    }

    // Search products by name only (more specific search)
    public void searchProductsByName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            _error.setValue("Vui lòng nhập tên sản phẩm");
            return;
        }

        android.util.Log.d("ShoppingViewModel", "Searching products by name only: '" + productName + "'");

        _isLoading.setValue(true);
        _error.setValue("");
        currentPage = 0;
        isLastPage = false;

        LiveData<ProductResponse> responseLiveData = repository.searchProducts(
                productName.trim(), currentPage, pageSize);

        Observer<ProductResponse> observer = new Observer<ProductResponse>() {
            private boolean hasBeenCalled = false;

            @Override
            public void onChanged(ProductResponse response) {
                if (hasBeenCalled)
                    return;
                hasBeenCalled = true;

                _isLoading.setValue(false);
                if (response != null && response.getContent() != null) {
                    List<Product> allProducts = response.getContent();

                    // Filter only by product name (strict name search)
                    List<Product> nameFilteredProducts = new ArrayList<>();
                    String searchName = productName.toLowerCase().trim();

                    for (Product product : allProducts) {
                        if (product.getName() != null && product.getName().toLowerCase().contains(searchName)) {
                            nameFilteredProducts.add(product);
                            android.util.Log.d("ShoppingViewModel", "Name match: " + product.getName());
                        }
                    }

                    android.util.Log.d("ShoppingViewModel", "Name-only search found: " + nameFilteredProducts.size() + " products");

                    _searchResults.setValue(nameFilteredProducts);
                    _isEmpty.setValue(nameFilteredProducts.isEmpty());
                    isLastPage = currentPage >= response.getTotalPages() - 1;

                    if (nameFilteredProducts.isEmpty()) {
                        _error.setValue("Không tìm thấy sản phẩm nào có tên chứa: \"" + productName + "\"");
                    } else {
                        _error.setValue("");
                    }
                } else {
                    android.util.Log.e("ShoppingViewModel", "Name search response is null for: '" + productName + "'");
                    _error.setValue("Không tìm thấy sản phẩm");
                    _searchResults.setValue(new ArrayList<>());
                    _isEmpty.setValue(true);
                }
                responseLiveData.removeObserver(this);
            }
        };

        responseLiveData.observeForever(observer);
    }
}
