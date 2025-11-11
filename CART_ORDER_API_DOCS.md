# PhoneShop - Cart & Order Management API Documentation

## 🛒 Cart Management APIs (Updated)

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

**Response Success (200):**
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
        "image": "https://example.com/image.jpg",
        "quantity": 2
      }
    ]
  }
}
```

**Features:**
- ✅ Lưu vào `data.json` với `userId` tương ứng
- ✅ Tự động cộng dồn quantity nếu sản phẩm đã có trong giỏ
- ✅ Tạo giỏ hàng mới nếu user chưa có giỏ hàng

---

### 2. Xem giỏ hàng
**GET** `/api/cart/:userId`

**Response Success (200):**
```json
{
  "userId": "user_1699123456789_abc123def",
  "items": [
    {
      "productId": "p1",
      "name": "Samsung Galaxy S24",
      "price": 25000000,
      "image": "https://example.com/image.jpg",
      "quantity": 2
    },
    {
      "productId": "p2",
      "name": "iPhone 15 Pro",
      "price": 30000000,
      "image": "https://example.com/image2.jpg",
      "quantity": 1
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

**Response Success (200):**
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

**Response Success (200):**
```json
{
  "message": "Đã xóa sản phẩm khỏi giỏ hàng",
  "cart": {
    "userId": "user_1699123456789_abc123def",
    "items": [...]
  }
}
```

---

### 5. Xóa toàn bộ giỏ hàng (NEW)
**DELETE** `/api/cart/clear/:userId`

**Response Success (200):**
```json
{
  "success": true,
  "message": "Đã xóa toàn bộ giỏ hàng"
}
```

**Response Error (404):**
```json
{
  "success": false,
  "message": "Không tìm thấy giỏ hàng"
}
```

---

### 6. Lấy số lượng sản phẩm trong giỏ hàng (NEW)
**GET** `/api/cart/:userId/count`

**Response Success (200):**
```json
{
  "success": true,
  "totalItems": 5,      // Tổng số lượng sản phẩm (quantity)
  "itemCount": 3        // Số loại sản phẩm khác nhau
}
```

---

## 📦 Order Management APIs (Updated)

### 1. Tạo đơn hàng từ giỏ hàng (NEW - RECOMMENDED)
**POST** `/api/orders/from-cart`

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
  "paymentMethod": "COD",
  "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM"
}
```

**Response Success (201):**
```json
{
  "success": true,
  "message": "Tạo đơn hàng từ giỏ hàng thành công",
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
        "quantity": 2,
        "image": "https://example.com/image.jpg"
      }
    ],
    "paymentMethod": "COD",
    "totalAmount": 50000000,
    "status": "Đang xử lý",
    "createdAt": "2024-11-05T10:30:56.789Z",
    "updatedAt": "2024-11-05T10:30:56.789Z"
  }
}
```

**Features:**
- ✅ Tự động lấy sản phẩm từ giỏ hàng của user
- ✅ Tự động tính tổng tiền
- ✅ **Tự động xóa giỏ hàng sau khi tạo đơn hàng thành công**
- ✅ Lưu đơn hàng vào `data.json`

**Response Error (400):**
```json
{
  "success": false,
  "message": "Giỏ hàng trống"
}
```

---

### 2. Tạo đơn hàng thủ công (Original)
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
  "paymentMethod": "COD",
  "totalAmount": 25000000,
  "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM"
}
```

**Features:**
- ✅ **Tự động xóa giỏ hàng sau khi tạo đơn hàng thành công**
- ✅ Lưu đơn hàng vào `data.json`

---

### 3. Lấy danh sách đơn hàng của user
**GET** `/api/orders/:userId?page=0&size=20`

### 4. Lấy chi tiết đơn hàng
**GET** `/api/orders/detail/:orderId`

### 5. Cập nhật trạng thái đơn hàng
**PATCH** `/api/orders/:orderId/status`

### 6. Hủy đơn hàng
**DELETE** `/api/orders/:orderId`

*(Các API này giữ nguyên như đã document trước đó)*

---

## 🔄 Complete Workflow

### Workflow 1: Thêm sản phẩm và đặt hàng
```
1. POST /api/cart/add (Thêm sản phẩm vào giỏ)
   ↓ Lưu vào data.json với userId
   
2. GET /api/cart/:userId (Xem giỏ hàng)
   ↓ Hiển thị sản phẩm trong giỏ
   
3. POST /api/orders/from-cart (Đặt hàng từ giỏ)
   ↓ Tạo order + Xóa cart + Lưu vào data.json
   
4. GET /api/orders/:userId (Xem lịch sử đơn hàng)
```

### Workflow 2: Quản lý giỏ hàng
```
1. POST /api/cart/add (Thêm sản phẩm)
2. PATCH /api/cart/update (Cập nhật số lượng)
3. DELETE /api/cart/remove (Xóa sản phẩm)
4. GET /api/cart/:userId/count (Kiểm tra số lượng)
5. DELETE /api/cart/clear/:userId (Xóa toàn bộ giỏ)
```

---

## 📊 Data Structure trong data.json

### Cart Structure
```json
{
  "carts": [
    {
      "userId": "user_1699123456789_abc123def",
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
  ]
}
```

