# Order Service

The **Order Service** handles order placement and management within the Orderly microservices ecosystem. It validates inventory availability before processing orders.

## Responsibilities

*   **Order Placement**: Accepts and validates order requests from clients.
*   **Inventory Validation**: Communicates with Inventory Service to verify stock availability.
*   **Order Persistence**: Saves confirmed orders to the database.
*   **Resilience**: Implements circuit breakers and retries for fault tolerance.

## Key Features

*   **Place Order**: Submit new orders with SKU, price, and quantity (`POST /api/order`).
*   **Inventory Check**: Synchronous validation against Inventory Service before order confirmation.
*   **Circuit Breaker**: Graceful degradation when Inventory Service is unavailable.
*   **Retry Logic**: Automatic retries for transient failures.

