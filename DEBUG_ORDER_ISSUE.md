# Debug Order Issue - PhoneShop

## 🚨 **Vấn đề hiện tại**
User bấm "Xác nhận Đặt hàng" nhưng:
- ❌ Order không lưu vào data.json
- ❌ Order không hiện trong lịch sử đơn hàng

## 🔍 **Các nguyên nhân có thể**

### **1. Network Connection Issue**
- Android app không connect được tới server
- Server không chạy hoặc chạy sai port
- CORS issues

### **2. API Call Issues**
- Android gọi sai endpoint
- Request format không đúng
- Server validation failed

### **3. Server Issues**
- Server crash khi xử lý request
- Database save failed
- Logic error trong code

## 🛠️ **Debug Steps**

### **Step 1: Kiểm tra Server**
```bash
# Start server với logging
node server.js
```

**Expected output:**
```
✅ Initialized clean data.json - NO MOCK DATA
🚀 PhoneShop API Server running at http://localhost:8080
📊 Initial data stats:
   - Users: 0
   - Products: 4
   - Carts: 0
   - Orders: 0
```

### **Step 2: Test API với script**
```bash
# Install axios if needed
npm install axios

# Run test script
node test-order-flow.js
```

**Expected output:**
```
🚀 Testing PhoneShop Order Flow...

1️⃣ Checking server status...
✅ Server is running

2️⃣ Registering test user...
✅ User registered successfully
👤 User ID: user_1699999999999_abc123

3️⃣ Adding product to cart...
✅ Product added to cart
🛒 Cart items: 1

4️⃣ Viewing cart...
✅ Cart retrieved
🛒 Cart has 1 items

5️⃣ Creating order from cart...
✅ Order created successfully
📦 Order ID: order_1699999999999_def456
💰 Total Amount: 50,000,000 VND

6️⃣ Checking cart after order...
🛒 Cart items after order: 0
✅ Cart cleared successfully after order

7️⃣ Getting order history...
📋 Order history: 1 orders
✅ Order found in history
```

### **Step 3: Kiểm tra Android Logs**
Trong Android Studio, check Logcat cho:

**OrderRepository logs:**
```
D/OrderRepository: Creating order via API - Items: 1
D/OrderRepository: Order created successfully via API: order_123
```

**CheckoutFragment logs:**
```
D/CheckoutFragment: Order request created - Items: 1, Payment: COD
D/CheckoutFragment: Order created successfully: order_123
```

**Network logs:**
```
D/OkHttp: --> POST http://10.0.2.2:8080/api/orders/from-cart
D/OkHttp: <-- 201 Created
```

### **Step 4: Kiểm tra data.json**
File `data.json` phải có:
```json
{
  "orders": [
    {
      "id": "order_123",
      "userId": "user_123",
      "status": "Đang xử lý",
      "totalAmount": 50000000,
      "items": [...],
      "createdAt": "2024-11-05T10:30:56.789Z"
    }
  ],
  "carts": []  // Empty after order
}
```

## 🔧 **Fixes đã thực hiện**

### **1. Fixed GET orders API**
```javascript
// OLD - Wrapped in object
res.json({
  success: true,
  orders: paginatedOrders,
  ...
});

// NEW - Direct array as Android expects
res.json(userOrders);
```

### **2. Enhanced Logging**
```javascript
// Added detailed logging to orders/from-cart
console.log(`📦 CREATE ORDER FROM CART: Received request`);
console.log(`📦 Request body:`, JSON.stringify(req.body, null, 2));
console.log(`📦 ORDER FROM CART: ${newOrder.id} for user ${userId}`);
console.log(`🗑️ CART CLEARED: Removed ${cartItemCount} items`);
console.log(`💾 Order from cart saved and cart cleared`);
```

### **3. Added Test Script**
- `test-order-flow.js` - Complete API flow test
- Tests user registration → cart → order → history

## 🚨 **Common Issues & Solutions**

