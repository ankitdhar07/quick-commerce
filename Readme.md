# 🚀 QuickCommerce - Event-Driven E-Commerce Platform

A modern, production-ready **microservices e-commerce platform** built with the latest technologies.

## ✨ Technology Stack

- ☕ **Java 21** + **Spring Boot 3.2**
- 📨 **Apache Kafka** (event-driven)
- 🗄️ **Flyway** (database migrations)
- 📖 **OpenAPI 3.0** (API specification)
- 🅰️ **Angular 17** (modern frontend)
- 🐳 **Docker Compose** (one command startup)
- 📊 **PostgreSQL 16** (separate database per service)

## 🎯 Quick Start (5 Minutes)

### Prerequisites
- Docker & Docker Compose
- Java 21
- Maven 3.8+

### Start Everything

```bash
cd quickcommerce
docker-compose up -d

# Wait 1-2 minutes for services to start...

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### Access the System

- **API Gateway**: http://localhost:8000
- **Eureka Dashboard**: http://localhost:8761
- **API Documentation (Swagger)**: http://localhost:8000/swagger-ui.html
- **Kafka UI**: http://localhost:9080
- **PgAdmin**: http://localhost:5050 (admin@quickcommerce.com / admin123)

## 🏗️ System Architecture

```
┌──────────────────────────────────────────────────┐
│                QUICKCOMMERCE                     │
├──────────────────────────────────────────────────┤
│                                                  │
│  Frontend (Angular 17) → API Gateway (8000)      │
│                              ↓                   │
│     ┌─────────────────────────────────┐          │
│     ↓              ↓              ↓              │
│ Product Svc    Order Svc    Payment Svc          │
│  (8001)         (8002)        (8003)             │
│     ↓              ↓              ↓              │
│ PostgreSQL     PostgreSQL    PostgreSQL          │
│                              │                   │
│  ┌───────────────────────────┴──────────────┐    │
│  ↓                                          ↓    │
│  Kafka (Event Bus)  ←  Eureka (Service Registry) │
│                                                  │
└──────────────────────────────────────────────────┘
```

## 📚 Services Overview

### Eureka Server (8761)
- Service registry and discovery
- Health monitoring
- Auto-registration of services

### API Gateway (8000)
- Central routing point
- CORS configuration
- Request/response handling
- OpenAPI aggregation

### Product Service (8001)
- Product catalog management
- Search and filtering
- Inventory tracking
- Events: `ProductCreated`, `ProductUpdated`, `InventoryChanged`

### Order Service (8002)
- Order management
- Order status tracking
- Kafka consumer for payment events
- Events: `OrderCreated`, `OrderConfirmed`, `OrderShipped`

### Payment Service (8003)
- Payment processing
- Payment status tracking
- Events: `PaymentInitiated`, `PaymentCompleted`, `PaymentFailed`

## 🗄️ Database Migrations (Flyway)

Each service has automatic database migrations on startup:

```
product-service/src/main/resources/db/migration/
├── V1__Initial_schema.sql      # Initial tables and indexes
└── V2__*.sql                    # Future migrations

order-service/src/main/resources/db/migration/
└── V1__Initial_schema.sql

payment-service/src/main/resources/db/migration/
└── V1__Initial_schema.sql
```

No manual database setup needed!

## 📡 Event-Driven Architecture

### Kafka Topics

- `product-events` - Product catalog changes
- `order-events` - Order lifecycle events
- `payment-events` - Payment status updates
- `inventory-events` - Stock changes

### Example Flow

```
User creates order
  → Order Service publishes: OrderCreated
    → Payment Service consumes & processes
      → Payment Service publishes: PaymentCompleted
        → Order Service consumes & updates status
          → Product Service updates inventory
```

## 📖 API Documentation

All services have auto-generated Swagger documentation:

### Gateway (Aggregated)
```
http://localhost:8000/swagger-ui.html
```

### Individual Services
```
http://localhost:8001/swagger-ui.html   (Product)
http://localhost:8002/swagger-ui.html   (Order)
http://localhost:8003/swagger-ui.html   (Payment)
```

Try-it-out endpoints directly in the browser!

## 🚀 Key Features

✅ **Database Migrations** - Flyway version control
✅ **Event-Driven** - Kafka for async communication  
✅ **Service Discovery** - Eureka auto-registration
✅ **API Documentation** - OpenAPI 3.0 Swagger
✅ **Health Checks** - Service health endpoints
✅ **Docker** - Multi-container orchestration
✅ **Scalability** - Stateless services
✅ **Logging** - Structured JSON logging
✅ **Error Handling** - Global exception handlers

## 📝 Project Structure

```
quickcommerce/
├── docker-compose.yml           # All services orchestration
├── pom.xml                      # Parent POM
│
├── eureka-server/               # Service Registry
│   ├── pom.xml
│   └── src/main/...
│
├── api-gateway/                 # Central Gateway
│   ├── pom.xml
│   └── src/main/...
│
├── product-service/             # Product Management
│   ├── pom.xml
│   ├── src/main/java/com/quickcommerce/product/
│   └── src/main/resources/db/migration/
│
├── order-service/               # Order Management
│   ├── pom.xml
│   ├── src/main/java/com/quickcommerce/order/
│   └── src/main/resources/db/migration/
│
└── payment-service/             # Payment Processing
    ├── pom.xml
    ├── src/main/java/com/quickcommerce/payment/
    └── src/main/resources/db/migration/
