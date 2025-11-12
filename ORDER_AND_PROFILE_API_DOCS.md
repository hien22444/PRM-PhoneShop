# PhoneShop - Order Management & Profile Update API Documentation

## 📦 Order Management APIs

### 1. Tạo đơn hàng mới
**POST** `/api/orders`

**Request Body:**
```json
{
  "userId": "user_1699123456789_abc123def",
  "customerInfo": {
    "fullName": "Nguyễn Văn A",
    "phone": "0123456789",
    "email": "nguyenvana@example.com",
    "address": "123 Đường ABC, Quận 1, TP.HCM"
  },
  "items": [
    {
      "productId": "p1",
      "name": "Samsung Galaxy S24",
      "price": 25000000,
      "quantity": 1,
      "image": "https://example.com/image.jpg"
    }
  ],
  "paymentMethod": "COD", // "COD", "BANK_TRANSFER", "E_WALLET"
  "totalAmount": 25000000,
  "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM" // Optional, fallback to customerInfo.address
}
```

**Response Success (201):**
```json
{
  "success": true,
  "message": "Tạo đơn hàng thành công",
  "order": {
    "id": "order_1699123456789_xyz789abc",
    "userId": "user_1699123456789_abc123def",
    "customerInfo": {
      "fullName": "Nguyễn Văn A",
      "phone": "0123456789",
      "email": "nguyenvana@example.com",
      "address": "123 Đường ABC, Quận 1, TP.HCM"
    },
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
    "status": "Đang xử lý", // COD: "Đang xử lý", Others: "Chờ thanh toán"
    "createdAt": "2024-11-05T10:30:56.789Z",
    "updatedAt": "2024-11-05T10:30:56.789Z"
  }
}
```

**Response Error (400):**
```json
{
  "success": false,
  "message": "Thiếu thông tin đơn hàng"
}
```

---

### 2. Lấy danh sách đơn hàng của user
**GET** `/api/orders/:userId`

**Query Parameters:**
- `page` (optional, default: 0): Trang hiện tại
- `size` (optional, default: 20): Số lượng đơn hàng mỗi trang

**Example:** `/api/orders/user_1699123456789_abc123def?page=0&size=10`

**Response Success (200):**
```json
{
  "success": true,
  "orders": [
    {
      "id": "order_1699123456789_xyz789abc",
      "userId": "user_1699123456789_abc123def",
      "customerInfo": {
        "fullName": "Nguyễn Văn A",
        "phone": "0123456789",
        "email": "nguyenvana@example.com",
        "address": "123 Đường ABC, Quận 1, TP.HCM"
      },
      "items": [...],
      "paymentMethod": "COD",
      "totalAmount": 25000000,
      "status": "Đang xử lý",
      "createdAt": "2024-11-05T10:30:56.789Z",
      "updatedAt": "2024-11-05T10:30:56.789Z"
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "currentPage": 0
}
```

---

### 3. Lấy chi tiết đơn hàng
**GET** `/api/orders/detail/:orderId`

**Response Success (200):**
```json
{
  "success": true,
  "order": {
    "id": "order_1699123456789_xyz789abc",
    "userId": "user_1699123456789_abc123def",
    "customerInfo": {
      "fullName": "Nguyễn Văn A",
      "phone": "0123456789",
      "email": "nguyenvana@example.com",
      "address": "123 Đường ABC, Quận 1, TP.HCM"
    },
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
    "status": "Đang xử lý",
    "createdAt": "2024-11-05T10:30:56.789Z",
    "updatedAt": "2024-11-05T10:30:56.789Z"
  }
}
```

**Response Error (404):**
```json
{
  "success": false,
  "message": "Không tìm thấy đơn hàng"
}
```

---

### 4. Cập nhật trạng thái đơn hàng
**PATCH** `/api/orders/:orderId/status`

**Request Body:**
```json
{
  "status": "Đã thanh toán" // "Chờ thanh toán", "Đã thanh toán", "Đang xử lý", "Đang giao hàng", "Đã giao hàng", "Đã hủy"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Cập nhật trạng thái đơn hàng thành công",
  "order": {
    "id": "order_1699123456789_xyz789abc",
    "status": "Đã thanh toán",
    "updatedAt": "2024-11-05T11:00:00.000Z"
    // ... other fields
  }
}
```

