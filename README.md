# Leave Management System — Backend (Spring Boot + MySQL)

A production-ready REST API for managing employee leave requests with **role-based access**, **token auth**, **validation**, **conflict detection**, and **clean error handling**. Pairs with the React + Redux (Vite) frontend in the companion repo.

---

## Tech Stack

- **Java 17+**, **Spring Boot**
- Spring Web, Spring Security, Validation (Jakarta)
- Spring Data JPA (MySQL)
- Lombok
- JUnit/Mockito for tests
- **MySQL** for persistence
- Stateless auth using a simple bearer token

---

## Features

- 🔐 **Authentication**: `POST /login` validates user and returns a stateless simple bearer token.
- 👥 **Roles**: `ADMIN` and `EMPLOYEE` with guarded endpoints & actions.
- 📝 **Leaves CRUD**:
  - **Employee**: create, read, update, delete **only while `PENDING`**.
  - **Admin**: list all, filter by employee, approve/reject, delete.
- ✅ **Validation**: required fields & valid date ranges.
- 🧠 **Conflict detection (bonus)**: reject creating a leave that overlaps an **Approved** leave for the same employee.
- 📊 **Login response includes leave summary**: maximum allowance, days taken, and remaining balance.
- 🌐 **CORS enabled** for local frontend dev (Vite/React on `5173` and CRA on `3000`).
- 🧯 **Global exception handling** with consistent JSON error envelopes.
- 🧪 **Unit tests** for core services and auth flows.
- ⚙️ **Seed users** on startup: `admin/admin123`, `employee/emp123`.

---

## Project Structure
<img width="482" height="703" alt="image" src="https://github.com/user-attachments/assets/ef9ff1f5-d3ac-4680-b00b-6bda9cb627e9" />


---

## Data Model

### `User`
- `id`, `username` (unique), `password` (BCrypt), `role` (`ADMIN`/`EMPLOYEE`), `maximumLeaveCount` (int)

### `Leave`
- `id`, `employee` (User), `startDate`, `endDate`, `reason`, `status` (`PENDING`/`APPROVED`/`REJECTED`)

---

## Security & Auth

- **Stateless**: No server sessions. Every request to protected endpoints must include an `Authorization` header:

```http
Authorization: Bearer SESSION_FLAG_<ROLE>_<USERNAME>
```

Example:
Bearer SESSION_FLAG_ADMIN_admin or Bearer SESSION_FLAG_EMPLOYEE_employee

The token is generated on successful POST /login and parsed by SimpleTokenFilter.

Route protection in SecurityConfig:

POST /login → public

POST /api/users → ADMIN only

/api/leaves/** → authenticated (role rules enforced in service layer)

CORS is enabled for local frontend dev:

Allowed origins: http://localhost:5173, http://localhost:3000

Methods: GET, POST, PUT, DELETE, OPTIONS

Seed Users (created at startup)
Role	Username	Password
ADMIN	admin	admin123
EMPLOYEE	employee	emp123

Passwords are stored encoded (BCrypt). You can create more users via the Admin API.

## Getting Started

### Create a database
```http
CREATE DATABASE leave_mgmt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
### Configure application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/leave_mgmt?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASS

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080

### Run the app
```http
mvn spring-boot:run
```