### **Issue 1: Server not running**
**Symptoms:** Connection refused, network error
**Solution:**
```bash
node server.js
# Check http://localhost:8080/api/status
```

### **Issue 2: Wrong base URL in Android**
**Symptoms:** API calls fail, network error
**Solution:**
```java
// For emulator
private static final String BASE_URL = "http://10.0.2.2:8080/";

// For real device (replace with your IP)
private static final String BASE_URL = "http://192.168.1.100:8080/";
```

### **Issue 3: API call falls back to mock**
**Symptoms:** Order created but not in data.json
**Check Android logs:**
```
D/OrderRepository: API call failed - Response code: 404
W/OrderRepository: Falling back to mock order
```
**Solution:** Check server endpoint exists and is correct

### **Issue 4: Request validation failed**
**Symptoms:** 400 Bad Request
**Check server logs:**
```
❌ Validation failed - userId: undefined, customerInfo: false, paymentMethod: COD
```
**Solution:** Check OrderRequest format in Android

### **Issue 5: User not found**
**Symptoms:** 404 User not found
**Solution:** Make sure user is registered and userId is correct

### **Issue 6: Empty cart**
**Symptoms:** 400 Giỏ hàng trống
**Solution:** Add items to cart before creating order

## 📱 **Android Debug Checklist**

### **CheckoutFragment:**
- [ ] `userId` được lấy từ SharedPreferences
- [ ] `customerInfo` được tạo đúng format
- [ ] `paymentMethod` không null
- [ ] API call được thực hiện (không fallback to mock)

### **OrderRepository:**
- [ ] `createOrder()` gọi API thật
- [ ] Response được handle đúng
- [ ] Không fallback to mock order

### **OrderHistoryViewModel:**
- [ ] `loadOrderHistory(userId)` được gọi với userId
- [ ] API response được parse đúng
- [ ] UI được update với real data

### **ApiService:**
- [ ] `@POST("api/orders/from-cart")` đúng endpoint
- [ ] `@GET("api/orders/{userId}")` đúng endpoint
- [ ] Base URL đúng

## 🎯 **Expected Behavior**

### **When user clicks "Xác nhận Đặt hàng":**

**1. Android logs:**
```
D/CheckoutFragment: Order request created - Items: 1, Payment: COD
D/OrderRepository: Creating order via API - Items: 1
D/OrderRepository: Order created successfully via API: order_123
D/CheckoutFragment: Order created successfully: order_123
```

**2. Server logs:**
```
📦 CREATE ORDER FROM CART: Received request
📦 ORDER FROM CART: order_123 for user user_123 (1 items)
🗑️ CART CLEARED: Removed 1 items from user user_123 cart
💾 Order from cart saved and cart cleared for user user_123
💾 Data saved to data.json at 2024-11-05T10:30:56.789Z
```

**3. data.json updated:**
```json
{
  "orders": [
    {
      "id": "order_123",
      "userId": "user_123",
      ...
    }
  ],
  "carts": []
}
```

**4. Order history shows new order**

## 🔍 **Debug Commands**

### **Test server API:**
```bash
# Check status
curl http://localhost:8080/api/status

# Test register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Test","email":"test@test.com","username":"test","password":"123"}'

# Test add to cart
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{"userId":"USER_ID","productId":"p1","quantity":1}'

# Test create order
curl -X POST http://localhost:8080/api/orders/from-cart \
  -H "Content-Type: application/json" \
  -d '{"userId":"USER_ID","customerInfo":{"fullName":"Test","phone":"123","email":"test@test.com","address":"123 St"},"paymentMethod":"COD"}'

# Test get orders
curl http://localhost:8080/api/orders/USER_ID
```

### **Reset database:**
```bash
node test-and-reset.js
```

## ✅ **Success Indicators**

- [ ] Server starts without errors
- [ ] Test script passes completely
- [ ] data.json contains real orders
- [ ] Android shows orders in history
- [ ] Cart is cleared after order
- [ ] Server logs show all operations

**If all checks pass, the order flow should work correctly!** 🎉
