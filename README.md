# Inventory & Order Management REST API

A REST API built with **Java 17, Spring Boot 3, Spring Data JPA and MySQL** to manage products, customers and orders - with **automatic stock updates** when orders are placed or cancelled.

It mirrors the kind of inventory / sales / procurement flow found in ERP systems, built from scratch.

## Features
- CRUD for **products**, **customers**; place, view and cancel **orders**
- Stock **decreases automatically** when an order is placed; request fails with `409 Conflict` if stock is insufficient
- Cancelling an order **restores stock**
- Order lines store the **unit price at purchase time**
- Bean **validation** (`@NotBlank`, `@Email`, `@Min`, `@DecimalMin`) with clear field-level errors
- **Global exception handling** (`@RestControllerAdvice`) with consistent JSON error responses
- **Pagination, sorting and search** (`?page=0&size=10&sort=price,desc&name=pen`)
- **Optimistic locking** (`@Version`) on products to prevent two requests overselling the last unit
- **Swagger UI** for interactive API docs
- **Unit tests** (JUnit 5 + Mockito) for stock and order business logic

## Tech stack
Java 17 | Spring Boot 3.3 | Spring Web | Spring Data JPA (Hibernate) | MySQL | springdoc-openapi | JUnit 5 + Mockito | Maven

## Architecture
```
Controller  ->  Service (business rules, @Transactional)  ->  Repository (JPA)  ->  MySQL
   |                      |
  DTOs (records)    Custom exceptions -> GlobalExceptionHandler
```

## Database design
```
customers (1) ----< orders (1) ----< order_items >---- (1) products
```

## Getting started

**Prerequisites:** JDK 17+, Maven 3.9+, MySQL 8 running locally.

```bash
git clone <your-repo-url>
cd inventory-order-api

# optional: set DB credentials (defaults are root/root)
export DB_USERNAME=root
export DB_PASSWORD=your_password

mvn spring-boot:run
```
The database `inventory_db` is created automatically. Open **http://localhost:8080/swagger-ui.html**.

## Run the tests
```bash
mvn test
```
The unit tests use Mockito, so no database is needed.

## API overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products` | Create product |
| GET | `/api/products?name=&page=&size=&sort=` | List / search products (paginated) |
| GET | `/api/products/{id}` | Get product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |
| POST | `/api/customers` | Create customer |
| GET | `/api/customers` | List customers |
| GET | `/api/customers/{id}` | Get customer |
| POST | `/api/orders` | Place order (reduces stock) |
| GET | `/api/orders` | List orders |
| GET | `/api/orders/{id}` | Get order |
| POST | `/api/orders/{id}/cancel` | Cancel order (restores stock) |

## Example requests

```bash
# create a product
curl -X POST http://localhost:8080/api/products -H "Content-Type: application/json" \
  -d '{"sku":"PEN-1","name":"Gel Pen","price":25.50,"stockQuantity":100}'

# create a customer
curl -X POST http://localhost:8080/api/customers -H "Content-Type: application/json" \
  -d '{"name":"Asha Patil","email":"asha@example.com"}'

# place an order
curl -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":4}]}'
```

Insufficient stock response (`409`):
```json
{
  "timestamp": "2026-01-10T11:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Insufficient stock for 'Gel Pen'. Available: 2, requested: 4",
  "details": null
}
```

## What I would add next
- JWT authentication with role-based access (ADMIN / USER)
- Dockerfile + docker-compose with MySQL
- Integration tests with Testcontainers
- Low-stock alerts and a stock-movement history table
