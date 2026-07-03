# Mindora AI — Backend

Spring Boot 3.3.4 · Java 17 · PostgreSQL · Spring Security + JWT. Backend cho ứng dụng đồng hành sức khỏe tâm thần Mindora AI.

## Yêu cầu
- JDK 17+
- Maven 3.9+ (hoặc dùng `./mvnw` nếu bạn thêm wrapper)
- PostgreSQL 14+ đang chạy

## Cấu hình
Sửa `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mindora_db
spring.datasource.username=postgres
spring.datasource.password=your_password
app.jwt.secret=<chuỗi bí mật tối thiểu 32 ký tự>
app.cors.allowed-origins=http://localhost:5173,http://localhost:5174
```

Tạo database trước khi chạy:

```sql
CREATE DATABASE mindora_db;
```

Bảng sẽ được Hibernate tự tạo (`ddl-auto=update`). Ba role (`USER`, `EXPERT`, `ADMIN`)
được seed tự động qua `data.sql` khi khởi động (idempotent — chạy lại không bị trùng).

## Chạy

```bash
mvn spring-boot:run
# hoặc
mvn clean package && java -jar target/mindora-backend-1.0.0.jar
```

Server chạy ở `http://localhost:8080`.

## Xác thực
- `POST /api/auth/register` và `POST /api/auth/login` trả về `{ token, user, conversationId }`.
- Các API khác cần header: `Authorization: Bearer <token>`.

## Nhóm endpoint chính
| Prefix | Mô tả |
|---|---|
| `/api/auth/**` | Đăng ký / đăng nhập (public) |
| `/api/users/me` | Thông tin & preferences của user |
| `/api/conversations/**` | Chat AI: hội thoại + tin nhắn |
| `/api/journals/**` | Nhật ký cảm xúc (CRUD) |
| `/api/moods/**` | Ghi nhận & truy vấn tâm trạng |
| `/api/contents/**` | Thư viện nội dung + lưu nội dung |
| `/api/notifications/**` | Thông báo |
| `/api/analysis/**` | Phân tích nguy cơ sức khỏe tâm thần |
| `/api/experts/**` | Danh sách chuyên gia |
| `/api/expert-connections/**` | Yêu cầu kết nối; `PATCH /{id}/status` chỉ cho ROLE_EXPERT |

## Kiến trúc tầng service
- `com.mindora.service` chứa **interface** (`XxxService`).
- `com.mindora.service.impl` chứa **class triển khai** (`XxxServiceImpl` có `@Service`, `implements XxxService`).
- Controller và các service khác inject theo **kiểu interface**; Spring tự tiêm bean impl.
- Repository **không** tách interface/impl: trong Spring Data JPA, repository vốn là interface đơn (`extends JpaRepository`) và Spring sinh class triển khai lúc runtime.

## Những điểm đã sửa so với spec gốc
1. **Lombok** cho toàn bộ entity (`@Getter/@Setter/@NoArgsConstructor`) — spec gốc thiếu getter/setter.
2. Bỏ `new User(userId)`; dùng `userRepository.getReferenceById(userId)` (đúng chuẩn JPA, tránh transient entity).
3. **Mảng Postgres** dùng kiểu native Hibernate 6 (`@JdbcTypeCode(SqlTypes.ARRAY)` + `text[]`) thay cho thư viện `hibernate-types-60` (đã bỏ dependency).
4. **CORS** cấu hình qua `CorsConfigurationSource` và gắn vào Spring Security (`.cors(...)`) — xử lý đúng preflight trên endpoint cần auth.
5. Enum `MessageRole` chuẩn hoá `USER/AI`; service dùng `toUpperCase()` khi parse và trả về lowercase cho client.
6. Thứ tự matcher bảo mật: `/api/expert-connections/*/status` (EXPERT) đặt trước `/api/experts/**`.
7. Bổ sung các phần spec mới liệt kê tên: toàn bộ controllers, DTOs, `UserRoleRepository`, `UserPreferencesRepository`, `UserSavedContentRepository`, `MusicRecommendationRepository`, `JpaConfig`, `UserPrincipal`.

## Lưu ý
- Phân tích cảm xúc/nguy cơ hiện ở dạng **rule-based** (từ khóa tiếng Việt). Interface giữ nguyên nên có thể thay bằng model NLP thật mà không đổi controller.
- Project chưa được build/compile-verify trong môi trường tạo file (Maven Central không nằm trong domain được phép tải). Hãy chạy `mvn clean package` ở máy bạn; nếu thiếu dependency nào, kiểm tra kết nối mạng tới Maven Central.
