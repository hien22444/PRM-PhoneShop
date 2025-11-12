# PhoneShop Server Usage Guide

## 🚀 **File server.js đã được tạo**

File `server.js` đã được copy từ `phoneshop-server-updated.js` với đầy đủ tính năng.

## 📋 **Cách sử dụng**

### **1. Cài đặt dependencies**
```bash
npm install express cors
```

### **2. Start server**
```bash
node server.js
```

### **3. Expected output**
```
✅ Initialized clean data.json - NO MOCK DATA
🚀 PhoneShop API Server running at http://localhost:8080
📁 Data file: D:\FPT_Document\CN8\PRM\pro\PRM-PhoneShop\data.json
📊 Initial data stats:
   - Users: 0
   - Products: 4
   - Carts: 0
   - Orders: 0
🔗 Status endpoint: http://localhost:8080/api/status
```

## 🔧 **Tính năng chính**

### **✅ User Management**
- Đăng ký, đăng nhập user
- Cập nhật profile, avatar
- Kiểm tra username/email tồn tại

### **✅ Cart Management**
- Thêm/xóa sản phẩm vào giỏ hàng
- Cập nhật số lượng
- Xóa toàn bộ giỏ hàng
- User isolation (mỗi user có giỏ riêng)

### **✅ Order Management**
- Tạo đơn hàng từ giỏ hàng
- Xem lịch sử đơn hàng theo user
- Cập nhật trạng thái đơn hàng
- Hủy đơn hàng

### **✅ Product Management**
- Xem danh sách sản phẩm
- Tìm kiếm, lọc sản phẩm
- Admin CRUD operations

### **✅ Data Persistence**
- Tất cả data lưu vào `data.json`
- Real-time logging
- Automatic backup

## 🎯 **API Endpoints chính**

### **Authentication**
- `POST /api/auth/register` - Đăng ký
- `POST /api/auth/login` - Đăng nhập
- `PUT /api/auth/user/:id` - Cập nhật profile

### **Cart**
- `POST /api/cart/add` - Thêm vào giỏ
- `GET /api/cart/:userId` - Xem giỏ hàng
- `DELETE /api/cart/remove` - Xóa khỏi giỏ

### **Orders**
- `POST /api/orders/from-cart` - Tạo đơn hàng
- `GET /api/orders/:userId` - Lịch sử đơn hàng

### **Products**
- `GET /api/products` - Danh sách sản phẩm
- `GET /api/products/:id` - Chi tiết sản phẩm

### **Utility**
- `GET /api/status` - Kiểm tra server
- `POST /api/reset-database` - Reset database

## 🔍 **Test APIs**

### **1. Check server status**
```bash
curl http://localhost:8080/api/status
```

### **2. Register user**
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

### **3. Add to cart**
```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USER_ID_FROM_REGISTRATION",
    "productId": "p1",
    "quantity": 2
  }'
```

### **4. Create order**
```bash
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

## 📊 **Data Structure**

File `data.json` sẽ có cấu trúc:
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
      "fullName": "Test User",
      "email": "test@example.com",
      "username": "testuser",
      "password": "hashed_password",
      "createdAt": "2024-11-05T10:30:56.789Z",
      "isActive": true
    }
  ],
  "orders": [
    {
      "id": "order_456",
      "userId": "user_123",
      "customerInfo": {
        "fullName": "Test User",
        "phone": "0123456789",
        "email": "test@example.com",
        "address": "123 Test Street"
      },
      "items": [...],
      "paymentMethod": "COD",
      "totalAmount": 50000000,
      "status": "Đang xử lý",
      "createdAt": "2024-11-05T10:30:56.789Z"
    }
  ]
}
```

## 🔧 **Development Tools**

### **Reset database**
```bash
curl -X POST http://localhost:8080/api/reset-database
```

### **Create backup**
```bash
curl -X POST http://localhost:8080/api/backup
```

### **Alternative reset script**
```bash
node test-and-reset.js
```

## 🚨 **Troubleshooting**

### **Port already in use**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Change port in server.js if needed
const PORT = 8081;
```

### **CORS issues**
Server đã enable CORS cho tất cả origins:
```javascript
app.use(cors());
```

### **Data not saving**
Check server logs cho `💾 Data saved` messages.

## 📱 **Android Integration**

### **Base URL for Android**
```java
// For emulator
private static final String BASE_URL = "http://10.0.2.2:8080/";

// For real device (replace with your IP)
private static final String BASE_URL = "http://192.168.1.100:8080/";
```

### **Key APIs for Android**
- `POST /api/orders/from-cart` - Tạo đơn hàng
- `GET /api/orders/:userId` - Lịch sử đơn hàng
- `POST /api/cart/add` - Thêm vào giỏ
- `GET /api/cart/:userId` - Xem giỏ hàng

## ✅ **Server Features**

- ✅ **Real-time data persistence**
- ✅ **User isolation**
- ✅ **Comprehensive logging**
- ✅ **Error handling**
- ✅ **Input validation**
- ✅ **RESTful API design**
- ✅ **Development tools**

## 🎉 **Ready to Use!**

File `server.js` đã sẵn sàng để sử dụng với Android app.

**Start command:** `node server.js`  
**Server URL:** http://localhost:8080  
**Status check:** http://localhost:8080/api/status
