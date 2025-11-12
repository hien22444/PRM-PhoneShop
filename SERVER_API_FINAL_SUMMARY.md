# PhoneShop Server - Final API Summary

## 🚀 **Server đã được cập nhật hoàn chỉnh**

File server hiện tại: `phoneshop-server-updated.js` đã có đầy đủ tất cả API cần thiết.

## 📊 **Data Structure trong data.json**
```json
{
  "products": [
    {
      "id": "p1",
      "name": "Samsung Galaxy S24 Ultra",
      "brand": "Samsung",
      "price": 25000000,
      "stock": 10,
      "visible": true,
      "images": ["https://picsum.photos/seed/1/300/300"]
    }
  ],
  "carts": [
    {
      "userId": "user_123",
      "items": [
        {
          "productId": "p1",
          "name": "Samsung Galaxy S24 Ultra",
          "price": 25000000,
          "image": "https://picsum.photos/seed/1/300/300",
          "quantity": 2
        }
      ]
    }
  ],
  "users": [
    {
      "id": "user_123",
      "fullName": "Nguyễn Văn A",
      "email": "user@example.com",
      "username": "username",
      "password": "hashed_password",
      "phone": "0123456789",
      "address": "123 ABC Street",
      "dateOfBirth": "1990-01-01",
      "gender": "male",
      "avatarUrl": "https://example.com/avatar.jpg",
      "createdAt": "2024-11-05T10:30:56.789Z",
      "updatedAt": "2024-11-05T10:30:56.789Z",
      "isActive": true
    }
  ],
  "orders": [
    {
      "id": "order_456",
      "userId": "user_123",
      "customerInfo": {
        "fullName": "Nguyễn Văn A",
        "phone": "0123456789",
        "email": "user@example.com",
        "address": "123 ABC Street"
      },
      "items": [
        {
          "productId": "p1",
          "name": "Samsung Galaxy S24 Ultra",
          "price": 25000000,
          "quantity": 2,
          "image": "https://picsum.photos/seed/1/300/300"
        }
      ],
      "paymentMethod": "COD",
      "totalAmount": 50000000,
      "status": "Đang xử lý",
      "createdAt": "2024-11-05T10:30:56.789Z",
      "updatedAt": "2024-11-05T10:30:56.789Z"
    }
  ]
}
```

---

## 🔐 **USER AUTHENTICATION APIs**

### ✅ **POST** `/api/auth/register`
**Đăng ký user mới**
```json
Request:
{
  "fullName": "Nguyễn Văn A",
  "email": "user@example.com",
  "username": "username",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Đăng ký thành công",
  "user": {
    "id": "user_123",
    "fullName": "Nguyễn Văn A",
    "email": "user@example.com",
    "username": "username",
    ...
  }
}
```
**💾 Lưu vào data.json**: User mới được thêm vào `users` array

### ✅ **POST** `/api/auth/login`
**Đăng nhập user**
```json
Request:
{
  "username": "username",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Đăng nhập thành công",
  "user": { ... }
}
```

### ✅ **GET** `/api/auth/user/:id`
**Lấy thông tin user**

### ✅ **PUT** `/api/auth/user/:id`
**Cập nhật profile user**
```json
Request:
{
  "fullName": "Nguyễn Văn A Updated",
  "email": "newemail@example.com",
  "phone": "0987654321",
  "address": "456 XYZ Street",
  "dateOfBirth": "1990-01-15",
  "gender": "male"
}
```
**💾 Cập nhật data.json**: User info được update trong `users` array

### ✅ **PUT** `/api/auth/user/:id/avatar`
**Cập nhật avatar**
```json
Request:
{
  "avatarUrl": "https://example.com/new-avatar.jpg"
}
```

### ✅ **PUT** `/api/auth/user/:id/password`
**Đổi mật khẩu**

### ✅ **GET** `/api/auth/check-username/:username`
**Kiểm tra username tồn tại**

### ✅ **GET** `/api/auth/check-email/:email`
**Kiểm tra email tồn tại**

---

## 🛒 **CART MANAGEMENT APIs**

