# Profile Service

Manages user onboarding and security.

## Overview

A Spring Boot microservice providing REST APIs for user profile management. It handles user registration (onboarding) and exposes secured endpoints for profile CRUD operations.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2** (Web, Data JPA, Security, Actuator, Validation)
- **H2** in-memory database (development)
- **Maven**

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+

### Run locally

```bash
mvn spring-boot:run
```

The service starts on `http://localhost:8080`.

### Run with Docker

```bash
docker build -t profile-service .
docker run -p 8080:8080 profile-service
```

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/api/profiles` | Required | List all profiles |
| `GET` | `/api/profiles/{id}` | Required | Get profile by ID |
| `POST` | `/api/profiles` | None | Create / register a new profile |
| `PUT` | `/api/profiles/{id}` | Required | Update an existing profile |
| `DELETE` | `/api/profiles/{id}` | Required | Delete a profile |
| `GET` | `/actuator/health` | None | Health check |
| `GET` | `/actuator/info` | None | Application info |

### Example: Create a profile

```bash
curl -X POST http://localhost:8080/api/profiles \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "email": "alice@example.com"}'
```

## Running Tests

```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/profile/
│   │   ├── ProfileApplication.java      # Entry point
│   │   ├── config/
│   │   │   ├── SecurityConfig.java      # Spring Security configuration
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── controller/
│   │   │   └── ProfileController.java   # REST endpoints
│   │   ├── model/
│   │   │   └── Profile.java             # JPA entity
│   │   ├── repository/
│   │   │   └── ProfileRepository.java   # Spring Data JPA repository
│   │   └── service/
│   │       └── ProfileService.java      # Business logic
│   └── resources/
│       └── application.yml              # Configuration
└── test/
    └── java/com/example/profile/
        ├── ProfileApplicationTests.java
        ├── controller/
        │   └── ProfileControllerTest.java
        └── service/
            └── ProfileServiceTest.java
```
