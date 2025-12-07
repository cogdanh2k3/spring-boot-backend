# Hướng dẫn Kết nối Frontend Admin với Backend

## ✅ Backend APIs đã hoàn thành

Backend đã implement đầy đủ các APIs cho Admin Panel:

### 1. Admin Dashboard API
- **GET** `/api/admin/dashboard/{username}` - Lấy thống kê tổng quan

### 2. Question Management APIs
- **POST** `/api/admin/questions/{username}/filter` - Lấy danh sách câu hỏi với filter
- **GET** `/api/admin/questions/{username}/{questionId}` - Lấy chi tiết câu hỏi
- **POST** `/api/admin/questions/{username}` - Tạo câu hỏi mới
- **PUT** `/api/admin/questions/{username}` - Cập nhật câu hỏi
- **DELETE** `/api/admin/questions/{username}/{questionId}` - Xóa câu hỏi
- **POST** `/api/admin/questions/{username}/bulk` - Import nhiều câu hỏi

### 3. Contest Management APIs
- **GET** `/api/admin/contests/{username}` - Lấy danh sách contests
- **GET** `/api/admin/contests/{username}/{contestId}` - Lấy chi tiết contest
- **POST** `/api/admin/contests/{username}` - Tạo contest mới
- **PUT** `/api/admin/contests/{username}` - Cập nhật contest
- **DELETE** `/api/admin/contests/{username}/{contestId}` - Xóa contest
- **GET** `/api/admin/contests/{username}/{contestId}/stats` - Lấy thống kê contest

## 📂 Cấu trúc Backend đã tạo

```
src/main/java/com/springboot/admin/
├── controller/
│   ├── AdminDashboardController.java ✅
│   ├── QuestionController.java ✅
│   └── ContestController.java ✅
├── entity/
│   ├── Question.java ✅
│   ├── QuestionChoice.java ✅
│   ├── Contest.java ✅
│   └── ContestQuestion.java ✅
├── repository/
│   ├── QuestionRepository.java ✅
│   ├── QuestionChoiceRepository.java ✅
│   ├── ContestRepository.java ✅
│   └── ContestQuestionRepository.java ✅
├── service/
│   ├── QuestionService.java ✅
│   └── ContestService.java ✅
└── dto/
    ├── QuestionDTO.java ✅
    ├── ContestDTO.java ✅
    ├── QuestionFilter.java ✅
    ├── QuestionCreateRequest.java ✅
    ├── QuestionUpdateRequest.java ✅
    ├── ContestCreateRequest.java ✅
    ├── ContestUpdateRequest.java ✅
    ├── BulkImportResult.java ✅
    └── ContestStats.java ✅
```

## 🚀 Các bước để chạy Backend

### 1. Khởi động Database (PostgreSQL)
```bash
# Nếu dùng Docker Compose
docker-compose up -d

# Hoặc đảm bảo PostgreSQL đang chạy trên localhost:5332
```

### 2. Chạy Spring Boot Application
```bash
# Sử dụng Maven wrapper
.\mvnw.cmd spring-boot:run

# Hoặc
mvn spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080`

### 3. Test APIs với request.http
Mở file `request.http` và test các endpoints:
```http
### Test Dashboard
GET http://localhost:8080/api/admin/dashboard/admin

### Test Create Question
POST http://localhost:8080/api/admin/questions/admin
Content-Type: application/json

{
  "questionText": "Thủ đô của Việt Nam là gì?",
  "choices": [
    {"choiceLabel": "A", "choiceText": "Hà Nội", "isCorrect": true},
    {"choiceLabel": "B", "choiceText": "TP.HCM", "isCorrect": false}
  ],
  "difficulty": "Easy",
  "category": "Địa lý",
  "points": 10,
  "timeLimit": 30
}
```

## 📱 Kết nối Frontend Android

### 1. Cấu hình BASE_URL trong ApiService.kt

**Cho Android Emulator:**
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"
```

**Cho thiết bị thật (dùng IP máy chủ):**
```kotlin
private const val BASE_URL = "http://192.168.1.XXX:8080/"
```

### 2. Tắt Mock Data trong AdminRepository.kt
```kotlin
companion object {
    private const val USE_MOCK_DATA = false  // Đổi thành false
}
```

### 3. Verify các endpoints trong ApiService.kt
Đảm bảo các endpoints đã được uncomment:
```kotlin
// Questions
@POST("api/admin/questions/{username}/filter")
suspend fun getQuestions(
    @Path("username") username: String,
    @Body filter: QuestionFilter
): Response<QuestionListResponse>

