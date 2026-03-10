# Hướng dẫn Test API Backend (Không cần UI)

## 🚀 Cách lấy JWT Token để test

Sử dụng endpoint **dev-login** (chỉ dùng cho development):

### Bước 1: Lấy JWT Token

```bash
POST http://localhost:8080/api/auth/dev-login
Content-Type: application/json

{
  "email": "alice@test.com"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "uuid-here",
    "email": "alice@test.com",
    "fullName": "Alice Nguyen",
    "avatarUrl": "https://ui-avatars.com/api/?name=Alice+Nguyen"
  }
}
```

### Bước 2: Sử dụng Token để gọi API

Copy token từ response, sau đó gọi các protected endpoints:

#### Gửi lời mời kết nối

```bash
POST http://localhost:8080/api/connections/request
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "addresseeId": "uuid-của-user-khác"
}
```

#### Lấy tất cả connections của mình

```bash
GET http://localhost:8080/api/connections/me
Authorization: Bearer <your-jwt-token>
```

## 👥 Test Users có sẵn

Khi khởi động app lần đầu, hệ thống tự động tạo 5 test users:

1. **alice@test.com** - Alice Nguyen (Software Engineer)
2. **bob@test.com** - Bob Tran (Product Manager)
3. **charlie@test.com** - Charlie Le (UI/UX Designer)
4. **diana@test.com** - Diana Pham (Data Analyst)
5. **eve@test.com** - Eve Hoang (Marketing Specialist)

## 📝 Ví dụ Test Flow

### 1. Alice login

```bash
POST /api/auth/dev-login
{ "email": "alice@test.com" }
→ Lưu token_alice
```

### 2. Bob login

```bash
POST /api/auth/dev-login
{ "email": "bob@test.com" }
→ Lưu token_bob và user_id_bob
```

### 3. Alice gửi lời mời kết nối cho Bob

```bash
POST /api/connections/request
Authorization: Bearer <token_alice>
{
  "addresseeId": "<user_id_bob>"
}
```

### 4. Alice xem connections của mình

```bash
GET /api/connections/me
Authorization: Bearer <token_alice>
```

## 🛠️ Tools để test

### Postman

1. Tạo request mới
2. Thêm header `Authorization: Bearer <token>`
3. Body chọn raw → JSON

### Thunder Client (VS Code Extension)

1. Install extension "Thunder Client"
2. New Request → chọn POST/GET
3. Headers: `Authorization: Bearer <token>`

### cURL

```bash
# Get token
curl -X POST http://localhost:8080/api/auth/dev-login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@test.com"}'

# Use token
curl -X GET http://localhost:8080/api/connections/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## ⚠️ Lưu ý

- Endpoint `/api/auth/dev-login` **CHỈ dùng để test**, không deploy lên production
- JWT token có thời hạn 24 giờ (xem `JwtProvider.java` để config)
- Database reset mỗi lần restart app (do `ddl-auto=create-drop`)
- Nếu muốn giữ data, đổi thành `ddl-auto=update` trong `application.properties`
