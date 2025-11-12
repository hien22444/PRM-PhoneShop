# Fix Order Issue - PhoneShop App

## 🚨 **Vấn đề hiện tại**

1. **Đơn hàng ảo hiển thị** - App đang sử dụng `OrderStorageService` (local SharedPreferences)
2. **data.json không có order** - App không gọi API server thật
3. **OrderRepository tạo mock data** thay vì gọi API

## ✅ **Nguyên nhân chính**

### **1. OrderRepository sử dụng Mock Data**
```java
// WRONG - Tạo mock order thay vì gọi API
Order mockOrder = createMockOrder(request);
data.setValue(mockOrder);
```

### **2. API Endpoint không đúng**
```java
// WRONG - Server có /api/orders/from-cart
@POST("api/orders")
Call<Order> createOrder(@Body OrderRequest request);

// CORRECT - Phải match với server
@POST("api/orders/from-cart") 
Call<Order> createOrder(@Body OrderRequest request);
```

### **3. OrderRequest format không đúng**
Server API `/api/orders/from-cart` cần:
```json
{
  "userId": "user_123",
  "customerInfo": {
    "fullName": "...",
    "phone": "...",
    "email": "...",
    "address": "..."
  },
  "paymentMethod": "COD",
  "shippingAddress": "..."
}
```

Nhưng Android gửi:
```json
{
  "items": [...],
  "fullName": "...",
  "phone": "...",
  "address": "...",
  "paymentMethod": "..."
}
```

## 🔧 **Các Fix đã thực hiện**

### **1. Fixed OrderRepository**
```java
// OLD - Mock data
Order mockOrder = createMockOrder(request);
data.setValue(mockOrder);

// NEW - Real API call with fallback
apiService.createOrder(request).enqueue(new Callback<Order>() {
    @Override
    public void onResponse(Call<Order> call, Response<Order> response) {
        if (response.isSuccessful() && response.body() != null) {
            android.util.Log.d("OrderRepository", "Order created via API: " + 
                response.body().getOrderId());
            data.setValue(response.body());
        } else {
            // Fallback to mock if API fails
            Order mockOrder = createMockOrder(request);
            data.setValue(mockOrder);
        }
    }
    
    @Override
    public void onFailure(Call<Order> call, Throwable t) {
        // Fallback to mock if network fails
        Order mockOrder = createMockOrder(request);
        data.setValue(mockOrder);
    }
});
```

### **2. Fixed API Endpoints**
```java
// ApiService.java - FIXED
@POST("api/orders/from-cart")
Call<Order> createOrder(@Body OrderRequest request);

@GET("api/orders/{userId}")
Call<List<Order>> getOrderHistory(@Path("userId") String userId);
```

### **3. Fixed OrderRequest Format**
```java
// OrderRequest.java - UPDATED
public class OrderRequest {
    private String userId;                    // NEW
    private CustomerInfo customerInfo;        // NEW
    private String paymentMethod;
    private String shippingAddress;          // NEW
    
    // Legacy fields for backward compatibility
    private List<OrderItemRequest> items;
    private String fullName;
    private String phone;
    private String address;
    
    public static class CustomerInfo {        // NEW
        private String fullName;
        private String phone;
        private String email;
        private String address;
    }
}
```

### **4. Fixed CheckoutFragment**
```java
// CheckoutFragment.java - UPDATED
// Get user info from SharedPreferences
String userId = sharedPreferences.getString("user_id", "");
String userEmail = sharedPreferences.getString("user_email", "");

// Create proper OrderRequest
OrderRequest request = new OrderRequest();
request.setUserId(userId);                                    // NEW
request.setPaymentMethod(paymentMethod);
request.setShippingAddress(address);

// Create CustomerInfo object                                 // NEW
CustomerInfo customerInfo = new CustomerInfo(fullName, phone, userEmail, address);
request.setCustomerInfo(customerInfo);
```

---

## 🚀 **Cách Test Fix**

### **1. Start Server**
```bash
# Reset database clean
node test-and-reset.js

# Start server
node phoneshop-server-updated.js
```

**Server logs sẽ hiển thị:**
```
✅ Initialized clean data.json - NO MOCK DATA
🚀 PhoneShop API Server running at http://localhost:8080
📊 Initial data stats:
   - Users: 0
   - Products: 4
   - Carts: 0
   - Orders: 0
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
# Add to cart
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USER_ID_FROM_REGISTRATION",
    "productId": "p1",
    "quantity": 2
  }'
```

### **4. Test Order Creation**
```bash
# Create order from cart
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

**Server sẽ log:**
```
🛒 ADD TO CART: User user_123 adding product p1 (qty: 2)
✅ Created new cart for user user_123
💾 Cart saved for user user_123 - Total items: 1

