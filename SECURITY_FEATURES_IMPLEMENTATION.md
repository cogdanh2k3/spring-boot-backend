# Hướng Dẫn Cấu Hình Các Tính Năng Bảo Mật

## Tổng Quan

Dự án đã được bổ sung các tính năng bảo mật sau:

1. ✅ **Quên mật khẩu qua mã PIN** - Gửi PIN 6 số qua email
2. ✅ **Chống brute-force đăng nhập** - Giới hạn số lần thử đăng nhập sai
3. ✅ **Kiểm tra độ mạnh mật khẩu** - Yêu cầu: 8 ký tự, 1 in hoa, 1 số, 1 ký tự đặc biệt

## Cài Đặt

### 1. Cấu Hình Email (Gmail)

Để sử dụng chức năng quên mật khẩu, bạn cần cấu hình Gmail:

**Bước 1:** Tạo App Password trong Gmail
1. Đăng nhập vào Gmail
2. Vào Settings → Security
3. Bật "2-Step Verification"
4. Vào "App passwords"
5. Chọn "Mail" và "Other" (nhập tên app)
6. Gmail sẽ sinh ra mật khẩu 16 ký tự

**Bước 2:** Cập nhật `application.properties`

```properties
# Email configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=xxxx-xxxx-xxxx-xxxx  # App password từ Gmail
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### 2. Chạy Database Migration

Chạy file SQL để tạo các bảng mới:

```bash
psql -U wordsearch_user -d wordsearch_db -f database/security_tables.sql
```

Hoặc Spring JPA sẽ tự động tạo bảng khi khởi động (với `spring.jpa.hibernate.ddl-auto=update`).

### 3. Build và Run

```bash
mvn clean install
mvn spring-boot:run
```

## API Endpoints Mới

### 1. Forgot Password

**Request:**
```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "PIN sent to your email"
}
```

### 2. Verify PIN và Reset Password

**Request:**
```http
POST /api/auth/verify-pin
Content-Type: application/json

{
  "email": "user@example.com",
  "pin": "123456",
  "newPassword": "NewSecure@123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Password reset successfully"
}
```

### 3. Login (với brute-force protection)

**Request:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "john_doe",
  "password": "wrongpassword"
}
```

**Response (sau nhiều lần thất bại):**
```json
{
  "success": false,
  "message": "Too many failed attempts. Please try again in 30 minutes.",
  "user": null,
  "requiresCaptcha": true,
  "remainingAttempts": 0
}
```

## Yêu Cầu Mật Khẩu Mới

Khi đăng ký hoặc đổi mật khẩu, hệ thống yêu cầu:

- ✅ Tối thiểu 8 ký tự
- ✅ Ít nhất 1 chữ in hoa (A-Z)
- ✅ Ít nhất 1 số (0-9)
- ✅ Ít nhất 1 ký tự đặc biệt (!@#$%^&*()_+-=[]{}';:"|,.<>/?)

**Ví dụ mật khẩu hợp lệ:**
- `MyPass@123`
- `Secure#2024`
- `Test!Pass1`

## Brute-Force Protection

Hệ thống tự động:

- 🔒 Chặn IP/user sau **5 lần đăng nhập sai**
- ⏱️ Thời gian chặn: **30 phút**
- 🤖 Yêu cầu CAPTCHA sau **3 lần thất bại**
- 📊 Hiển thị số lần thử còn lại

## Testing

### Test Forgot Password

```bash
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
```

### Test Verify PIN

```bash
curl -X POST http://localhost:8080/api/auth/verify-pin \
  -H "Content-Type: application/json" \
  -d '{
    "email":"test@example.com",
    "pin":"123456",
    "newPassword":"NewSecure@123"
  }'
```

### Test Password Validation

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username":"testuser",
    "email":"test@example.com",
    "password":"weak",
    "fullName":"Test User"
  }'
```

**Expected Error:**
```json
{
  "success": false,
  "message": "Registration failed: Password requirements not met: Password must be at least 8 characters, Password must contain at least 1 uppercase letter, Password must contain at least 1 number, Password must contain at least 1 special character"
}
```

## Troubleshooting

### Email không gửi được

**Lỗi:** `Failed to send email: AuthenticationFailedException`

**Giải pháp:**
1. Kiểm tra App Password đã đúng chưa
2. Bật "Less secure app access" (nếu không dùng App Password)
3. Kiểm tra firewall/antivirus có chặn port 587 không

### Database errors

**Lỗi:** `Table 'password_reset_pins' doesn't exist`

**Giải pháp:**
1. Chạy file SQL: `database/security_tables.sql`
2. Hoặc đợi Spring Boot tự động tạo bảng (với ddl-auto=update)

### PIN hết hạn

PIN có hiệu lực trong **15 phút**. Sau thời gian này, user phải request PIN mới.

## Các File Đã Thêm/Sửa

### Entities
- `auth/entity/PasswordResetPin.java` - Entity cho PIN reset
- `auth/entity/LoginAttempt.java` - Entity cho tracking login attempts

### Repositories
- `auth/repository/PasswordResetPinRepository.java`
- `auth/repository/LoginAttemptRepository.java`

### Services
- `auth/service/PasswordResetService.java` - Xử lý quên mật khẩu
- `auth/service/LoginAttemptService.java` - Xử lý brute-force protection
- `auth/service/PasswordValidator.java` - Validate độ mạnh mật khẩu

### Controllers
- `auth/controller/AuthController.java` - Thêm endpoints mới

### Configuration
- `pom.xml` - Thêm spring-boot-starter-mail
- `application.properties` - Cấu hình email
- `database/security_tables.sql` - SQL cho bảng mới

## Lưu Ý Bảo Mật

⚠️ **QUAN TRỌNG:**

1. **Không commit email credentials vào Git**
   - Sử dụng environment variables
   - Hoặc file `.env` (thêm vào `.gitignore`)

2. **Production settings:**
   - Đổi `spring.jpa.show-sql=false`
   - Đổi `logging.level` về INFO
   - Sử dụng HTTPS cho API

3. **Email rate limiting:**
   - Giới hạn số lần gửi PIN trong 1 giờ
   - Tránh spam email

## Support

Nếu có vấn đề, vui lòng tạo issue hoặc liên hệ team.
