# Auth Service

The **Auth Service** is the centralized identity and access management component of the Orderly microservices architecture. It is responsible for securing the platform by handling user authentication, authorization, and session management.

## Responsibilities

*   **User Management**: Handles user registration and profile management.
*   **Authentication**: Validates credentials and manages login sessions.
*   **Token Generation**: Issues JWTs (JSON Web Tokens)) for secure, stateless service-to-service communication.
*   **Authorization**: Enforces role-based access control (RBAC) across the Orderly ecosystem.
*   **Security**: Implements security best practices including password hashing (BCrypt) and CORS policies.

## Key Features

*   **Register**: Create new user accounts (`/auth/register`).
*   **Login**: Authenticate users and issue JWT access tokens (`/auth/login`).
*   **Logout**: Securely terminate user sessions (`/auth/logout`).
*   **Stateless Security**: Uses JWT for scalable, distributed authentication without server-side session storage.


