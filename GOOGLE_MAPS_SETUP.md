# Hướng dẫn cài đặt Google Maps API Key

## Bước 1: Tạo Google Maps API Key

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project hiện có
3. Bật Google Maps SDK for Android:
   - Vào **APIs & Services** > **Library**
   - Tìm kiếm "Maps SDK for Android"
   - Click **Enable**

4. Tạo API Key:
   - Vào **APIs & Services** > **Credentials**
   - Click **Create Credentials** > **API Key**
   - Copy API Key vừa tạo

## Bước 2: Thêm API Key vào project

Mở file `app/src/main/AndroidManifest.xml` và thay thế `YOUR_GOOGLE_MAPS_API_KEY_HERE` bằng API Key của bạn:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="AIzaSy..." />
```

## Bước 3: Giới hạn API Key (Khuyến nghị)

Để bảo mật, nên giới hạn API Key:

1. Trong Google Cloud Console, chọn API Key vừa tạo
2. Click **Edit API Key**
3. Trong **Application restrictions**, chọn **Android apps**
4. Click **Add an item**
5. Nhập:
   - Package name: `com.example.phoneshop`
   - SHA-1 certificate fingerprint (lấy từ debug keystore)

### Lấy SHA-1 fingerprint:

Chạy lệnh sau trong terminal:

**Windows:**
```bash
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

**Mac/Linux:**
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Copy giá trị SHA1 và paste vào Google Cloud Console.

## Lưu ý

- API Key miễn phí có giới hạn sử dụng
- Không commit API Key lên Git (nên dùng local.properties)
- Để test, có thể dùng API Key không giới hạn, nhưng nhớ giới hạn khi deploy production
