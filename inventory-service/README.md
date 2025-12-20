# 📦 Inventory Service

The **Inventory Service** manages product stock levels and availability within the Orderly platform. It ensures data integrity by preventing overselling through transactional stock validation.

## 📋 Responsibilities

- **Stock Management** - Tracks product quantities by SKU
- **Availability Checks** - Validates sufficient stock before order processing
- **Inventory Updates** - Handles restocking and sales deductions
- **Data Integrity** - Transactional operations to prevent race conditions

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/inventory` | Check stock availability by SKU |
| GET | `/api/inventory/details` | Get full inventory details |
| POST | `/api/inventory/add` | Add stock (restocking) |
| POST | `/api/inventory/reduce` | Reduce stock (order fulfillment) |

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.2.5
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA
- **Validation**: Jakarta Validation

## ⚙️ Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8082 | Service port |
| `spring.datasource.url` | - | MySQL connection URL |

## 🚀 Running Locally

```bash
cd inventory-service
mvn spring-boot:run
```

Service available at: `http://localhost:8082`