```

## 🔧 Common Commands

```bash
# Start everything
docker-compose up -d

# View logs (all services)
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Check service status
docker-compose ps

# View specific service logs
docker-compose logs -f product-service

# Rebuild containers
docker-compose up -d --build

# Access PostgreSQL directly
docker exec -it quickcommerce-product-db psql -U quickcommerce_user -d quickcommerce_product

# View Kafka topics
docker exec -it quickcommerce-kafka kafka-topics --list --bootstrap-server localhost:9092
```

## ✅ Health Check

```bash
# Check all services
for port in 8761 8000 8001 8002 8003; do 
  echo -n "Port $port: "
  curl -s http://localhost:$port/actuator/health | jq '.status' 2>/dev/null || echo "DOWN"
done
```

Expected output:
```
Port 8761: UP
Port 8000: UP
Port 8001: UP
Port 8002: UP
Port 8003: UP
```

## 🔗 API Endpoints

### Product Service
```
GET    /api/products              # List all products
GET    /api/products/{id}         # Get product by ID
GET    /api/products/sku/{sku}    # Get product by SKU
GET    /api/products/search       # Search products
POST   /api/products              # Create product
PUT    /api/products/{id}         # Update product
DELETE /api/products/{id}         # Delete product
PATCH  /api/products/{id}/stock   # Update stock
```

### Order Service
```
GET    /api/orders/{id}           # Get order by ID
GET    /api/orders/number/{num}   # Get order by number
GET    /api/orders/customer/{id}  # Get customer orders
GET    /api/orders/status/{status} # Get orders by status
POST   /api/orders                # Create order
PATCH  /api/orders/{id}/status    # Update order status
DELETE /api/orders/{id}/cancel    # Cancel order
```

### Payment Service
```
GET    /api/payments/{id}         # Get payment by ID
GET    /api/payments/transaction/{id} # Get by transaction
GET    /api/payments/order/{id}   # Get order payments
POST   /api/payments              # Initiate payment
POST   /api/payments/{id}/process # Process payment
POST   /api/payments/{id}/complete # Complete payment
POST   /api/payments/{id}/fail    # Fail payment
POST   /api/payments/{id}/refund  # Refund payment
```

## 🐛 Troubleshooting

### Services not starting?
```bash
# Check Docker is running
docker ps

# Check logs
docker-compose logs -f service-name

# Rebuild
docker-compose down -v && docker-compose up -d --build
```

### Database connection error?
```bash
# Check database is healthy
docker-compose ps postgres-product

# Wait a bit longer (60 seconds) and try again
# Databases take time to initialize
```

### Eureka dashboard shows services as DOWN?
- Wait 30-60 seconds for heartbeats to register
- Check service logs for connection errors
- Ensure all services are running: `docker-compose ps`

### Kafka topics not created?
```bash

docker exec quickcommerce-kafka kafka-topics --create \
  --topic product-events \
  --bootstrap-server localhost:9092
```

## 📚 Development

### Running locally (without Docker)

```bash
# Terminal 1: Eureka Server
cd eureka-server
mvn spring-boot:run

# Terminal 2: Product Service
cd product-service
mvn spring-boot:run

# Terminal 3: Order Service
cd order-service
mvn spring-boot:run

# Terminal 4: Payment Service
cd payment-service
mvn spring-boot:run

# Terminal 5: API Gateway
cd api-gateway
mvn spring-boot:run
```

Start services in order: Eureka → Services → Gateway

### Building for production

```bash
# Build all services
mvn clean install

# Create Docker images (add Dockerfile to each service)
docker build -t quickcommerce:latest .
```

## 📊 Monitoring

### Eureka Dashboard
http://localhost:8761

Shows real-time status of all registered services.

### Kafka UI
http://localhost:9080

Monitor topics, consumers, and message flow.

### PgAdmin
http://localhost:5050

Manage PostgreSQL databases with GUI.

### Actuator Endpoints

```
# Service health
http://localhost:8001/actuator/health

# Metrics
http://localhost:8001/actuator/metrics

# All actuator endpoints
http://localhost:8001/actuator
```

## 🔐 Security Considerations

- ✅ Input validation on all endpoints
- ✅ SQL injection prevention (JPA)
- ✅ CORS configured for frontend
- ✅ Error messages don't leak sensitive data
- ✅ Database credentials in environment variables
- ⚠️ Add JWT/OAuth for production

## 🚀 Next Steps

1. Add authentication (Spring Security + JWT)
2. Implement authorization rules
3. Add circuit breakers (Hystrix/Resilience4j)
4. Setup metrics and monitoring (Prometheus + Grafana)
5. Add distributed tracing (Sleuth + Zipkin)
6. Deploy to Kubernetes

For issues and questions, check the logs first:
```bash
docker-compose logs -f service-name
```

