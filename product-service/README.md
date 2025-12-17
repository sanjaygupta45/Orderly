# Product Service

The **Product Service** manages the product catalog within the Orderly microservices ecosystem. It provides CRUD operations for products and supports filtering and pagination.

## Responsibilities

*   **Product Management**: Handles creation and retrieval of product information.
*   **Catalog Queries**: Supports filtering products by category with pagination.
*   **Data Persistence**: Stores product data using MongoDB.
*   **Validation**: Ensures product data integrity through request validation.

## Key Features

*   **Create Product**: Add new products to the catalog (`POST /api/products/create`).
*   **Get All Products**: Retrieve products with optional category filtering and pagination (`GET /api/products/get/all`).
*   **Get Product by ID**: Fetch a specific product by its ID (`GET /api/products/get/{productId}`).

## Tech Stack

*   Java 21
*   Spring Boot 3
*   Spring Data MongoDB
*   MongoDB
*   Lombok

## Running the Service

```bash
mvn spring-boot:run
```

