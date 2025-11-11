# Android App Integration Guide - Order & Profile APIs

## 🎯 Required Android App Updates

### 1. **Create Order Request/Response Models**

**OrderRequest.java:**
```java
package com.example.phoneshop.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderRequest {
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("customerInfo")
    private CustomerInfo customerInfo;
    
    @SerializedName("items")
    private List<OrderItem> items;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("totalAmount")
    private long totalAmount;
    
    @SerializedName("shippingAddress")
    private String shippingAddress;

    // Constructors, getters, setters...
    
    public static class CustomerInfo {
        @SerializedName("fullName")
        private String fullName;
        
        @SerializedName("phone")
        private String phone;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("address")
        private String address;
        
        // Constructors, getters, setters...
    }
    
    public static class OrderItem {
        @SerializedName("productId")
        private String productId;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("price")
        private long price;
        
        @SerializedName("quantity")
        private int quantity;
        
        @SerializedName("image")
        private String image;
        
        // Constructors, getters, setters...
    }
}
```

**OrderResponse.java:**
```java
package com.example.phoneshop.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("order")
    private Order order;
    
    @SerializedName("orders")
    private List<Order> orders;
    
    @SerializedName("totalElements")
    private int totalElements;
    
    @SerializedName("totalPages")
    private int totalPages;
    
    @SerializedName("currentPage")
    private int currentPage;
    
    // Constructors, getters, setters...
}
```

**Order.java:**
```java
package com.example.phoneshop.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Order {
    @SerializedName("id")
    private String id;
    
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("customerInfo")
    private CustomerInfo customerInfo;
    
    @SerializedName("items")
    private List<OrderItem> items;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("totalAmount")
    private long totalAmount;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;
    
    // Constructors, getters, setters...
}
```

### 2. **Update ApiService Interface**

Add these methods to `ApiService.java`:

```java
// ========== ORDER ENDPOINTS ==========

// Tạo đơn hàng mới
@POST("api/orders")
Call<OrderResponse> createOrder(@Body OrderRequest request);

// Lấy danh sách đơn hàng của user
@GET("api/orders/{userId}")
Call<OrderResponse> getUserOrders(
    @Path("userId") String userId,
    @Query("page") int page,
    @Query("size") int size
);

// Lấy chi tiết đơn hàng
@GET("api/orders/detail/{orderId}")
Call<OrderResponse> getOrderDetail(@Path("orderId") String orderId);

// Cập nhật trạng thái đơn hàng
@PATCH("api/orders/{orderId}/status")
Call<OrderResponse> updateOrderStatus(
    @Path("orderId") String orderId,
    @Body OrderStatusRequest request
);

// Hủy đơn hàng
@DELETE("api/orders/{orderId}")
Call<OrderResponse> cancelOrder(
    @Path("orderId") String orderId,
    @Body CancelOrderRequest request
);

// ========== ENHANCED PROFILE ENDPOINTS ==========

// Cập nhật avatar
@PUT("api/auth/user/{id}/avatar")
Call<AuthResponse> updateAvatar(
    @Path("id") String userId,
    @Body AvatarRequest request
);
```

### 3. **Update AuthRequest for Enhanced Profile**

Add these methods to `AuthRequest.java`:

```java
// Additional fields for profile
@SerializedName("phone")
private String phone;

@SerializedName("address")
private String address;

@SerializedName("dateOfBirth")
private String dateOfBirth;

@SerializedName("gender")
private String gender;

@SerializedName("avatarUrl")
private String avatarUrl;

// Enhanced profile update factory method
public static AuthRequest forEnhancedProfileUpdate(String fullName, String email, 
    String phone, String address, String dateOfBirth, String gender) {
    AuthRequest request = new AuthRequest();
    request.fullName = fullName;
    request.email = email;
    request.phone = phone;
    request.address = address;
    request.dateOfBirth = dateOfBirth;
    request.gender = gender;
    return request;
}

// Avatar update factory method
public static AuthRequest forAvatarUpdate(String avatarUrl) {
    AuthRequest request = new AuthRequest();
    request.avatarUrl = avatarUrl;
    return request;
}

// Getters and setters for new fields...
```

### 4. **Update User Model**

Add these fields to `User.java`:

```java
@SerializedName("phone")
private String phone;

@SerializedName("address")
private String address;

@SerializedName("dateOfBirth")
private String dateOfBirth;

@SerializedName("gender")
private String gender;

@SerializedName("avatarUrl")
private String avatarUrl;

@SerializedName("updatedAt")
private String updatedAt;

// Add getters and setters for new fields...
```

---

## 🔄 Implementation in Fragments

### 1. **CheckoutFragment Integration**

```java
public class CheckoutFragment extends Fragment {
    
    private void createOrder() {
        // Collect order data
        OrderRequest.CustomerInfo customerInfo = new OrderRequest.CustomerInfo(
            fullName, phone, email, address
        );
        
        List<OrderRequest.OrderItem> orderItems = convertCartItemsToOrderItems(cartItems);
        
        OrderRequest orderRequest = new OrderRequest(
            userId,
            customerInfo,
            orderItems,
            selectedPaymentMethod,
            totalAmount,
            shippingAddress
        );
        
        // Call API
        apiService.createOrder(orderRequest).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Order createdOrder = response.body().getOrder();
                    
                    // Save order ID for payment processing
                    String orderId = createdOrder.getId();
                    
                    // Navigate to payment or success screen
                    if ("COD".equals(selectedPaymentMethod)) {
                        // COD - order is already "Đang xử lý"
                        navigateToOrderSuccess(orderId);
                    } else {
                        // Online payment - proceed to payment gateway
                        proceedToPayment(orderId);
                    }
                    
                } else {
                    showError("Tạo đơn hàng thất bại");
                }
            }
            
            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    private void proceedToPayment(String orderId) {
        // After successful payment, update order status
        OrderStatusRequest statusRequest = new OrderStatusRequest("Đã thanh toán");
        
        apiService.updateOrderStatus(orderId, statusRequest).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    navigateToOrderSuccess(orderId);
                }
            }
            
            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                // Handle error
            }
        });
    }
}
```

