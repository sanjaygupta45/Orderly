# Inventory Service

The **Inventory Service** manages product stock availability and inventory operations within the Orderly microservices ecosystem. It ensures that orders can only be placed for items that are physically available.

## Responsibilities

*   **Stock Management**: Tracks quantity of products by SKU.
*   **Availability Checks**: Validates if sufficient stock exists before order processing.
*   **Inventory Updates**: Handles increments (restocking) and decrements (sales) of inventory.
*   **Data Integrity**: Prevents overselling through transactional stock validation.

## Key Features

*   **Check Stock**: Verify availability for a specific SKU and quantity (`GET /api/inventory`).
*   **Get Details**: Retrieve full inventory status for a product (`GET /api/inventory/details`).
*   **Add Stock**: Increase inventory levels (`POST /api/inventory/add`).
*   **Reduce Stock**: Reserve or deduct items for orders (`POST /api/inventory/reduce`).


