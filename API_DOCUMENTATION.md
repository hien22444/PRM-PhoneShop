# PhoneShop API Documentation

## 📋 Tổng quan
API server cho ứng dụng PhoneShop với đầy đủ tính năng quản lý sản phẩm, người dùng, giỏ hàng và xác thực.

**Base URL:** `http://localhost:8080`

---

## 🔐 Authentication APIs

### 1. Đăng ký người dùng
**POST** `/api/auth/register`

**Request Body:**
```json
{
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@example.com", 
  "username": "nguyenvana",
  "password": "123456"
}
```

**Response Success (201):**
```json
{
  "success": true,
  "message": "Đăng ký thành công",
  "user": {
    "id": "user_1699123456789_abc123def",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "username": "nguyenvana",
    "createdAt": "2024-11-05T10:30:56.789Z",
    "isActive": true
  }
}
```

**Response Error (400/409):**
```json
{
  "success": false,
  "message": "Tên đăng nhập đã tồn tại"
}
```

**Validation Rules:**
- `fullName`: Bắt buộc, không được trống
- `email`: Bắt buộc, định dạng email hợp lệ, unique
- `username`: Bắt buộc, unique, ít nhất 3 ký tự
- `password`: Bắt buộc, ít nhất 6 ký tự

---

### 2. Đăng nhập
**POST** `/api/auth/login`

**Request Body:**
```json
{
  "username": "nguyenvana",
  "password": "123456"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "user": {
    "id": "user_1699123456789_abc123def",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "username": "nguyenvana",
    "createdAt": "2024-11-05T10:30:56.789Z",
    "isActive": true
  }
}
```

**Response Error (401):**
```json
{
  "success": false,
  "message": "Tên đăng nhập hoặc mật khẩu không đúng"
}
```

---

### 3. Lấy thông tin người dùng
**GET** `/api/auth/user/:id`

**Response Success (200):**
```json
{
  "success": true,
  "user": {
    "id": "user_1699123456789_abc123def",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "username": "nguyenvana",
    "createdAt": "2024-11-05T10:30:56.789Z",
    "isActive": true
  }
}
```

---

### 4. Cập nhật thông tin người dùng
**PUT** `/api/auth/user/:id`

**Request Body:**
```json
{
  "fullName": "Nguyễn Văn A Updated",
  "email": "newemail@example.com"
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
    "createdAt": "2024-11-05T10:30:56.789Z",
    "isActive": true
  }
}
```

---

### 5. Đổi mật khẩu
**PUT** `/api/auth/user/:id/password`

**Request Body:**
```json
{
  "currentPassword": "123456",
  "newPassword": "newpassword123"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Đổi mật khẩu thành công"
}
```

---

### 6. Kiểm tra username có tồn tại
**GET** `/api/auth/check-username/:username`

**Response:**
```json
{
  "exists": false,
  "available": true
}
```

---

### 7. Kiểm tra email có tồn tại
**GET** `/api/auth/check-email/:email`

**Response:**
```json
{
  "exists": false,
  "available": true
}
```

---

## 🛍️ Product APIs (User)

### 1. Lấy danh sách sản phẩm
**GET** `/api/products`

**Query Parameters:**
- `q` (optional): Từ khóa tìm kiếm
- `brand` (optional): Lọc theo thương hiệu

**Examples:**
- `/api/products` - Lấy tất cả sản phẩm
- `/api/products?q=samsung` - Tìm kiếm "samsung"
- `/api/products?brand=Apple` - Lọc sản phẩm Apple
- `/api/products?q=phone&brand=Samsung` - Tìm "phone" trong sản phẩm Samsung

**Response:**
```json
{
  "content": [
    {
      "id": "p1",
      "name": "Samsung Galaxy S24",
      "brand": "Samsung",
      "price": 25000000,
      "stock": 10,
      "visible": true,
      "images": ["https://picsum.photos/seed/1/300/300"]
    }
  ],
  "page": 0,
  "size": 12,
  "totalPages": 1,
  "totalElements": 12
}
```

---

### 2. Lấy chi tiết sản phẩm
**GET** `/api/products/:id`

**Response:**
```json
{
  "id": "p1",
  "name": "Samsung Galaxy S24",
  "brand": "Samsung", 
  "price": 25000000,
  "stock": 10,
  "visible": true,
  "images": ["https://picsum.photos/seed/1/300/300"]
}
```

---

## 🛒 Cart APIs

### 1. Thêm sản phẩm vào giỏ hàng
**POST** `/api/cart/add`

**Request Body:**
```json
{
  "userId": "user_1699123456789_abc123def",
  "productId": "p1",
  "quantity": 2
}
```

**Response:**
```json
{
  "message": "Đã thêm sản phẩm vào giỏ hàng",
  "cart": {
    "userId": "user_1699123456789_abc123def",
    "items": [
      {
        "productId": "p1",
        "name": "Samsung Galaxy S24",
        "price": 25000000,
        "image": "https://picsum.photos/seed/1/300/300",
        "quantity": 2
      }
    ]
  }
}
```

---

### 2. Xem giỏ hàng
**GET** `/api/cart/:userId`

**Response:**
```json
{
  "userId": "user_1699123456789_abc123def",
  "items": [
    {
      "productId": "p1",
      "name": "Samsung Galaxy S24",
      "price": 25000000,
      "image": "https://picsum.photos/seed/1/300/300",
      "quantity": 2
    }
  ]
}
```

---

### 3. Cập nhật số lượng sản phẩm
**PATCH** `/api/cart/update`

**Request Body:**
```json
{
  "userId": "user_1699123456789_abc123def",
  "productId": "p1",
  "quantity": 3
}
```

**Response:**
```json
{
  "message": "Cập nhật số lượng thành công",
  "cart": {
    "userId": "user_1699123456789_abc123def",
    "items": [...]
  }
}
```