### ✅ **POST** `/api/cart/add`
**Thêm sản phẩm vào giỏ hàng**
```json
Request:
{
  "userId": "user_123",
  "productId": "p1",
  "quantity": 2
}

Response:
{
  "success": true,
  "message": "Đã thêm sản phẩm vào giỏ hàng",
  "cart": { ... }
}
```
**💾 Lưu vào data.json**: Item được thêm vào cart của user trong `carts` array
**🔍 Server logs**: `🛒 ADD TO CART: User user_123 adding product p1 (qty: 2)`

### ✅ **GET** `/api/cart/:userId`
**Xem giỏ hàng theo userId**
```json
Response:
{
  "userId": "user_123",
  "items": [...]
}
```

### ✅ **PATCH** `/api/cart/update`
**Cập nhật số lượng sản phẩm**
```json
Request:
{
  "userId": "user_123",
  "productId": "p1",
  "quantity": 3
}
```
**💾 Cập nhật data.json**: Quantity được update trong cart

### ✅ **DELETE** `/api/cart/remove`
**Xóa sản phẩm khỏi giỏ hàng**
```json
Request:
{
  "userId": "user_123",
  "productId": "p1"
}
```
**💾 Cập nhật data.json**: Item được xóa khỏi cart
**🔍 Server logs**: `🗑️ REMOVE FROM CART: User user_123 removing product p1`

### ✅ **DELETE** `/api/cart/clear/:userId`
**Xóa toàn bộ giỏ hàng**
**💾 Cập nhật data.json**: Toàn bộ cart của user được xóa

### ✅ **GET** `/api/cart/:userId/count`
**Lấy số lượng sản phẩm trong giỏ**
```json
Response:
{
  "success": true,
  "totalItems": 5,
  "itemCount": 3
}
```

---

## 📦 **ORDER MANAGEMENT APIs**

### ✅ **POST** `/api/orders/from-cart` ⭐ **MAIN API**
**Tạo đơn hàng từ giỏ hàng (Android sử dụng API này)**
```json
Request:
{
  "userId": "user_123",
  "customerInfo": {
    "fullName": "Nguyễn Văn A",
    "phone": "0123456789",
    "email": "user@example.com",
    "address": "123 ABC Street"
  },
  "paymentMethod": "COD",
  "shippingAddress": "123 ABC Street"
}

Response:
{
  "success": true,
  "message": "Tạo đơn hàng từ giỏ hàng thành công",
  "order": {
    "id": "order_456",
    "userId": "user_123",
    "status": "Đang xử lý",
    ...
  }
}
```
**💾 Lưu vào data.json**: 
1. Order mới được thêm vào `orders` array
2. Cart của user được xóa khỏi `carts` array
**🔍 Server logs**: 
```
📦 ORDER FROM CART: order_456 for user user_123 (2 items)
🗑️ CART CLEARED: Removed 2 items from user user_123 cart
💾 Order from cart saved and cart cleared for user user_123
```

### ✅ **POST** `/api/orders`
**Tạo đơn hàng thủ công**
```json
Request:
{
  "userId": "user_123",
  "customerInfo": {...},
  "items": [...],
  "paymentMethod": "COD",
  "totalAmount": 50000000
}
```

### ✅ **GET** `/api/orders/:userId` ⭐ **MAIN API**
**Lấy lịch sử đơn hàng theo userId (Android sử dụng API này)**
```json
Response:
[
  {
    "id": "order_456",
    "userId": "user_123",
    "status": "Đang xử lý",
    "totalAmount": 50000000,
    "createdAt": "2024-11-05T10:30:56.789Z",
    ...
  }
]
```
**📖 Đọc từ data.json**: Filter orders theo userId từ `orders` array

### ✅ **GET** `/api/orders/detail/:orderId`
**Lấy chi tiết đơn hàng**

### ✅ **PATCH** `/api/orders/:orderId/status`
**Cập nhật trạng thái đơn hàng**
```json
Request:
{
  "status": "Đã thanh toán"
}
```
**💾 Cập nhật data.json**: Status và updatedAt được update

### ✅ **DELETE** `/api/orders/:orderId`
**Hủy đơn hàng**
**💾 Cập nhật data.json**: Status được đổi thành "Đã hủy"

---

## 📱 **PRODUCT APIs**

### ✅ **GET** `/api/products`
**Lấy danh sách sản phẩm**
```
Query parameters:
- page: số trang (default: 0)
- size: số items per page (default: 20)  
- q: search query
- brand: filter theo brand
```