@POST("api/admin/questions/{username}")
suspend fun createQuestion(
    @Path("username") username: String,
    @Body request: QuestionCreateRequest
): Response<QuestionResponse>

// Contests
@GET("api/admin/contests/{username}")
suspend fun getContests(
    @Path("username") username: String
): Response<ContestListResponse>

@POST("api/admin/contests/{username}")
suspend fun createContest(
    @Path("username") username: String,
    @Body request: ContestCreateRequest
): Response<ContestResponse>
```

### 4. Build và Run Android App
```bash
./gradlew clean
./gradlew build
./gradlew installDebug
```

## 🔍 Kiểm tra kết nối

1. **Backend logs**: Kiểm tra console khi gọi API từ app
2. **Android Logcat**: Xem response từ API
3. **Network Inspector**: Debug HTTP requests

## 📊 Database Schema

Database sẽ tự động tạo tables khi khởi động (JPA auto-create):

### Questions Table
- `id` (VARCHAR) - Primary key
- `question_text` (TEXT) - Nội dung câu hỏi
- `difficulty` (VARCHAR) - Easy/Medium/Hard
- `category` (VARCHAR) - Phân loại
- `points` (INT) - Điểm số
- `time_limit` (INT) - Giới hạn thời gian (seconds)
- `created_at` (BIGINT) - Timestamp tạo

### Question_Choices Table
- `id` (BIGINT) - Auto increment primary key
- `question_id` (VARCHAR) - Foreign key to questions
- `choice_label` (VARCHAR) - A, B, C, D
- `choice_text` (TEXT) - Nội dung đáp án
- `is_correct` (BOOLEAN) - Đáp án đúng

### Contests Table
- `id` (VARCHAR) - Primary key
- `title` (VARCHAR) - Tên cuộc thi
- `description` (TEXT) - Mô tả
- `start_time` (BIGINT) - Timestamp bắt đầu
- `end_time` (BIGINT) - Timestamp kết thúc
- `duration` (INT) - Thời lượng (minutes)
- `total_questions` (INT) - Tổng số câu hỏi
- `status` (VARCHAR) - scheduled/live/ended
- `participant_count` (INT) - Số người tham gia
- `max_participants` (INT) - Số người tối đa
- `created_by` (VARCHAR) - Người tạo
- `created_at` (BIGINT) - Timestamp tạo

### Contest_Questions Table
- `id` (BIGINT) - Auto increment primary key
- `contest_id` (VARCHAR) - Foreign key to contests
- `question_id` (VARCHAR) - ID câu hỏi
- `question_order` (INT) - Thứ tự câu hỏi

## 🔐 Admin Access

Để truy cập admin APIs, user phải có role `ADMIN` trong database:

```sql
-- Cập nhật user thành admin
UPDATE users 
SET role = 'ADMIN' 
WHERE username = 'admin';
```

## ⚠️ Lưu ý quan trọng

1. **CORS**: Đã cấu hình `@CrossOrigin(origins = "*")` cho tất cả controllers
2. **Database**: Đảm bảo PostgreSQL đang chạy và cấu hình đúng trong `application.properties`
3. **Admin Role**: User phải có role ADMIN mới có quyền truy cập các APIs này
4. **Error Handling**: Backend trả về format:
   ```json
   {
     "success": true/false,
     "data": {...} hoặc "message": "error message"
   }
   ```

## 📞 Troubleshooting

### Backend không kết nối được database
```bash
# Kiểm tra database connection trong application.properties
spring.datasource.url=jdbc:postgresql://localhost:5332/wordsearch_db
spring.datasource.username=wordsearch_user
spring.datasource.password=wordsearch_password
```

### Android app không kết nối được backend
- Kiểm tra firewall Windows
- Đảm bảo backend đang chạy
- Ping IP từ thiết bị Android
- Kiểm tra BASE_URL trong code

### APIs trả về 403 Forbidden
- User chưa có role ADMIN
- Cập nhật role trong database

## ✨ Features đã implement

- ✅ Admin authentication & authorization
- ✅ Question CRUD operations
- ✅ Contest CRUD operations
- ✅ Question filtering & search
- ✅ Bulk question import
- ✅ Contest statistics
- ✅ Dashboard overview
- ✅ Auto-update contest status based on time

## 🎯 Sẵn sàng kết nối!

Backend đã hoàn thiện và sẵn sàng để frontend kết nối. Tất cả APIs đã được test và hoạt động đúng.

Chúc bạn thành công! 🚀
