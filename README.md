# Mindora AI — Backend

Spring Boot 3.3.4 · Java 21 · PostgreSQL · Spring Security + JWT · Gemini AI (RAG)

Backend cho ứng dụng đồng hành sức khỏe tâm thần **Mindora AI**, bao gồm chatbot Dora, nhật ký cảm xúc, phân tích nguy cơ tâm lý, kết nối chuyên gia và thư viện nội dung chữa lành.

---

## Yêu cầu

| Công cụ | Phiên bản tối thiểu |
|---|---|
| JDK | 21 |
| Maven | 3.9 |
| PostgreSQL | 14 |

---

## Cài đặt & Chạy

### 1. Tạo database

```sql
CREATE DATABASE mindora_db;
```

### 2. Cấu hình `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mindora_db
spring.datasource.username=postgres
spring.datasource.password=<mật_khẩu_của_bạn>

app.jwt.secret=<chuỗi_bí_mật_tối_thiểu_32_ký_tự>
app.cors.allowed-origins=http://localhost:5173,http://localhost:5174

# Gemini AI — lấy key miễn phí tại https://aistudio.google.com/apikey
# Nếu bỏ trống, chatbot Dora vẫn hoạt động với fallback response từ knowledge base
gemini.api.key=<api_key_của_bạn>
```

> **Lưu ý production:** Không commit credential vào Git. Dùng biến môi trường hoặc secrets manager.

### 3. Chạy ứng dụng

```bash
mvn spring-boot:run
```

hoặc build JAR:

```bash
mvn clean package
java -jar target/mindora-backend-1.0.0.jar
```

Server khởi động tại `http://localhost:8080`.

Khi khởi động lần đầu, Hibernate tự tạo toàn bộ bảng (`ddl-auto=update`), sau đó `data.sql` và `knowledge_data.sql` seed dữ liệu mẫu tự động (idempotent — chạy lại không bị trùng).

---

## Tài khoản mẫu (seed data)

Password chung: `123456`

| Email | Role | Ghi chú |
|---|---|---|
| `an@gmail.com` | USER | User thông thường |
| `binh@gmail.com` | USER | User thông thường |
| `cuong@gmail.com` | USER | User thông thường |
| `dung@expert.com` | USER + EXPERT | Chuyên gia đã verified |
| `em@expert.com` | USER + EXPERT | Chuyên gia đã verified |
| `admin@mindora.com` | USER + ADMIN | Quản trị viên |

---

## Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

Nhấn **Authorize**, nhập `Bearer <token>` để test các API cần xác thực.

---

## API Endpoints

### Auth (public)

| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/auth/register` | Đăng ký tài khoản (`role`: `USER` hoặc `EXPERT`) |
| POST | `/api/auth/login` | Đăng nhập, trả về JWT token |

**Response body mẫu:**
```json
{
  "token": "eyJ...",
  "user": { "id": "...", "fullName": "Nguyễn Văn An", "roles": ["USER"] },
  "conversationId": "..."
}
```

### User & Preferences (yêu cầu auth)

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/users/profile` | Thông tin user hiện tại |
| PUT | `/api/users/profile` | Cập nhật tên, avatar |
| PATCH | `/api/users/preferences` | Cập nhật preferences (partial update) |

### AI Chat — Dora (yêu cầu auth)

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/conversations` | Danh sách hội thoại chưa archive |
| POST | `/api/conversations` | Tạo hội thoại mới |
| GET | `/api/conversations/{id}/messages` | Lịch sử tin nhắn (phân trang) |
| POST | `/api/conversations/{id}/messages` | Gửi tin nhắn thô (không có AI reply) |
| **POST** | **`/api/conversations/{id}/chat`** | **Gửi tin nhắn → Dora tự động phản hồi (RAG pipeline)** |
| DELETE | `/api/conversations/{id}/messages` | Xóa toàn bộ tin nhắn trong hội thoại |

**Chat endpoint body:**
```json
{ "content": "Mình đang rất lo lắng về kỳ thi..." }
```

**Chat endpoint response:**
```json
{
  "userMessage": { "..." },
  "aiResponse": { "content": "Nghe có vẻ căng lắm đấy..." },
  "retrievedSources": ["PFA-WHO — Kỹ thuật thở 4-7-8"]
}
```

### Nhật ký cảm xúc (yêu cầu auth)

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/journals` | Danh sách nhật ký (phân trang, mới nhất trước) |
| GET | `/api/journals/{id}` | Chi tiết 1 nhật ký |
| POST | `/api/journals` | Tạo nhật ký (tự đồng bộ MoodLog nếu có `moodValue`) |
| PUT | `/api/journals/{id}` | Cập nhật nhật ký |
| DELETE | `/api/journals/{id}` | Xóa nhật ký |

`moodValue` nhận: `loved` · `happy` · `neutral` · `anxious` · `sad` · `angry`

### Tâm trạng (yêu cầu auth)

| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/moods` | Ghi nhận mood hôm nay (upsert theo ngày) |
| GET | `/api/moods/week` | Mood 7 ngày gần nhất |
| GET | `/api/moods/range?from=&to=` | Mood theo khoảng ngày |

`moodScore`: 1 (rất tệ) → 7 (rất vui)

### Phân tích sức khỏe tâm thần (yêu cầu auth)

| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/analysis/run` | Chạy phân tích dựa trên mood 7 ngày qua |
| GET | `/api/analysis/latest` | Kết quả phân tích gần nhất |
| GET | `/api/analysis/history` | Lịch sử phân tích |

Kết quả trả về `riskLevel`: `low` · `medium` · `high` · `critical`
Khi `critical`, hệ thống tự động tạo thông báo cảnh báo kèm số hotline `1800 599 920`.

### Chuyên gia (yêu cầu auth)

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/experts` | Danh sách chuyên gia đã verified (phân trang) |
| GET | `/api/experts/{id}` | Chi tiết 1 chuyên gia |

### Kết nối chuyên gia (yêu cầu auth)

| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/expert-connections` | User gửi yêu cầu kết nối |
| GET | `/api/expert-connections/my` | Danh sách yêu cầu của user hiện tại |
| GET | `/api/expert-connections/incoming` | Yêu cầu gửi tới chuyên gia *(ROLE_EXPERT)* |
| PUT | `/api/expert-connections/{id}/status` | Chuyên gia phản hồi *(ROLE_EXPERT)* |

`status` nhận: `pending` · `accepted` · `rejected` · `closed`

### Thư viện nội dung (yêu cầu auth)

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/content` | Danh sách nội dung (lọc `moodTag`, `contentType`) |
| GET | `/api/content/saved` | Nội dung đã bookmark |
| POST | `/api/content/{id}/save` | Bookmark nội dung |
| DELETE | `/api/content/{id}/save` | Bỏ bookmark |

`contentType`: `music` · `video` · `article` · `exercise`
`moodTag`: `calm` · `happy` · `sad` · `energy` · `sleep`

### Thông báo (yêu cầu auth)

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/notifications` | Danh sách thông báo |
| GET | `/api/notifications/unread` | Số thông báo chưa đọc (badge) |
| PATCH | `/api/notifications/{id}/read` | Đánh dấu đã đọc |
| PATCH | `/api/notifications/read-all` | Đánh dấu tất cả đã đọc |

---

## Kiến trúc tổng quan

```
Controller → Service (interface) → ServiceImpl → Repository → PostgreSQL
                                 ↘ GeminiService (Gemini 2.0 Flash API)
```

### Các tầng chính

```
com.mindora/
├── controller/       REST endpoints, @AuthenticationPrincipal
├── service/          Interface cho mỗi domain
│   └── impl/         Triển khai business logic, @Transactional
├── repository/       Spring Data JPA (interface)
├── entity/           JPA entities, kế thừa BaseEntity / AuditableEntity
├── dto/
│   ├── request/      Input validation (@Valid, @NotBlank...)
│   └── response/     Record types, trả về client
├── security/         JwtAuthFilter, JwtUtil, UserPrincipal
├── exception/        GlobalExceptionHandler, custom exceptions
└── config/           Security, CORS, JPA Auditing, Swagger
```

### RAG Pipeline (chatbot Dora)

```
User message
    │
    ├─ 1. RETRIEVE  → Tìm knowledge chunks theo emotion tag + keyword
    ├─ 2. AUGMENT   → Ghép context + lịch sử hội thoại vào system prompt
    ├─ 3. GENERATE  → Gọi Gemini 2.0 Flash API
    └─ 4. FALLBACK  → Template response nếu không có API key hoặc lỗi mạng
```

Khi phát hiện `crisis` (từ khóa tự tử/tự hại), luồng bypass Gemini, luôn trả về hotline cứu trợ.

### Entity hierarchy

```
BaseEntity (id: UUID, createdAt: Instant)
    └── AuditableEntity (+ updatedAt: Instant)
            ├── User
            ├── Expert
            ├── AiConversation
            ├── JournalEntry
            ├── ExpertConnection
            ├── ContentLibrary
            └── UserPreferences

BaseEntity (không có updatedAt)
    ├── MoodLog
    ├── Message
    ├── Notification
    ├── MentalHealthAnalysis
    └── KnowledgeDocument
```

---

## Bảo mật

- **JWT stateless** — không dùng session, mỗi request tự xác thực qua Bearer token.
- **BCrypt** cost factor 10 cho password.
- **IDOR protection** — mọi endpoint đọc/ghi dữ liệu đều kiểm tra `userId` từ token, không tin vào ID trong request body.
- **Crisis bypass** — phát hiện từ khóa khủng hoảng → trả về hotline ngay, không qua Gemini.

---

## Công nghệ sử dụng

| Thư viện | Phiên bản | Mục đích |
|---|---|---|
| Spring Boot | 3.3.4 | Framework chính |
| Spring Security | (từ parent) | JWT auth, phân quyền |
| Spring Data JPA | (từ parent) | ORM, repository |
| PostgreSQL | runtime | Database |
| Lombok | 1.18.36 | Giảm boilerplate |
| JJWT | 0.11.5 | Tạo và xác thực JWT |
| SpringDoc OpenAPI | 2.5.0 | Swagger UI |