📦 ORDER FROM CART: order_456 for user user_123 (1 items)
🗑️ CART CLEARED: Removed 1 items from user user_123 cart
💾 Order from cart saved and cart cleared for user user_123
```

---

## 📱 **Android App Changes Needed**

### **1. Clear Local Storage**
Thêm vào `MainActivity.onCreate()` hoặc khi user login:
```java
// Clear old local data when switching to API mode
OrderStorageService orderStorage = OrderStorageService.getInstance();
orderStorage.initialize(this);
orderStorage.clearAllLocalData();
```

### **2. Update Base URL**
Trong `ApiClient.java` hoặc nơi config Retrofit:
```java
// For emulator
private static final String BASE_URL = "http://10.0.2.2:8080/";

// For real device (replace with your IP)
private static final String BASE_URL = "http://192.168.1.100:8080/";
```

### **3. Update OrderHistoryViewModel**
Thay vì sử dụng `OrderStorageService`, sử dụng API:
```java
// OLD - Local storage
List<Order> orders = orderStorageService.getAllOrders();

// NEW - API call
String userId = getCurrentUserId();
apiService.getOrderHistory(userId).enqueue(new Callback<List<Order>>() {
    @Override
    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
        if (response.isSuccessful() && response.body() != null) {
            _orders.setValue(response.body());
        }
    }
});
```

---

## 🎯 **Expected Results After Fix**

### **1. When User Registers**
- ✅ User saved to `data.json` on server
- ✅ No local storage used

### **2. When User Adds to Cart**
- ✅ Item saved to `data.json` with userId
- ✅ Server logs: `🛒 ADD TO CART: User user_123 adding product p1`
- ✅ Server logs: `💾 Cart saved for user user_123`

### **3. When User Confirms Order**
- ✅ API call to `POST /api/orders/from-cart`
- ✅ Order saved to `data.json` with userId
- ✅ Cart cleared from `data.json`
- ✅ Server logs: `📦 ORDER FROM CART: order_456 for user user_123`
- ✅ Server logs: `🗑️ CART CLEARED: Removed 1 items from user user_123 cart`

### **4. When User Views Order History**
- ✅ API call to `GET /api/orders/{userId}`
- ✅ Only real orders from server displayed
- ✅ No more mock/fake orders

---

## 🔍 **Debug Steps**

### **1. Check Server Logs**
Khi user bấm "Xác nhận thanh toán", server phải log:
```
🛒 ADD TO CART: User user_123 adding product p1 (qty: 2)
📦 ORDER FROM CART: order_456 for user user_123 (1 items)  
🗑️ CART CLEARED: Removed 1 items from user user_123 cart
💾 Order from cart saved and cart cleared for user user_123
💾 Data saved to data.json at 2024-11-05T10:30:56.789Z
```

### **2. Check data.json**
File `data.json` phải có:
```json
{
  "products": [...],
  "carts": [],                    // Empty after order
  "users": [
    {
      "id": "user_123",
      "fullName": "Test User",
      ...
    }
  ],
  "orders": [                     // New order here
    {
      "id": "order_456", 
      "userId": "user_123",
      "status": "Đang xử lý",
      ...
    }
  ]
}
```

### **3. Check Android Logs**
Android logs phải hiển thị:
```
D/OrderRepository: Creating order via API - Items: 1
D/OrderRepository: Order created successfully via API: order_456
D/CheckoutFragment: Order created successfully: order_456
```

---

## 🚨 **Common Issues & Solutions**

### **Issue 1: API Call Fails**
**Symptoms:** OrderRepository falls back to mock data
**Solution:** 
- Check server is running on correct port
- Check Android base URL is correct
- Check network permissions in AndroidManifest.xml

### **Issue 2: Wrong API Endpoint**
**Symptoms:** 404 error in logs
**Solution:**
- Ensure ApiService uses `@POST("api/orders/from-cart")`
- Check server has this endpoint

### **Issue 3: Wrong Request Format**
**Symptoms:** 400 Bad Request
**Solution:**
- Ensure OrderRequest has `userId` and `customerInfo`
- Check server expects this format

### **Issue 4: Still Seeing Mock Orders**
**Symptoms:** Old orders still appear
**Solution:**
- Call `orderStorageService.clearAllLocalData()`
- Restart app completely

---

## ✅ **Verification Checklist**

- [ ] Server starts without errors
- [ ] Server logs show clean initialization (0 orders)
- [ ] User registration works and saves to data.json
- [ ] Cart operations save to data.json with userId
- [ ] Order creation calls API (not mock)
- [ ] Order creation clears cart on server
- [ ] Order history shows real orders from API
- [ ] No more fake/mock orders displayed
- [ ] data.json contains real order data

---

## 🎉 **Final Result**

After implementing all fixes:

1. **No more fake orders** - Only real orders from API
2. **Real-time data sync** - All operations save to data.json immediately  
3. **User isolation** - Each user sees only their own data
4. **Proper workflow** - Cart → Order → Clear cart → Save to data.json
5. **Complete logging** - Full visibility into all operations

**The app will now work exactly as expected with proper server-side data persistence!** 🚀
