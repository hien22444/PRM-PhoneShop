# Fix Mock Data Issue - PhoneShop

## 🚨 **Vấn đề hiện tại**

Bạn đang thấy các đơn hàng "ảo" và dữ liệu mock vì:

1. **App Android đang sử dụng Local Storage** thay vì API server
2. **Dữ liệu mock/test** từ các lần test trước vẫn còn trong app
3. **Server chưa được kết nối** với Android app

## ✅ **Giải pháp hoàn chỉnh**

### **Bước 1: Reset Database Server**

```bash
# Xóa dữ liệu cũ và tạo database clean
node test-and-reset.js

# Start server với database clean
node phoneshop-server-updated.js
```

**Kết quả mong đợi:**
```
✅ Database reset successfully!
📁 Clean data.json created with:
   - 4 products
   - 0 users
   - 0 carts
   - 0 orders

🚀 PhoneShop API Server running at http://localhost:8080
📊 Initial data stats:
   - Users: 0
   - Products: 4
   - Carts: 0
   - Orders: 0
```

### **Bước 2: Kiểm tra Server hoạt động**

```bash
# Test server status
curl http://localhost:8080/api/status

# Test reset API (nếu cần)
curl -X POST http://localhost:8080/api/reset-database
```

### **Bước 3: Cấu hình Android App sử dụng API**

**Vấn đề:** App Android đang sử dụng `OrderStorageService` (local storage) thay vì API.

**Cần sửa trong Android:**

1. **Trong `CheckoutFragment.java`** - Đảm bảo gọi API thay vì local storage:
```java
// Thay vì OrderStorageService
// Sử dụng ApiService để tạo order
apiService.createOrderFromCart(orderRequest).enqueue(new Callback<OrderResponse>() {
    @Override
    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
        if (response.isSuccessful()) {
            // Order created successfully
            // Navigate to order history
        }
    }
});
```

2. **Trong `OrderHistoryFragment.java`** - Lấy orders từ API:
```java
// Thay vì OrderStorageService.getOrders()
// Sử dụng ApiService.getOrders(userId)
apiService.getOrders(userId).enqueue(new Callback<List<Order>>() {
    @Override
    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
        if (response.isSuccessful()) {
            List<Order> orders = response.body();
            // Update UI with real orders from server
        }
    }
});
```

3. **Trong `CartFragment.java`** - Sử dụng API cho cart operations:
```java
// Add to cart via API
apiService.addToCart(cartRequest).enqueue(new Callback<CartResponse>() {
    @Override
    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
        if (response.isSuccessful()) {
            // Item added to cart successfully
            // Refresh cart display
        }
    }
});
```

### **Bước 4: Clear Local Storage trong Android**

**Thêm method để xóa local storage:**

```java
// Trong OrderStorageService.java
public void clearAllLocalData() {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.clear();
    editor.apply();
    Log.d("OrderStorageService", "All local data cleared");
}

// Trong CartStorageService.java (nếu có)
public void clearAllCartData() {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.clear(); 
    editor.apply();
    Log.d("CartStorageService", "All cart data cleared");
}
```

**Gọi khi app start hoặc user logout:**
```java
// Trong MainActivity.onCreate() hoặc AuthViewModel.logout()
orderStorageService.clearAllLocalData();
cartStorageService.clearAllCartData();
```

---

## 🔧 **Server APIs đã được Fix**

### **✅ User Isolation hoàn toàn**
- Mỗi user chỉ thấy cart và orders của mình
- Validation user tồn tại trước mọi operations
- Detailed logging cho mọi operations

### **✅ Real-time Data Persistence**
- **Cart Add**: `POST /api/cart/add` → Lưu ngay vào data.json
- **Cart Remove**: `DELETE /api/cart/remove` → Cập nhật ngay data.json  
- **Order Create**: `POST /api/orders/from-cart` → Lưu order + Xóa cart ngay
- **Profile Update**: `PUT /api/auth/user/:id` → Lưu ngay vào data.json

### **✅ Enhanced Logging**
Server sẽ log tất cả operations:
```
🛒 ADD TO CART: User user_123 adding product p1 (qty: 2)
✅ Created new cart for user user_123
✅ Added new item p1 to cart
💾 Cart saved for user user_123 - Total items: 1
💾 Data saved to data.json at 2024-11-05T10:30:56.789Z

📦 ORDER FROM CART: order_456 for user user_123 (1 items)
🗑️ CART CLEARED: Removed 1 items from user user_123 cart after order from cart
💾 Order from cart saved and cart cleared for user user_123
💾 Data saved to data.json at 2024-11-05T10:31:15.123Z
```

---

## 🎯 **Test Workflow hoàn chỉnh**

### **1. Reset và Start Server**
```bash
node test-and-reset.js
node phoneshop-server-updated.js
```

### **2. Test User Registration**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com", 
    "username": "testuser",
    "password": "123456"
  }'
```

### **3. Test Cart Operations**
```bash
# Add to cart (sẽ lưu vào data.json)
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USER_ID_FROM_REGISTRATION",
    "productId": "p1",
    "quantity": 2
  }'

# View cart
curl http://localhost:8080/api/cart/USER_ID_FROM_REGISTRATION
```

### **4. Test Order Creation**
```bash
# Create order from cart (sẽ lưu order + xóa cart)
curl -X POST http://localhost:8080/api/orders/from-cart \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USER_ID_FROM_REGISTRATION",
    "customerInfo": {
      "fullName": "Test User",
      "phone": "0123456789",
      "email": "test@example.com",
      "address": "123 Test Street"
    },
    "paymentMethod": "COD"
  }'
```

### **5. Verify Results**
```bash
# Check cart is empty
curl http://localhost:8080/api/cart/USER_ID_FROM_REGISTRATION

# Check order exists  
curl http://localhost:8080/api/orders/USER_ID_FROM_REGISTRATION

# Check server status
curl http://localhost:8080/api/status
```

---

## 🚨 **Lưu ý quan trọng**

### **Android App cần sửa:**
1. **Thay OrderStorageService bằng API calls**
2. **Thay CartStorageService bằng API calls**  
3. **Clear local storage khi switch sang API**
4. **Update base URL**: `http://10.0.2.2:8080` (cho emulator) hoặc `http://YOUR_IP:8080`

### **Server đã sẵn sàng:**
- ✅ Clean database initialization
- ✅ User isolation hoàn toàn
- ✅ Real-time data persistence  
- ✅ Comprehensive logging
- ✅ Reset API để clear test data

### **Workflow mong đợi:**
1. User đăng ký → Lưu vào data.json
2. User thêm vào cart → Lưu vào data.json ngay lập tức
3. User xóa khỏi cart → Cập nhật data.json ngay lập tức
4. User bấm "Xác nhận thanh toán" → Tạo order + Xóa cart trong data.json
5. User xem lịch sử → Chỉ thấy orders của mình

---

## 🎉 **Kết quả sau khi fix**

- ✅ **Không còn đơn hàng ảo**
- ✅ **User mới sẽ có cart và order history trống**
- ✅ **Mọi thao tác lưu ngay vào data.json**
- ✅ **User isolation hoàn toàn**
- ✅ **Real-time data synchronization**

**Commands để bắt đầu:**
```bash
# 1. Reset database
node test-and-reset.js

# 2. Start server
node phoneshop-server-updated.js

# 3. Test registration
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{"fullName":"New User","email":"new@test.com","username":"newuser","password":"123456"}'
```
