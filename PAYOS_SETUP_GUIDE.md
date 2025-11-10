# Hướng dẫn cấu hình PayOS cho PhoneShop

## 1. Đăng ký tài khoản PayOS

1. Truy cập [PayOS Dashboard](https://my.payos.vn/)
2. Đăng ký tài khoản merchant
3. Hoàn thành xác thực KYC
4. Tạo ứng dụng mới trong dashboard

## 2. Lấy thông tin API Keys

Sau khi tạo ứng dụng, bạn sẽ nhận được:
- **Client ID**: ID của ứng dụng
- **API Key**: Key để gọi API PayOS
- **Checksum Key**: Key để tạo chữ ký bảo mật

## 3. Cấu hình trong ứng dụng

### Bước 1: Cập nhật PayOSConfig.java

Mở file `app/src/main/java/com/example/phoneshop/config/PayOSConfig.java` và thay thế các giá trị:

```java
public static final String CLIENT_ID = "your_actual_client_id";
public static final String API_KEY = "your_actual_api_key";
public static final String CHECKSUM_KEY = "your_actual_checksum_key";
```

### Bước 2: Cấu hình Return URLs trong PayOS Dashboard

Trong PayOS Dashboard, cấu hình các Return URLs:
- **Success URL**: `phoneshop://payment-success`
- **Cancel URL**: `phoneshop://payment-cancel`

### Bước 3: Cập nhật PayOSService.java (Tùy chọn)

Nếu bạn muốn sử dụng PayOS SDK thực tế thay vì simulation, hãy cập nhật method `simulatePayOSApiCall` trong `PayOSService.java`:

```java
private void callRealPayOSAPI(PaymentRequest request, MutableLiveData<String> paymentUrlLiveData) {
    // Implement real PayOS API call here
    // Sử dụng PayOS SDK hoặc Retrofit để gọi API
}
```

## 4. Test thanh toán

### Môi trường Sandbox
- Đặt `IS_SANDBOX = true` trong `PayOSConfig.java`
- Sử dụng thông tin test từ PayOS

### Môi trường Production
- Đặt `IS_SANDBOX = false` trong `PayOSConfig.java`
- Sử dụng thông tin thực tế từ PayOS

## 5. Luồng thanh toán

1. Người dùng chọn sản phẩm và thêm vào giỏ hàng
2. Trong màn hình checkout, chọn phương thức thanh toán online
3. Ứng dụng gọi PayOS API để tạo payment link
4. Người dùng được chuyển đến WebView để thanh toán
5. Sau khi thanh toán, PayOS redirect về app qua URL scheme
6. App xử lý kết quả và hiển thị màn hình thành công

## 6. Xử lý lỗi

- Kiểm tra kết nối internet
- Xác minh API keys đúng
- Đảm bảo Return URLs được cấu hình chính xác
- Kiểm tra logs trong PayOS Dashboard

## 7. Bảo mật

- **KHÔNG** commit API keys vào Git
- Sử dụng environment variables hoặc build configs
- Validate webhook signatures từ PayOS
- Implement proper error handling

## 8. Webhook (Tùy chọn)

Để nhận thông báo real-time về trạng thái thanh toán, cấu hình webhook URL trong PayOS Dashboard:
- Webhook URL: `https://your-server.com/webhook/payos`
- Implement endpoint để xử lý webhook notifications

## Liên hệ hỗ trợ

- PayOS Documentation: [https://payos.vn/docs](https://payos.vn/docs)
- PayOS Support: support@payos.vn
