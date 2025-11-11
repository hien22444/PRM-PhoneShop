# PhoneShop - Complete API Reference for data.json Operations

## 📊 Data Structure trong data.json
```json
{
  "products": [...],
  "carts": [
    {
      "userId": "user_123",
      "items": [
        {
          "productId": "p1",
          "name": "Samsung Galaxy S24",
          "price": 25000000,
          "image": "https://example.com/image.jpg",
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
      "id": "order_123",
      "userId": "user_123",
      "customerInfo": {
        "fullName": "Nguyễn Văn A",
        "phone": "0123456789",
        "email": "user@example.com",
        "address": "123 ABC Street"
      },
      "items": [...],
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

## 👤 USER MANAGEMENT APIs

### 1. Đăng ký user mới
**POST** `/api/auth/register`
```json
{
  "fullName": "Nguyễn Văn A",
  "email": "user@example.com",
  "username": "username",
  "password": "password123"
}
```
**✅ Lưu vào data.json**: Thêm user mới vào `users` array

### 2. Đăng nhập user
**POST** `/api/auth/login`
```json
{
  "username": "username",
  "password": "password123"
}
```
**✅ Đọc từ data.json**: Tìm user trong `users` array

### 3. Lấy thông tin user
**GET** `/api/auth/user/:id`
**✅ Đọc từ data.json**: Lấy user theo ID từ `users` array

### 4. Cập nhật profile user
**PUT** `/api/auth/user/:id`
```json
{
  "fullName": "Nguyễn Văn A Updated",
  "email": "newemail@example.com",
  "phone": "0987654321",
  "address": "456 XYZ Street",
  "dateOfBirth": "1990-01-15",
  "gender": "male"
}
```
**✅ Cập nhật data.json**: Update user trong `users` array và lưu file

### 5. Cập nhật avatar
**PUT** `/api/auth/user/:id/avatar`
```json
{
  "avatarUrl": "https://example.com/new-avatar.jpg"
}
```
**✅ Cập nhật data.json**: Update avatarUrl của user

### 6. Đổi mật khẩu
**PUT** `/api/auth/user/:id/password`
```json
{
  "currentPassword": "oldpass",
  "newPassword": "newpass"
}
```
**✅ Cập nhật data.json**: Update password (hashed) của user

### 7. Kiểm tra username có tồn tại
**GET** `/api/auth/check-username/:username`
**✅ Đọc từ data.json**: Tìm username trong `users` array

### 8. Kiểm tra email có tồn tại
**GET** `/api/auth/check-email/:email`
**✅ Đọc từ data.json**: Tìm email trong `users` array

---

## 🛒 CART MANAGEMENT APIs

### 1. Thêm sản phẩm vào giỏ hàng
**POST** `/api/cart/add`
```json
{
  "userId": "user_123",
  "productId": "p1",
  "quantity": 2
}
```
**✅ Lưu vào data.json**: 
- Tìm cart của user trong `carts` array
- Nếu chưa có cart → tạo mới
- Thêm/cập nhật sản phẩm trong cart
- Lưu vào data.json

### 2. Xem giỏ hàng theo userId
**GET** `/api/cart/:userId`
**✅ Đọc từ data.json**: Lấy cart của user từ `carts` array

### 3. Cập nhật số lượng sản phẩm trong giỏ
**PATCH** `/api/cart/update`
```json
{
  "userId": "user_123",
  "productId": "p1",
  "quantity": 3
}
```
**✅ Cập nhật data.json**: Update quantity trong cart và lưu file

### 4. Xóa sản phẩm khỏi giỏ hàng
**DELETE** `/api/cart/remove`
```json
{
  "userId": "user_123",
  "productId": "p1"
}
```
**✅ Cập nhật data.json**: Xóa item khỏi cart và lưu file

### 5. Xóa toàn bộ giỏ hàng
**DELETE** `/api/cart/clear/:userId`
**✅ Cập nhật data.json**: Xóa toàn bộ cart của user khỏi `carts` array

### 6. Lấy số lượng sản phẩm trong giỏ
**GET** `/api/cart/:userId/count`
**✅ Đọc từ data.json**: Đếm items trong cart của user

---

## 📦 ORDER MANAGEMENT APIs

### 1. Tạo đơn hàng từ giỏ hàng (RECOMMENDED)
**POST** `/api/orders/from-cart`
```json
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
```
**✅ Lưu vào data.json**: 
- Lấy cart của user từ `carts` array
- Tạo order mới với items từ cart
- Thêm order vào `orders` array
- **Xóa cart của user khỏi `carts` array**
- Lưu data.json

### 2. Tạo đơn hàng thủ công
**POST** `/api/orders`
```json
{
  "userId": "user_123",
  "customerInfo": {...},
  "items": [
    {
      "productId": "p1",
      "name": "Samsung Galaxy S24",
      "price": 25000000,
      "quantity": 1,
      "image": "https://example.com/image.jpg"
    }
  ],
  "paymentMethod": "COD",
  "totalAmount": 25000000,
  "shippingAddress": "123 ABC Street"
}
```
**✅ Lưu vào data.json**: 
- Tạo order mới với items được cung cấp
- Thêm order vào `orders` array
- **Xóa cart của user (nếu có)**
- Lưu data.json

### 3. Lấy danh sách đơn hàng của user
**GET** `/api/orders/:userId?page=0&size=20`
**✅ Đọc từ data.json**: 
- Filter orders theo userId từ `orders` array
- Phân trang kết quả
- Sắp xếp theo thời gian (mới nhất trước)

### 4. Lấy chi tiết đơn hàng
**GET** `/api/orders/detail/:orderId`
**✅ Đọc từ data.json**: Tìm order theo ID từ `orders` array

### 5. Cập nhật trạng thái đơn hàng
**PATCH** `/api/orders/:orderId/status`
```json
{
  "status": "Đã thanh toán"
}
```
**✅ Cập nhật data.json**: 
- Tìm order theo ID trong `orders` array
- Update status và updatedAt
- Lưu data.json

### 6. Hủy đơn hàng
**DELETE** `/api/orders/:orderId`
```json
{
  "userId": "user_123"
}
```
**✅ Cập nhật data.json**: 
- Tìm order theo ID
- Kiểm tra quyền sở hữu (userId)
- Update status thành "Đã hủy"
- Lưu data.json

---

## 📱 PRODUCT APIs (Read Only for Users)

### 1. Lấy danh sách sản phẩm
**GET** `/api/products?page=0&size=20&q=search&brand=Samsung`
**✅ Đọc từ data.json**: Lấy products từ `products` array với filtering

### 2. Lấy chi tiết sản phẩm
**GET** `/api/products/:id`
**✅ Đọc từ data.json**: Tìm product theo ID từ `products` array

---

## 🔄 Complete User Workflow

### Workflow 1: User Registration & Profile Management
```
1. POST /api/auth/register → Lưu user mới vào data.json
2. POST /api/auth/login → Đọc user từ data.json
3. PUT /api/auth/user/:id → Cập nhật profile trong data.json
4. PUT /api/auth/user/:id/avatar → Cập nhật avatar trong data.json
```

### Workflow 2: Shopping Cart Management
```
1. POST /api/cart/add → Lưu item vào cart trong data.json
2. GET /api/cart/:userId → Đọc cart từ data.json
3. PATCH /api/cart/update → Cập nhật quantity trong data.json
4. DELETE /api/cart/remove → Xóa item khỏi cart trong data.json
```

### Workflow 3: Order Processing
```
1. POST /api/orders/from-cart → Tạo order + Xóa cart trong data.json
2. GET /api/orders/:userId → Đọc orders của user từ data.json
3. PATCH /api/orders/:orderId/status → Cập nhật status trong data.json
4. GET /api/orders/detail/:orderId → Đọc chi tiết order từ data.json
```

---

## 🛡️ Data Validation & Security

### User Data Validation
- Email uniqueness check
- Username uniqueness check
- Password hashing (MD5)
- Input sanitization (trim, lowercase email)

### Cart Data Validation
- Product existence check
- User existence check
- Quantity validation (positive numbers)

### Order Data Validation
- User existence check
- Cart not empty check (for from-cart API)
- Payment method validation
- Total amount calculation

---

## 📝 Key Features Summary

### ✅ User Management
- **Lưu**: Registration, profile updates, avatar updates
- **Đọc**: Login, get profile, check username/email
- **Cập nhật**: Profile fields, password, avatar
- **Xóa**: Deactivate user (soft delete)

### ✅ Cart Management  
- **Lưu**: Add products to cart
- **Đọc**: View cart, get cart count
- **Cập nhật**: Update quantities
- **Xóa**: Remove items, clear entire cart

### ✅ Order Management
- **Lưu**: Create orders (from cart or manual)
- **Đọc**: View order history, order details
- **Cập nhật**: Update order status
- **Xóa**: Cancel orders (soft delete - status change)

### ✅ Data Persistence
- Tất cả operations đều lưu vào `data.json`
- User-specific data isolation
- Automatic cart clearing after order creation
- Proper error handling và validation

---

## 🚀 Server Deployment

### Start Server
```bash
node phoneshop-server-updated.js
```

### Server Info
- **Port**: 8080
- **URL**: http://localhost:8080
- **Data File**: data.json (tự động tạo)
- **CORS**: Enabled for all origins

### Data File Location
```
/your-project-folder/data.json
```

---

## 🔧 Testing Commands

### User APIs
```bash
# Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Test User","email":"test@example.com","username":"testuser","password":"123456"}'

