# 🛒 Order Service

The **Order Service** handles order placement and management within the Orderly platform. It validates inventory availability via synchronous communication and publishes order events for downstream processing.

## 📋 Responsibilities

- **Order Processing** - Accepts, validates, and persists customer orders
- **Inventory Validation** - Synchronous stock check via FeignClient before order confirmation
- **Event Publishing** - Publishes order events to Kafka for async processing
- **Resilience** - Implements circuit breaker, retry, and timeout patterns

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/order` | Place new order |
| GET | `/api/order/{id}` | Get order by ID |
| GET | `/api/order/user/{userId}` | Get orders by user |

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.2.5
- **Communication**: OpenFeign (sync), Apache Kafka (async)
- **Resilience**: Resilience4j (Circuit Breaker, Retry, Timeout)
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA

## ⚡ Resilience Patterns

| Pattern | Configuration |
|---------|---------------|
| Circuit Breaker | Failure threshold: 50%, Wait duration: 5s |
| Retry | Max attempts: 3, Exponential backoff |
| Timeout | Request timeout: 3s |

## ⚙️ Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8083 | Service port |
| `inventory-service.url` | - | Inventory service URL |

## 🚀 Running Locally

```bash
cd order-service
mvn spring-boot:run
```

Service available at: `http://localhost:8083`