### ✅ **GET** `/api/products/:id`
**Lấy chi tiết sản phẩm**

### ✅ **POST** `/api/products` (Admin)
**Thêm sản phẩm mới**

### ✅ **PUT** `/api/products/:id` (Admin)
**Cập nhật sản phẩm**

### ✅ **DELETE** `/api/products/:id` (Admin)
**Xóa sản phẩm**

---

## 🔧 **UTILITY APIs**

### ✅ **GET** `/api/status`
**Kiểm tra trạng thái server**
```json
Response:
{
  "server": {
    "status": "running",
    "port": 8080,
    "timestamp": "2024-11-05T10:30:56.789Z"
  },
  "database": {
    "file": "/path/to/data.json",
    "exists": true,
    "stats": {
      "users": 5,
      "products": 4,
      "carts": 2,
      "orders": 8
    }
  }
}
```

### ✅ **POST** `/api/backup`
**Tạo backup dữ liệu**

### ✅ **POST** `/api/reset-database`
**Reset database về trạng thái clean (Development only)**

---

## 🎯 **Android App Integration**

### **Các API chính Android sử dụng:**

1. **User Registration/Login:**
   - `POST /api/auth/register`
   - `POST /api/auth/login`
   - `PUT /api/auth/user/:id` (profile update)

2. **Cart Management:**
   - `POST /api/cart/add` (thêm vào giỏ)
   - `GET /api/cart/:userId` (xem giỏ hàng)
   - `DELETE /api/cart/remove` (xóa khỏi giỏ)
   - `PATCH /api/cart/update` (cập nhật số lượng)

3. **Order Management:**
   - `POST /api/orders/from-cart` ⭐ **Tạo đơn hàng**
   - `GET /api/orders/:userId` ⭐ **Xem lịch sử đơn hàng**

### **Data Flow:**
```
Android App → API Server → data.json
     ↓              ↓           ↓
User actions → Real-time → Persistent storage
```

---

## 🚀 **Server Start Commands**

### **Development:**
```bash
# Reset database clean
node test-and-reset.js

# Start server
node phoneshop-server-updated.js
```

### **Expected Output:**
```
✅ Initialized clean data.json - NO MOCK DATA
🚀 PhoneShop API Server running at http://localhost:8080
📁 Data file: /path/to/data.json
📊 Initial data stats:
   - Users: 0
   - Products: 4
   - Carts: 0
   - Orders: 0
🔗 Status endpoint: http://localhost:8080/api/status
```

### **Runtime Logs:**
```
🛒 ADD TO CART: User user_123 adding product p1 (qty: 2)
✅ Created new cart for user user_123
💾 Cart saved for user user_123 - Total items: 1
💾 Data saved to data.json at 2024-11-05T10:30:56.789Z

📦 ORDER FROM CART: order_456 for user user_123 (1 items)
🗑️ CART CLEARED: Removed 1 items from user user_123 cart
💾 Order from cart saved and cart cleared for user user_123
💾 Data saved to data.json at 2024-11-05T10:30:56.789Z
```

---

## ✅ **Server Features Summary**

### **✅ Complete API Coverage:**
- User authentication & profile management
- Cart operations with user isolation
- Order creation & management
- Product catalog
- Admin functions

### **✅ Data Persistence:**
- All operations save to data.json immediately
- Real-time logging of all operations
- User-specific data isolation
- Automatic cart clearing after order creation

### **✅ Error Handling:**
- Comprehensive validation
- Proper HTTP status codes
- Detailed error messages
- Graceful fallbacks

### **✅ Development Tools:**
- Status endpoint for monitoring
- Database reset functionality
- Backup creation
- Detailed logging

---

## 🎉 **Server Ready for Production!**

**File server hiện tại `phoneshop-server-updated.js` đã hoàn chỉnh và sẵn sàng sử dụng với Android app.**

**Key Points:**
- ✅ Tất cả API đã được implement
- ✅ Data persistence hoạt động đúng
- ✅ User isolation hoàn toàn
- ✅ Real-time logging
- ✅ Compatible với Android app
- ✅ No mock data - chỉ real data

**Start server:** `node phoneshop-server-updated.js` 🚀