# Update profile
curl -X PUT http://localhost:8080/api/auth/user/user_123 \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Updated Name","phone":"0987654321"}'
```

### Cart APIs
```bash
# Add to cart
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{"userId":"user_123","productId":"p1","quantity":2}'

# Remove from cart
curl -X DELETE http://localhost:8080/api/cart/remove \
  -H "Content-Type: application/json" \
  -d '{"userId":"user_123","productId":"p1"}'
```

### Order APIs
```bash
# Create order from cart
curl -X POST http://localhost:8080/api/orders/from-cart \
  -H "Content-Type: application/json" \
  -d '{"userId":"user_123","customerInfo":{...},"paymentMethod":"COD"}'

# Update order status
curl -X PATCH http://localhost:8080/api/orders/order_123/status \
  -H "Content-Type: application/json" \
  -d '{"status":"Đã thanh toán"}'
```

---

**🎉 Server đã sẵn sàng xử lý tất cả operations với data.json persistence!**

**Key Points:**
- ✅ User có thể cập nhật profile → Lưu vào data.json
- ✅ User có thể thêm/xóa sản phẩm khỏi giỏ hàng → Lưu vào data.json  
- ✅ Khi user bấm "Xác nhận đặt hàng" → Tạo order với userId → Lưu vào data.json
- ✅ Tự động xóa giỏ hàng sau khi đặt hàng thành công
- ✅ Tất cả data được persist trong data.json với user isolation