---

### 5. Hủy đơn hàng
**DELETE** `/api/orders/:orderId`

**Request Body:**
```json
{
  "userId": "user_1699123456789_abc123def"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Hủy đơn hàng thành công"
}
```

**Response Error (400):**
```json
{
  "success": false,
  "message": "Không thể hủy đơn hàng đã được xử lý"
}
```

**Response Error (403):**
```json
{
  "success": false,
  "message": "Không có quyền hủy đơn hàng này"
}
```

---

## 👤 Enhanced Profile Management APIs

### 1. Cập nhật thông tin profile (Enhanced)
**PUT** `/api/auth/user/:id`

**Request Body:**
```json
{
  "fullName": "Nguyễn Văn A Updated",
  "email": "newemail@example.com",
  "phone": "0987654321",
  "address": "456 Đường XYZ, Quận 2, TP.HCM",
  "dateOfBirth": "1990-01-15",
  "gender": "male" // "male", "female", "other"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Cập nhật thông tin thành công",
  "user": {
    "id": "user_1699123456789_abc123def",
    "fullName": "Nguyễn Văn A Updated",
    "email": "newemail@example.com",
    "username": "nguyenvana",
    "phone": "0987654321",
    "address": "456 Đường XYZ, Quận 2, TP.HCM",
    "dateOfBirth": "1990-01-15",
    "gender": "male",
    "avatarUrl": "",
    "createdAt": "2024-11-05T10:30:56.789Z",
    "updatedAt": "2024-11-05T11:15:30.123Z",
    "isActive": true
  },
  "updatedFields": {
    "fullName": "Nguyễn Văn A Updated",
    "email": "newemail@example.com",
    "phone": "0987654321",
    "address": "456 Đường XYZ, Quận 2, TP.HCM",
    "dateOfBirth": "1990-01-15",
    "gender": "male"
  }
}
```

**Response Error (409):**
```json
{
  "success": false,
  "message": "Email đã được sử dụng bởi tài khoản khác"
}
```

---

### 2. Cập nhật avatar/profile picture
**PUT** `/api/auth/user/:id/avatar`

**Request Body:**
```json
{
  "avatarUrl": "https://example.com/avatars/user123.jpg"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Cập nhật avatar thành công",
  "user": {
    "id": "user_1699123456789_abc123def",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "username": "nguyenvana",
    "phone": "0987654321",
    "address": "456 Đường XYZ, Quận 2, TP.HCM",
    "dateOfBirth": "1990-01-15",
    "gender": "male",
    "avatarUrl": "https://example.com/avatars/user123.jpg",
    "createdAt": "2024-11-05T10:30:56.789Z",
    "updatedAt": "2024-11-05T11:20:15.456Z",
    "isActive": true
  }
}
```

---

## 📊 Data Structure Updates

### Enhanced User Object
```json
{
  "id": "user_1699123456789_abc123def",
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "username": "nguyenvana",
  "password": "hashed_password", // MD5 hash (chỉ lưu trong DB)
  "phone": "0987654321",
  "address": "456 Đường XYZ, Quận 2, TP.HCM",
  "dateOfBirth": "1990-01-15",
  "gender": "male",
  "avatarUrl": "https://example.com/avatars/user123.jpg",
  "createdAt": "2024-11-05T10:30:56.789Z",
  "updatedAt": "2024-11-05T11:20:15.456Z",
  "isActive": true
}
```

### Order Object
```json
{
  "id": "order_1699123456789_xyz789abc",
  "userId": "user_1699123456789_abc123def",
  "customerInfo": {
    "fullName": "Nguyễn Văn A",
    "phone": "0123456789",
    "email": "nguyenvana@example.com",
    "address": "123 Đường ABC, Quận 1, TP.HCM"
  },
  "items": [
    {
      "productId": "p1",
      "name": "Samsung Galaxy S24",
      "price": 25000000,
      "quantity": 1,
      "image": "https://example.com/image.jpg"
    }
  ],
  "paymentMethod": "COD", // "COD", "BANK_TRANSFER", "E_WALLET"
  "totalAmount": 25000000,
  "status": "Đang xử lý", // "Chờ thanh toán", "Đã thanh toán", "Đang xử lý", "Đang giao hàng", "Đã giao hàng", "Đã hủy"
  "createdAt": "2024-11-05T10:30:56.789Z",
  "updatedAt": "2024-11-05T10:30:56.789Z"
}
```

