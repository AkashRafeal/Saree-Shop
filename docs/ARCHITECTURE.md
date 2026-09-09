# SareeAura Architecture Specification

## 1. Overview
SareeAura is a luxury Indian Saree e-commerce platform built on a **Modular Monolith** architecture.
- **Backend**: Single Spring Boot 3.3 application with clear domain boundaries.
- **Frontend**: React 18/19 SPA built with Vite, TypeScript, Tailwind CSS, TanStack Query, and Zustand.
- **Database**: MySQL 8.0 with normalized schema and transactional consistency.

## 2. Modular Monolith Backend Boundaries
The backend application package `com.sareeaura` is structured strictly by business capabilities:

```
com.sareeaura
├── auth          # Registration, login, JWT token generation & refresh
├── user          # User profiles, customer accounts
├── product       # Sarees, variants, specifications, images
├── category      # Saree categories, weaves, fabrics, occasions
├── inventory     # Stock levels, reservations, low stock alerts
├── cart          # Shopping cart management
├── wishlist      # Saved items
├── order         # Orders, immutable snapshots, status workflow
├── payment       # Razorpay integration, webhook/signature verification
├── coupon        # Discounts, promotional codes, usage rules
├── review        # Customer reviews, ratings, verified purchases
├── banner        # Promotional banners, hero slides
├── notification  # Email alerts and system notifications
├── admin         # Administrative dashboards, reporting, operations
├── security      # Spring Security 6 & JWT authentication filters
├── config        # Application, OpenAPI/Swagger, CORS configuration
├── exception     # Global API error handlers and custom exceptions
└── common        # Shared DTO envelopes, constants, base entities
```

## 3. Communication Rules Between Modules
- Modules interact via well-defined Service interfaces and DTOs.
- Avoid direct cross-module entity relationships that create hard database coupling.
- Individual modules are designed to be easily extractable into standalone microservices in the future if required.
