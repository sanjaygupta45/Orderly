# Order Service

The **Order Service** is the core component of the Orderly microservices ecosystem responsible for handling customer orders. It orchestrates the order lifecycle, from creation to processing, ensuring seamless integration with inventory and payment systems.

## Responsibilities

*   **Order Management**: Handles the creation, retrieval, and updating of customer orders.
*   **Inventory Orchestration**: Communicates with the Inventory Service to validate stock availability before order placement.
*   **Data Persistence**: securely stores order details and transaction history.
*   **Response handling**: Provides standardized API responses for successful and failed order operations.

## Key Features

*   **Place Order**: validates stock and creates a new order (`POST /api/order`).
*   **Order Validation**: Ensures products are in stock before processing transactions.
*   **Standardized Responses**: Returns unified `ApiResponse` structures for consistent client handling.
