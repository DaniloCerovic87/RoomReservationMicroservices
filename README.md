# Room Reservation Microservices — Setup

This repository contains multiple Spring Boot microservices and supporting infrastructure (PostgreSQL, Kafka, MongoDB).

Each microservice has its own `Dockerfile`, while a single root `docker-compose.yml` starts the entire system.

---

## Prerequisites

- Docker Desktop (Windows/macOS) or Docker Engine (Linux)
- Docker Compose v2
- Available ports on host machine:

```
8080, 8081, 8082, 8083, 8084, 8085  
5432, 5433, 5434, 5435  
9092, 9096  
27017, 8086
```

---

## Project Structure (Expected)

```
docker-compose.yml (root)
_contracts/ (shared module, used by some services)
RoomService/ (contains Dockerfile)
ReservationService/ (contains Dockerfile)
EmployeeService/ (contains Dockerfile)
AuthenticationService/ (contains Dockerfile)
NotificationService/ (contains Dockerfile)
CalendarService/ (contains Dockerfile)
```

---

## How to Run

From the repository root:

```bash
docker compose up -d --build
```

This command:

- Builds all microservices
- Starts PostgreSQL instances
- Starts Kafka
- Starts MongoDB
- Runs all Spring Boot services in detached mode

To stop the system:

```bash
docker compose down
```

---

## Initial Demo Data

On the first application startup, the system automatically inserts predefined demo data using Liquibase database migrations.

### Created Records

- 12 default rooms
- 5 departments
- 1 system administrator account
- 1 employee account

---

### Administrator Account

- **Username:** `system.administrator`
- **Password:** `admin1234`
- **Role:** ADMIN

---

### Employee Account

- **Username:** `marko.jovanovic`
- **Password:** `empl1234`
- **Role:** USER

---

## What This Enables

The predefined dataset allows immediate functional testing of:

- Authentication & authorization
- Reservation creation
- Reservation approval workflow
- Role-based access control

No manual data entry is required before testing the system.

---

## Notes

⚠️ Demo credentials are intended for development and testing purposes only.

If the system was previously started and containers were not removed, Liquibase migrations will not reinsert demo data unless the database volumes are cleared.


To fully reset the environment:

```bash
docker compose down -v 
```

```bash
docker compose up -d --build
```


