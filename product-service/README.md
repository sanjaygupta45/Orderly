# 📋 Product Service

The **Product Service** manages the product catalog within the Orderly platform. It provides CRUD operations with MongoDB for flexible document storage, supporting filtering and pagination.

## 📋 Responsibilities

- **Product Management** - Create, read, update product information
- **Catalog Queries** - Filtering by category with pagination support
- **Data Persistence** - Document storage using MongoDB
- **Validation** - Request validation for data integrity

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products/create` | Create new product |
| GET | `/api/products/get/all` | List products (paginated, filterable) |
| GET | `/api/products/get/{id}` | Get product by ID |

### Query Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `category` | String | Filter by category |
| `page` | Integer | Page number (0-indexed) |
| `size` | Integer | Page size |

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.2.5
- **Database**: MongoDB 7.0
- **ODM**: Spring Data MongoDB
- **Validation**: Jakarta Validation

## ⚙️ Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8084 | Service port |
| `spring.data.mongodb.uri` | - | MongoDB connection URI |

## 🚀 Running Locally

```bash
cd product-service
mvn spring-boot:run
```

Service available at: `http://localhost:8084`
