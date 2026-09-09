# SareeAura | Luxury Indian Saree E-Commerce Platform

SareeAura is a luxury Indian fashion and saree e-commerce platform built using a **Modular Monolith** architecture with Spring Boot 3 (Java 21), React (TypeScript, Vite, Tailwind CSS), and MySQL 8.

---

## Architecture Overview
- **Frontend**: React 18/19, TypeScript, Vite, Tailwind CSS, TanStack Query, Zustand, React Router, Lucide React, Framer Motion, Recharts.
- **Backend**: Java 21, Spring Boot 3.3.3, Spring Data JPA, Hibernate, Spring Security, JWT, Lombok, Swagger/OpenAPI.
- **Database**: MySQL 8.0 with transactional guarantees and audit trails.
- **Payment**: Razorpay (Test Mode).
- **Media**: Cloudinary (Image URLs persisted in MySQL).

---

## Phase 1 Status: Project Setup & Foundation
- [x] Project structure initialization (Frontend & Backend)
- [x] Modular Monolith backend structure (`com.sareeaura.*`)
- [x] Spring Boot 3.3.3 configuration with Java 21
- [x] Spring Security 6, JWT, and Swagger OpenAPI integration
- [x] React + Vite + TypeScript + Tailwind CSS configuration
- [x] Centralized Axios API client with interceptors
- [x] MySQL 8 database connection and verification
- [x] Root configuration (`.env.example`, `.gitignore`, `docker-compose.yml`)

---

## Running the Project

### Prerequisites
- Java 21 LTS
- Apache Maven 3.9+
- Node.js 20+ & npm
- MySQL 8.0

### 1. Database Setup
```bash
mysql -u root -p < database/schema.sql
```

### 2. Backend Setup
```bash
cd backend
mvn clean spring-boot:run
```
- API Base URL: `http://localhost:8080`
- Swagger UI Documentation: `http://localhost:8080/swagger-ui.html`
- Health Check: `http://localhost:8080/api/health`

### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
- Web Application: `http://localhost:5173`
