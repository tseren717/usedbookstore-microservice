# Used Bookstore - Microservice Architecture

Хуучин номын дэлгүүрийн систем - 3 бие даасан microservice-ээс бүрдэнэ.

## Архитектур

```
┌─────────────────┐  ┌──────────────────┐  ┌─────────────────┐
│  Book Service   │  │  Order Service   │  │  User Service   │
│   port: 8081    │  │   port: 8082     │  │   port: 8083    │
│                 │  │                  │  │                 │
│  H2: bookdb    │  │  H2: orderdb     │  │  H2: userdb     │
└─────────────────┘  └──────────────────┘  └─────────────────┘
```

### Database-per-Service Pattern
- Тус бүр өөрийн H2 database-тэй
- Бие даан deploy хийх боломжтой
- Нэг service унасан ч бусад нь ажиллана

## Ажиллуулах

### Бүгдийг compile хийх
```bash
cd usedbookstore-microservice
mvn clean compile
```

### Тус тусад нь ажиллуулах (3 terminal нээнэ)

**Terminal 1 - Book Service:**
```bash
cd book-service
mvn exec:java
```

**Terminal 2 - Order Service:**
```bash
cd order-service
mvn exec:java
```

**Terminal 3 - User Service:**
```bash
cd user-service
mvn exec:java
```

## API Endpoints

### Book Service (http://localhost:8081)
| Method | URL | Тайлбар |
|--------|-----|---------|
| GET | /api/books | Бэлэн номнуудын жагсаалт |
| GET | /api/books/{id} | Номын дэлгэрэнгүй |
| GET | /api/books?seller=username | Тухайн хэрэглэгчийн номнууд |
| POST | /api/books | Шинэ ном нэмэх |
| PUT | /api/books/{id} | Ном засах |
| PUT | /api/books/{id}?action=sold | Зарагдсан болгох |
| DELETE | /api/books/{id} | Ном устгах |

### Order Service (http://localhost:8082)
| Method | URL | Тайлбар |
|--------|-----|---------|
| GET | /api/orders | Бүх захиалга |
| GET | /api/orders/{id} | Захиалгын дэлгэрэнгүй |
| GET | /api/orders?buyer=username | Тухайн хэрэглэгчийн захиалга |
| POST | /api/orders | Шинэ захиалга үүсгэх |
| PUT | /api/orders/{id}?action=confirm | Захиалга батлах |
| PUT | /api/orders/{id}?action=cancel | Захиалга цуцлах |
| PUT | /api/orders/{id}?action=complete | Захиалга дуусгах |

### User Service (http://localhost:8083)
| Method | URL | Тайлбар |
|--------|-----|---------|
| POST | /api/users/login | Нэвтрэх |
| POST | /api/users/register | Бүртгүүлэх |
| GET | /api/users | Бүх хэрэглэгчид (admin) |
| GET | /api/users/{username} | Хэрэглэгчийн мэдээлэл |

## Жишээ хүсэлтүүд

### Бүртгүүлэх
```bash
curl -X POST http://localhost:8083/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"1234","confirmPassword":"1234"}'
```

### Нэвтрэх
```bash
curl -X POST http://localhost:8083/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### Ном нэмэх
```bash
curl -X POST http://localhost:8081/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"1984","author":"George Orwell","price":15000,"condition":"GOOD","category":"FICTION","sellerUsername":"user1"}'
```

### Захиалга үүсгэх
```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"buyerUsername":"user2","sellerUsername":"user1","price":15000}'
```

## Admin нэвтрэх
- Username: `admin`
- Password: `password123`
