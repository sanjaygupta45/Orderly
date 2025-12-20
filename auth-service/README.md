# 🔐 Auth Service

The **Auth Service** is the centralized identity and access management component of the Orderly platform, handling user authentication, authorization, and JWT token management.

## 📋 Responsibilities

- **User Management** - Registration, profile management, and credential storage
- **Authentication** - Credential validation and session management
- **Token Generation** - JWT issuance for stateless service-to-service communication
- **Authorization** - Role-based access control (RBAC) enforcement
- **Security** - Password hashing (BCrypt), CORS policies, and security headers

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user account |
| POST | `/api/auth/login` | Authenticate and receive JWT token |
| POST | `/api/auth/logout` | Terminate user session |

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.2.5
- **Security**: Spring Security, JWT (JJWT 0.12.3)
- **Database**: MySQL 8.0
- **Password Hashing**: BCrypt

## ⚙️ Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8081 | Service port |
| `jwt.secret` | - | JWT signing secret |
| `jwt.expiration` | 86400000 | Token validity (ms) |

## 🚀 Running Locally

```bash
cd auth-service
mvn spring-boot:run
```

Service available at: `http://localhost:8081`