### Updated data.json Structure
```json
{
  "products": [...],
  "carts": [...],
  "users": [...],
  "orders": [...]
}
```

---

## 🔄 Integration Flow

### Order Creation Flow
1. **Checkout Screen**: User fills shipping info and selects payment method
2. **API Call**: `POST /api/orders` with order data
3. **Server Processing**: 
   - Validates user and order data
   - Creates order with appropriate status
   - Saves to data.json
4. **Response**: Returns order details
5. **Payment Processing**: Update status via `PATCH /api/orders/:orderId/status`

### Profile Update Flow
1. **Profile Screen**: User edits profile information
2. **API Call**: `PUT /api/auth/user/:id` with updated fields
3. **Server Processing**:
   - Validates data (email uniqueness, etc.)
   - Updates user record
   - Saves to data.json
4. **Response**: Returns updated user data
5. **UI Update**: Refresh profile display

---

## 🛡️ Security & Validation

### Order Validation
- User must exist and be active
- Order must have at least one item
- Total amount must be positive
- Payment method must be valid

### Profile Update Validation
- Email uniqueness check (excluding current user)
- Field sanitization (trim whitespace)
- Optional field handling (empty strings allowed)
- Update timestamp tracking

### Authorization
- Users can only access their own orders
- Users can only update their own profiles
- Order cancellation restricted by status

---

## 📱 Android Integration Examples

### Create Order (Checkout)
```java
// In CheckoutFragment
OrderRequest orderRequest = new OrderRequest(
    userId,
    customerInfo,
    cartItems,
    paymentMethod,
    totalAmount,
    shippingAddress
);

apiService.createOrder(orderRequest).enqueue(new Callback<OrderResponse>() {
    @Override
    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
        if (response.isSuccessful() && response.body().isSuccess()) {
            // Order created successfully
            String orderId = response.body().getOrder().getId();
            // Navigate to payment or order confirmation
        }
    }
});
```

### Update Profile
```java
// In ProfileFragment
AuthRequest updateRequest = AuthRequest.forProfileUpdate(fullName, email);
updateRequest.setPhone(phone);
updateRequest.setAddress(address);
updateRequest.setDateOfBirth(dateOfBirth);
updateRequest.setGender(gender);

apiService.updateUser(userId, updateRequest).enqueue(new Callback<AuthResponse>() {
    @Override
    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
        if (response.isSuccessful() && response.body().isSuccess()) {
            // Profile updated successfully
            User updatedUser = response.body().getUser();
            // Update UI with new data
        }
    }
});
```

### Get Order History
```java
// In OrderHistoryFragment
apiService.getUserOrders(userId, page, size).enqueue(new Callback<OrderListResponse>() {
    @Override
    public void onResponse(Call<OrderListResponse> call, Response<OrderListResponse> response) {
        if (response.isSuccessful() && response.body().isSuccess()) {
            List<Order> orders = response.body().getOrders();
            // Display orders in RecyclerView
        }
    }
});
```

---

## 🚀 Testing

### Order APIs Testing
```bash
# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user_123",
    "customerInfo": {...},
    "items": [...],
    "paymentMethod": "COD",
    "totalAmount": 25000000
  }'

# Get user orders
curl -X GET "http://localhost:8080/api/orders/user_123?page=0&size=10"

# Update order status
curl -X PATCH http://localhost:8080/api/orders/order_123/status \
  -H "Content-Type: application/json" \
  -d '{"status": "Đã thanh toán"}'
```

### Profile APIs Testing
```bash
# Update profile
curl -X PUT http://localhost:8080/api/auth/user/user_123 \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Updated Name",
    "phone": "0987654321",
    "address": "New Address"
  }'

# Update avatar
curl -X PUT http://localhost:8080/api/auth/user/user_123/avatar \
  -H "Content-Type: application/json" \
  -d '{"avatarUrl": "https://example.com/avatar.jpg"}'
```

---

**Last Updated:** November 2024  
**Version:** 2.0.0
