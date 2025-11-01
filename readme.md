# Fitness Microservices Architecture

A comprehensive microservices-based fitness tracking application built with Spring Boot 3.5.5, Spring Cloud 2025.0.0, and Java 21.

## 🏗️ Architecture Overview

This project consists of 6 microservices:

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| **Eureka Server** | 8761 | - | Service discovery and registration |
| **Config Server** | 8888 | - | Centralized configuration management |
| **API Gateway** | 8080 | - | API Gateway with OAuth2 security |
| **User Service** | 8081 | PostgreSQL | User management and authentication |
| **Activity Service** | 8082 | MongoDB | Activity tracking and management |
| **AI Service** | 8083 | MongoDB | AI-powered recommendations using Gemini API |

## 📋 Prerequisites

Before starting, ensure you have the following installed:

- **Java 21** (JDK 21)
- **Maven 3.8+**
- **PostgreSQL 15+**
- **MongoDB 6.0+**
- **RabbitMQ 3.12+**
- **Keycloak** (for OAuth2 authentication)

## 🚀 Quick Start Guide

### 1. Database Setup

#### PostgreSQL (User Service)
```bash
# Create database
createdb fitness_user_db

# Or using psql
psql -U postgres
CREATE DATABASE fitness_user_db;
```

Default credentials (configured in `user-service.yml`):
- **Host**: localhost:5432
- **Database**: fitness_user_db
- **Username**: postgres
- **Password**: admin

#### MongoDB (Activity & AI Services)
```bash
# Start MongoDB
mongod --dbpath /path/to/data/db

# Or using Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

Set the MongoDB connection string as an environment variable:
```bash
export mongo_url="mongodb://localhost:27017/fitness_db"
```

### 2. Message Broker Setup

#### RabbitMQ
```bash
# Using Docker (recommended)
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management

# Access RabbitMQ Management Console
# http://localhost:15672
# Default credentials: guest/guest
```

### 3. OAuth2 Setup (Keycloak)

```bash
# Using Docker
docker run -d --name keycloak \
  -p 8181:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest start-dev
```

**Configure Keycloak:**
1. Access Keycloak at http://localhost:8181
2. Create a realm named `fitness-oatuh2`
3. Configure clients and users as needed
4. The JWK Set URI is configured in `api-gateway.yml`

### 4. Environment Variables

Set the following environment variables before starting the services:

```bash
# MongoDB connection
export mongo_url="mongodb://localhost:27017/fitness_db"

# Gemini API (for AI Service)
export GEMINI_API_URL="https://generativelanguage.googleapis.com/v1beta/models"
export GEMINI_API_KEY="your-gemini-api-key-here"
```

## 🔧 Building the Services

Build all services using Maven:

```bash
# Build all services
mvn clean install

# Or build individual services
cd eureka && mvn clean install
cd configserver && mvn clean install
cd gateway && mvn clean install
cd userservice && mvn clean install
cd activityservice && mvn clean install
cd aiservice && mvn clean install
```

## ▶️ Starting the Services

**IMPORTANT**: Services must be started in the following order to ensure proper initialization:

### Step 1: Start Eureka Server (Service Discovery)
```bash
cd eureka
mvn spring-boot:run
```
Wait until Eureka is fully started. Access at: http://localhost:8761

### Step 2: Start Config Server (Configuration Management)
```bash
cd configserver
mvn spring-boot:run
```
Wait until Config Server is ready. Access at: http://localhost:8888

### Step 3: Start API Gateway
```bash
cd gateway
mvn spring-boot:run
```
Gateway will be available at: http://localhost:8080

### Step 4: Start Business Services (Can be started in parallel)

**User Service:**
```bash
cd userservice
mvn spring-boot:run
```

**Activity Service:**
```bash
cd activityservice
mvn spring-boot:run
```

**AI Service:**
```bash
cd aiservice
mvn spring-boot:run
```

## 🐳 Docker Compose (Alternative)

For easier setup, you can create a `docker-compose.yml` file in the root directory:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: fitness_user_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: admin
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest

  keycloak:
    image: quay.io/keycloak/keycloak:latest
    command: start-dev
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "8181:8080"

volumes:
  postgres_data:
  mongo_data:
```

Start infrastructure:
```bash
docker-compose up -d
```

## 📡 API Endpoints

All requests go through the API Gateway at `http://localhost:8080`

### User Service
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User login
- `GET /api/users/{id}` - Get user details
- `PUT /api/users/{id}` - Update user

### Activity Service
- `POST /api/activity` - Create activity
- `GET /api/activity/{id}` - Get activity details
- `GET /api/activity/user/{userId}` - Get user activities
- `PUT /api/activity/{id}` - Update activity
- `DELETE /api/activity/{id}` - Delete activity

### AI Service
- `GET /api/recommendations/{userId}` - Get AI recommendations
- `POST /api/recommendations/analyze` - Analyze activity data

## 🔍 Monitoring & Health Checks

- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672
- **Keycloak Admin**: http://localhost:8181

Health check endpoints (if Spring Actuator is enabled):
- `http://localhost:8080/actuator/health` (Gateway)
- `http://localhost:8081/actuator/health` (User Service)
- `http://localhost:8082/actuator/health` (Activity Service)
- `http://localhost:8083/actuator/health` (AI Service)

## 🛠️ Configuration

All service configurations are centralized in the Config Server at:
```
configserver/src/main/resources/config/
├── api-gateway.yml
├── user-service.yml
├── activity-service.yml
└── ai-service.yml
```

## 🔐 Security

- API Gateway uses **OAuth2 Resource Server** with JWT tokens
- Keycloak provides authentication and authorization
- All requests must include a valid JWT token in the Authorization header:
  ```
  Authorization: Bearer <your-jwt-token>
  ```

## 🧪 Testing

Run tests for individual services:
```bash
cd <service-directory>
mvn test
```

## 📝 Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Cloud**: Spring Cloud 2025.0.0
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config
- **Message Broker**: RabbitMQ (AMQP)
- **Databases**: PostgreSQL, MongoDB
- **Security**: OAuth2, JWT
- **AI Integration**: Google Gemini API
- **Build Tool**: Maven
- **Java Version**: 21

## 🐛 Troubleshooting

### Service won't start
- Ensure all prerequisite services (Eureka, Config Server) are running
- Check that ports are not already in use
- Verify environment variables are set correctly

### Database connection errors
- Verify PostgreSQL/MongoDB is running
- Check credentials in configuration files
- Ensure databases are created

### RabbitMQ connection issues
- Verify RabbitMQ is running on port 5672
- Check credentials (default: guest/guest)
- Ensure exchange and queues are properly configured

### OAuth2 authentication failures
- Verify Keycloak is running on port 8181
- Check realm name is `fitness-oatuh2`
- Ensure JWK Set URI is accessible

## 📄 License

This project is for educational/demonstration purposes.

## 👥 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📧 Contact

For questions or support, please open an issue in the repository.
