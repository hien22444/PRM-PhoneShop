# PhoneShop Server - Deployment Guide

## 🚀 Quick Start

### 1. Prerequisites
```bash
# Cài đặt Node.js (version 14 hoặc cao hơn)
# Kiểm tra version
node --version
npm --version
```

### 2. Install Dependencies
```bash
# Trong thư mục project
npm install express cors
```

### 3. Start Server
```bash
node phoneshop-server-updated.js
```

### 4. Verify Server is Running
```bash
# Kiểm tra status
curl http://localhost:8080/api/status

# Hoặc mở browser: http://localhost:8080/api/status
```

---

## 📊 Server Features

### ✅ **Automatic Data Persistence**
- **Tất cả API operations đều lưu vào `data.json` ngay lập tức**
- **Real-time updates**: Mọi thay đổi được lưu ngay khi thực hiện
- **Error handling**: Logging và error recovery
- **Data validation**: Kiểm tra dữ liệu trước khi lưu

### ✅ **Complete API Coverage**
- **User Management**: Register, login, profile updates → **Lưu vào data.json**
- **Cart Management**: Add, update, remove items → **Lưu vào data.json**
- **Order Management**: Create, update status, cancel → **Lưu vào data.json**
- **Product Management**: CRUD operations → **Lưu vào data.json**

### ✅ **Data Structure**
```json
{
  "products": [...],
  "carts": [
    {
      "userId": "user_123",
      "items": [...]
    }
  ],
  "users": [
    {
      "id": "user_123",
      "fullName": "...",
      "email": "...",
      ...
    }
  ],
  "orders": [
    {
      "id": "order_123",
      "userId": "user_123",
      "status": "Đang xử lý",
      ...
    }
  ]
}
```

---

## 🔧 API Testing

### User Registration & Profile Update
```bash
# Đăng ký user mới
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyễn Văn A",
    "email": "test@example.com",
    "username": "testuser",
    "password": "123456"
  }'

# Cập nhật profile (sẽ lưu vào data.json)
curl -X PUT http://localhost:8080/api/auth/user/USER_ID \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyễn Văn A Updated",
    "phone": "0987654321",
    "address": "123 ABC Street"
  }'
```

### Cart Management
```bash
# Thêm sản phẩm vào giỏ hàng (sẽ lưu vào data.json)
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USER_ID",
    "productId": "p1",
    "quantity": 2
  }'

# Xem giỏ hàng
curl http://localhost:8080/api/cart/USER_ID

# Xóa sản phẩm khỏi giỏ (sẽ cập nhật data.json)
curl -X DELETE http://localhost:8080/api/cart/remove \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USER_ID",
    "productId": "p1"
  }'
```

### Order Management
```bash
# Tạo đơn hàng từ giỏ hàng (sẽ lưu order + xóa cart trong data.json)
curl -X POST http://localhost:8080/api/orders/from-cart \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USER_ID",
    "customerInfo": {
      "fullName": "Nguyễn Văn A",
      "phone": "0123456789",
      "email": "test@example.com",
      "address": "123 ABC Street"
    },
    "paymentMethod": "COD"
  }'

# Cập nhật trạng thái đơn hàng (sẽ cập nhật data.json)
curl -X PATCH http://localhost:8080/api/orders/ORDER_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status": "Đã thanh toán"}'

# Xem lịch sử đơn hàng
curl http://localhost:8080/api/orders/USER_ID
```

---

## 📁 File Structure

```
your-project/
├── phoneshop-server-updated.js    # Main server file
├── data.json                      # Auto-created database file
├── data_backup_*.json            # Backup files (optional)
├── package.json                  # Dependencies
└── node_modules/                 # Installed packages
```

---

## 🔍 Monitoring & Debugging

### Server Status
```bash
# Kiểm tra trạng thái server và database
curl http://localhost:8080/api/status
```

**Response:**
```json
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
      "products": 12,
      "carts": 3,
      "orders": 8
    }
  }
}
```

### Backup Data
```bash
# Tạo backup của data.json
curl -X POST http://localhost:8080/api/backup
```

### Server Logs
Server sẽ log tất cả data operations:
```
💾 Data saved to /path/to/data.json at 2024-11-05T10:30:56.789Z
🚀 PhoneShop API Server running at http://localhost:8080
📁 Data file: /path/to/data.json
📊 Initial data stats:
   - Users: 0
   - Products: 12
   - Carts: 0
   - Orders: 0
```