### 2. **OrderHistoryFragment Integration**

```java
public class OrderHistoryFragment extends Fragment {
    
    private void loadUserOrders() {
        apiService.getUserOrders(userId, currentPage, pageSize).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Order> orders = response.body().getOrders();
                    
                    // Update RecyclerView
                    orderAdapter.updateOrders(orders);
                    
                    // Update pagination info
                    totalPages = response.body().getTotalPages();
                    totalElements = response.body().getTotalElements();
                    
                } else {
                    showEmptyState();
                }
            }
            
            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                showError("Lỗi tải đơn hàng: " + t.getMessage());
            }
        });
    }
    
    private void onOrderClick(Order order) {
        // Navigate to order detail
        Bundle args = new Bundle();
        args.putString("orderId", order.getId());
        
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_orderHistory_to_orderDetail, args);
    }
}
```

### 3. **OrderDetailFragment Integration**

```java
public class OrderDetailFragment extends Fragment {
    
    private void loadOrderDetail(String orderId) {
        apiService.getOrderDetail(orderId).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Order order = response.body().getOrder();
                    displayOrderDetail(order);
                } else {
                    showError("Không tìm thấy đơn hàng");
                }
            }
            
            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                showError("Lỗi tải chi tiết đơn hàng");
            }
        });
    }
    
    private void cancelOrder(String orderId) {
        CancelOrderRequest cancelRequest = new CancelOrderRequest(userId);
        
        apiService.cancelOrder(orderId, cancelRequest).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showSuccess("Hủy đơn hàng thành công");
                    // Refresh order detail
                    loadOrderDetail(orderId);
                } else {
                    showError(response.body().getMessage());
                }
            }
            
            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                showError("Lỗi hủy đơn hàng");
            }
        });
    }
}
```

### 4. **Enhanced ProfileFragment**

```java
public class ProfileFragment extends Fragment {
    
    private void updateProfile() {
        AuthRequest updateRequest = AuthRequest.forEnhancedProfileUpdate(
            fullName, email, phone, address, dateOfBirth, selectedGender
        );
        
        apiService.updateUser(userId, updateRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User updatedUser = response.body().getUser();
                    
                    // Update UI with new data
                    displayUserInfo(updatedUser);
                    
                    // Update local storage if needed
                    saveUserToPreferences(updatedUser);
                    
                    showSuccess("Cập nhật thông tin thành công");
                } else {
                    showError(response.body().getMessage());
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showError("Lỗi cập nhật thông tin");
            }
        });
    }
    
    private void updateAvatar(String avatarUrl) {
        AuthRequest avatarRequest = AuthRequest.forAvatarUpdate(avatarUrl);
        
        apiService.updateAvatar(userId, avatarRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User updatedUser = response.body().getUser();
                    
                    // Update avatar display
                    loadAvatarImage(updatedUser.getAvatarUrl());
                    
                    showSuccess("Cập nhật avatar thành công");
                } else {
                    showError("Cập nhật avatar thất bại");
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showError("Lỗi cập nhật avatar");
            }
        });
    }
}
```

---

## 📱 UI Updates Required

### 1. **Add Order Status Colors**

In `colors.xml`:
```xml
<color name="status_pending">#FF9800</color>
<color name="status_processing">#2196F3</color>
<color name="status_paid">#4CAF50</color>
<color name="status_shipping">#9C27B0</color>
<color name="status_delivered">#4CAF50</color>
<color name="status_cancelled">#F44336</color>
```

### 2. **Order Status Helper**

```java
public class OrderStatusHelper {
    public static int getStatusColor(String status) {
        switch (status) {
            case "Chờ thanh toán": return R.color.status_pending;
            case "Đã thanh toán": return R.color.status_paid;
            case "Đang xử lý": return R.color.status_processing;
            case "Đang giao hàng": return R.color.status_shipping;
            case "Đã giao hàng": return R.color.status_delivered;
            case "Đã hủy": return R.color.status_cancelled;
            default: return R.color.status_pending;
        }
    }
    
    public static String getStatusText(String status) {
        // Return localized status text
        return status;
    }
}
```

---

## 🔧 Testing Checklist

### Order Management
- [ ] Create order with COD payment
- [ ] Create order with online payment
- [ ] View order history with pagination
- [ ] View order details
- [ ] Update order status after payment
- [ ] Cancel order (valid status only)
- [ ] Handle order creation errors

### Profile Management
- [ ] Update basic profile info (name, email)
- [ ] Update extended profile (phone, address, DOB, gender)
- [ ] Update avatar/profile picture
- [ ] Handle email uniqueness validation
- [ ] Verify data persistence in data.json
- [ ] Handle profile update errors

### Integration
- [ ] Order data flows from checkout to history
- [ ] Profile updates reflect immediately in UI
- [ ] Server data.json file updates correctly
- [ ] Error handling for network failures
- [ ] Loading states and user feedback

---

## 🚀 Deployment Steps

1. **Update Server File**: Replace current server with `phoneshop-server-updated.js`
2. **Add Android Models**: Create all new request/response models
3. **Update ApiService**: Add new endpoints
4. **Update Fragments**: Implement API integration
5. **Test Thoroughly**: Verify all functionality works
6. **Deploy**: Start updated server and test with Android app

---

**Ready for implementation!** 🎉

The server now supports complete order management and enhanced profile updates with data persistence to data.json file.