### Order Structure
```json
{
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
      "items": [
        {
          "productId": "p1",
          "name": "Samsung Galaxy S24",
          "price": 25000000,
          "quantity": 2,
          "image": "https://example.com/image.jpg"
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

## 🔧 Android Integration Examples

### 1. Thêm sản phẩm vào giỏ hàng
```java
// In ProductDetailFragment
public void addToCart(String productId, int quantity) {
    CartRequest request = new CartRequest(userId, productId, quantity);
    
    apiService.addToCart(request).enqueue(new Callback<CartResponse>() {
        @Override
        public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
            if (response.isSuccessful()) {
                showSuccess("Đã thêm vào giỏ hàng");
                updateCartBadge(); // Update cart count in UI
            }
        }
        
        @Override
        public void onFailure(Call<CartResponse> call, Throwable t) {
            showError("Lỗi thêm vào giỏ hàng");
        }
    });
}
```

### 2. Tạo đơn hàng từ giỏ hàng (RECOMMENDED)
```java
// In CheckoutFragment
public void createOrderFromCart() {
    OrderFromCartRequest request = new OrderFromCartRequest(
        userId, 
        customerInfo, 
        paymentMethod, 
        shippingAddress
    );
    
    apiService.createOrderFromCart(request).enqueue(new Callback<OrderResponse>() {
        @Override
        public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
            if (response.isSuccessful() && response.body().isSuccess()) {
                Order order = response.body().getOrder();
                
                // Giỏ hàng đã được tự động xóa
                // Navigate to success screen
                navigateToOrderSuccess(order.getId());
                
            } else {
                showError(response.body().getMessage());
            }
        }
        
        @Override
        public void onFailure(Call<OrderResponse> call, Throwable t) {
            showError("Lỗi tạo đơn hàng");
        }
    });
}
```

### 3. Cập nhật cart badge
```java
// In MainActivity or BaseActivity
public void updateCartBadge() {
    apiService.getCartCount(userId).enqueue(new Callback<CartCountResponse>() {
        @Override
        public void onResponse(Call<CartCountResponse> call, Response<CartCountResponse> response) {
            if (response.isSuccessful()) {
                int totalItems = response.body().getTotalItems();
                updateCartBadgeUI(totalItems);
            }
        }
        
        @Override
        public void onFailure(Call<CartCountResponse> call, Throwable t) {
            // Handle error silently for badge update
        }
    });
}
```

---

## 🚀 Required Android Models

### CartRequest.java
```java
public class CartRequest {
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("productId")
    private String productId;
    
    @SerializedName("quantity")
    private int quantity;
    
    // Constructor, getters, setters...
}
```

### OrderFromCartRequest.java
```java
public class OrderFromCartRequest {
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("customerInfo")
    private CustomerInfo customerInfo;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("shippingAddress")
    private String shippingAddress;
    
    // Constructor, getters, setters...
}
```

### CartCountResponse.java
```java
public class CartCountResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("totalItems")
    private int totalItems;
    
    @SerializedName("itemCount")
    private int itemCount;
    
    // Getters, setters...
}
```

---

## 📝 Updated ApiService Methods

Add these methods to your `ApiService.java`:

```java
// ========== CART ENDPOINTS ==========

@POST("api/cart/add")
Call<CartResponse> addToCart(@Body CartRequest request);

@GET("api/cart/{userId}")
Call<Cart> getCart(@Path("userId") String userId);

@PATCH("api/cart/update")
Call<CartResponse> updateCartItem(@Body CartUpdateRequest request);

@HTTP(method = "DELETE", path = "api/cart/remove", hasBody = true)
Call<CartResponse> removeFromCart(@Body CartRemoveRequest request);

@DELETE("api/cart/clear/{userId}")
Call<BaseResponse> clearCart(@Path("userId") String userId);

@GET("api/cart/{userId}/count")
Call<CartCountResponse> getCartCount(@Path("userId") String userId);

// ========== ORDER ENDPOINTS ==========

@POST("api/orders/from-cart")
Call<OrderResponse> createOrderFromCart(@Body OrderFromCartRequest request);

// ... other existing order endpoints
```

---

## ✅ Testing Checklist

### Cart Management
- [ ] Thêm sản phẩm vào giỏ hàng → Lưu vào data.json
- [ ] Cập nhật số lượng sản phẩm → Cập nhật data.json
- [ ] Xóa sản phẩm khỏi giỏ → Cập nhật data.json
- [ ] Xem giỏ hàng theo userId
- [ ] Lấy số lượng sản phẩm trong giỏ
- [ ] Xóa toàn bộ giỏ hàng

### Order Management
- [ ] Tạo đơn hàng từ giỏ hàng → Xóa giỏ hàng + Lưu order
- [ ] Tạo đơn hàng thủ công → Xóa giỏ hàng + Lưu order
- [ ] Kiểm tra data.json có cả orders và carts được cập nhật
- [ ] Xem lịch sử đơn hàng
- [ ] Cập nhật trạng thái đơn hàng

### Integration Testing
- [ ] End-to-end: Add to cart → Checkout → Order created → Cart cleared
- [ ] Multiple users có giỏ hàng riêng biệt
- [ ] Data persistence across server restarts

---

**🎉 Hoàn thành! Server đã sẵn sàng xử lý cart và order với data.json persistence.**

**Key Features:**
- ✅ Cart lưu theo userId vào data.json
- ✅ Order creation tự động xóa cart
- ✅ API tạo order từ cart (recommended)
- ✅ Cart count và management APIs
- ✅ Complete workflow support