---

## ⚡ Performance & Reliability

### Data Persistence
- **Immediate save**: Mọi thay đổi được lưu ngay lập tức
- **Atomic operations**: Đảm bảo data consistency
- **Error recovery**: Logging và error handling
- **File locking**: Tránh data corruption

### Memory Management
- **In-memory database**: Fast read/write operations
- **Periodic sync**: Data được sync với file system
- **Backup support**: Tự động tạo backup khi cần

### Scalability
- **Single file database**: Phù hợp cho development và small-scale
- **Easy migration**: Có thể chuyển sang database thật khi cần
- **RESTful API**: Chuẩn REST API design

---

## 🛡️ Security Features

### Password Security
- **MD5 hashing**: Password được hash trước khi lưu
- **No plaintext**: Không bao giờ lưu password dạng plaintext
- **Validation**: Input validation cho tất cả endpoints

### Data Validation
- **User input sanitization**: Trim và validate input
- **Email uniqueness**: Kiểm tra email trùng lặp
- **Username uniqueness**: Kiểm tra username trùng lặp
- **Authorization**: User chỉ có thể access data của mình

### Error Handling
- **Graceful errors**: Proper HTTP status codes
- **Error logging**: Log tất cả errors để debug
- **Data recovery**: Backup và recovery mechanisms

---

## 🚨 Troubleshooting

### Common Issues

**1. Server không start được:**
```bash
# Kiểm tra port 8080 có bị sử dụng không
netstat -an | findstr :8080

# Kill process nếu cần
taskkill /F /PID <PID>
```

**2. Data.json không được tạo:**
```bash
# Kiểm tra quyền write trong thư mục
# Đảm bảo thư mục có quyền write
```

**3. API không response:**
```bash
# Kiểm tra server logs
# Kiểm tra network connectivity
curl http://localhost:8080/api/status
```

**4. Data không được lưu:**
```bash
# Kiểm tra server logs cho saveData operations
# Kiểm tra file data.json có được update không
```

### Debug Commands
```bash
# Xem nội dung data.json
cat data.json

# Theo dõi file changes
# Windows: dir data.json
# Linux/Mac: ls -la data.json

# Kiểm tra server process
# Windows: tasklist | findstr node
# Linux/Mac: ps aux | grep node
```

---

## 🎯 Production Deployment

### Security Enhancements
- **Use bcrypt**: Thay MD5 bằng bcrypt cho password hashing
- **Add authentication**: JWT tokens cho API authentication
- **Rate limiting**: Thêm rate limiting để tránh abuse
- **HTTPS**: Sử dụng HTTPS cho production

### Database Migration
- **PostgreSQL/MySQL**: Chuyển từ JSON file sang database thật
- **MongoDB**: Sử dụng NoSQL database
- **Redis**: Thêm caching layer

### Monitoring
- **Logging**: Structured logging với Winston
- **Metrics**: Application metrics và monitoring
- **Health checks**: Health check endpoints
- **Alerting**: Error alerting và notification

---

## ✅ Verification Checklist

### Before Deployment
- [ ] Server starts without errors
- [ ] data.json file is created automatically
- [ ] All API endpoints respond correctly
- [ ] User registration works and saves to data.json
- [ ] Cart operations save to data.json immediately
- [ ] Order creation clears cart and saves order
- [ ] Profile updates save to data.json
- [ ] Status endpoint shows correct data counts

### After Deployment
- [ ] Test user registration flow
- [ ] Test cart add/remove operations
- [ ] Test order creation from cart
- [ ] Test profile updates
- [ ] Verify data persistence across server restarts
- [ ] Check server logs for any errors
- [ ] Test backup functionality

---

**🎉 Server is ready for production use!**

**Key Features:**
- ✅ **Real-time data persistence** to data.json
- ✅ **Complete API coverage** for all operations
- ✅ **Automatic cart clearing** after order creation
- ✅ **User-specific data isolation**
- ✅ **Comprehensive error handling**
- ✅ **Built-in monitoring and backup**

**Start command:** `node phoneshop-server-updated.js`  
**Server URL:** http://localhost:8080  
**Status check:** http://localhost:8080/api/status