---

### 4. Xóa sản phẩm khỏi giỏ hàng
**DELETE** `/api/cart/remove`

**Request Body:**
```json
{
  "userId": "user_1699123456789_abc123def",
  "productId": "p1"
}
```

**Response:**
```json
{
  "message": "Đã xóa sản phẩm khỏi giỏ hàng",
  "cart": {
    "userId": "user_1699123456789_abc123def",
    "items": []
  }
}
```

---

## 🧑‍💻 Admin APIs

### 1. Lấy danh sách sản phẩm (Admin)
**GET** `/admin/products`

**Query Parameters:**
- `page` (default: 0): Trang hiện tại
- `size` (default: 20): Số lượng sản phẩm mỗi trang
- `q`: Từ khóa tìm kiếm
- `brand`: Lọc theo thương hiệu
- `sort`: Sắp xếp (`price,asc` hoặc `price,desc`)

**Example:** `/admin/products?page=0&size=10&q=samsung&sort=price,desc`

**Response:**
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalPages": 2,
  "totalElements": 15
}
```

---

### 2. Lấy chi tiết sản phẩm (Admin)
**GET** `/admin/products/:id`

---

### 3. Tạo sản phẩm mới
**POST** `/admin/products`

**Request Body:**
```json
{
  "name": "iPhone 15 Pro",
  "brand": "Apple",
  "price": 30000000,
  "stock": 5,
  "images": ["https://example.com/image1.jpg"]
}
```

---

### 4. Cập nhật sản phẩm
**PUT** `/admin/products/:id`

**Request Body:**
```json
{
  "name": "iPhone 15 Pro Max",
  "price": 35000000,
  "stock": 8,
  "visible": true
}
```

---

### 5. Cập nhật trạng thái hiển thị
**PATCH** `/admin/products/:id/visibility?visible=true`

---

### 6. Xóa sản phẩm
**DELETE** `/admin/products/:id`

---

### 7. Lấy danh sách người dùng (Admin)
**GET** `/admin/users`

**Query Parameters:**
- `page` (default: 0): Trang hiện tại
- `size` (default: 20): Số lượng user mỗi trang
- `q`: Từ khóa tìm kiếm (tên, email, username)

**Response:**
```json
{
  "content": [
    {
      "id": "user_1699123456789_abc123def",
      "fullName": "Nguyễn Văn A",
      "email": "nguyenvana@example.com",
      "username": "nguyenvana",
      "createdAt": "2024-11-05T10:30:56.789Z",
      "isActive": true
    }
  ],
  "page": 0,
  "size": 20,
  "totalPages": 1,
  "totalElements": 1
}
```

---

## 📁 Data Structure

### User Object
```json
{
  "id": "user_1699123456789_abc123def",
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "username": "nguyenvana",
  "password": "hashed_password", // MD5 hash (chỉ lưu trong DB)
  "createdAt": "2024-11-05T10:30:56.789Z",
  "isActive": true
}
```

### Product Object
```json
{
  "id": "p1",
  "name": "Samsung Galaxy S24",
  "brand": "Samsung",
  "price": 25000000,
  "stock": 10,
  "visible": true,
  "images": ["https://picsum.photos/seed/1/300/300"]
}
```

### Cart Object
```json
{
  "userId": "user_1699123456789_abc123def",
  "items": [
    {
      "productId": "p1",
      "name": "Samsung Galaxy S24",
      "price": 25000000,
      "image": "https://picsum.photos/seed/1/300/300",
      "quantity": 2
    }
  ]
}
```

---

## 🔧 Setup Instructions

### 1. Cài đặt dependencies
```bash
npm install express cors
```

### 2. Chạy server
```bash
node phoneshop-server-updated.js
```

### 3. Server sẽ chạy tại
```
http://localhost:8080
```

---

## 📝 Notes

### Security
- Password được hash bằng MD5 (demo only - nên dùng bcrypt trong production)
- Không có JWT token (có thể thêm sau)
- Validation cơ bản cho input

### Data Storage
- Dữ liệu lưu trong file `data.json`
- Cấu trúc: `{ products: [], carts: [], users: [] }`
- Auto-save sau mỗi thay đổi

### Error Handling
- HTTP status codes chuẩn
- Error messages bằng tiếng Việt
- Consistent response format

### Features
- ✅ User registration/login
- ✅ Product search & filtering  
- ✅ Shopping cart management
- ✅ Admin product management
- ✅ Admin user management
- ✅ Input validation
- ✅ Duplicate prevention

---

## 🚀 Integration với Android App

### 1. Cập nhật ApiService
Thêm các endpoint mới vào interface ApiService:

```java
@POST("api/auth/register")
Call<AuthResponse> register(@Body RegisterRequest request);

@POST("api/auth/login") 
Call<AuthResponse> login(@Body LoginRequest request);

@GET("api/auth/user/{id}")
Call<UserResponse> getUser(@Path("id") String userId);
```

### 2. Cập nhật AuthViewModel
Sử dụng API thay vì UserManager local:

```java
public void register(String fullName, String email, String username, String password) {
    RegisterRequest request = new RegisterRequest(fullName, email, username, password);
    apiService.register(request).enqueue(new Callback<AuthResponse>() {
        @Override
        public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
            // Handle response
        }
    });
}
```

### 3. Response Models
Tạo các model class tương ứng:
- `AuthResponse.java`
- `RegisterRequest.java`
- `LoginRequest.java`
- `UserResponse.java`

---

## 📞 Support

Nếu có vấn đề gì, vui lòng liên hệ qua:
- Email: support@phoneshop.com
- GitHub Issues: [Link repository]

---

**Last Updated:** November 2024  
**Version:** 1.0.0
