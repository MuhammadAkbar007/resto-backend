# 🍽️ RestoApp Backend

## Description
🍴 A full-featured RESTful backend for a restaurant management system built with **Spring Boot 3** 🌱, using **JWT security** 🚔, **role-based access control** ☸️,
and **Swagger/OpenAPI** 📄 for API documentation. This project includes user registration with email 📧 OTP verification, scheduling for auto-cleanup 🧹,
dish and order management, and soft/hard delete ❌ functionality.

---

## 🚀 Features

### 🖥️ System
 - **Data Initialization:**
    * 🎄 Seeder to pre-populate the database with default roles (`admin`, `customer`, `manager`, `employee`), and an admin user.

### 👥 **User Management**
- Registration with **two-step email verification (OTP)** 📧
- JWT-based **log in**, **log out**, and **refresh token** 🚔
- Admin abilities ☸️:
  - Assign/revoke roles (`admin`, `manager`, `employee`)
  - Change user status (`active`, `block`, `deleted`)
  - Paginated and filtered user listing
- Users can 🙆‍♂️:
  - Update their profile
  - Change profile picture

### 🚩 **OTP System**
- Secure OTP verification for email activation 🚔
- Rate limiting & attempt count 🛑
- Auto-cleanup of expired OTPs 🧹

### 🥄 **Dish Management**
- CRUD operations
- Soft & hard delete
- Pagination, sorting, and filtering
- Enum-based category types: `HOT`, `COLD`, `SOUP`, `GRILL`, `APPETIZER`, `DESSERT`

### 🚚 **Order Management**
- CRUD operations with status flow:
  - `PENDING` → `PREPARING` → `COMPLETED` / `CANCELED`
- Soft & hard delete
- Pagination, sorting, filtering

### 🍕 **Order Item Management**
- CRUD operations
- Change `orderType`: `DINE_IN`, `TO_GO`, `DELIVERY`

### 🖼 **Attachment Management**
- Upload files
- Download original or thumbnail version
- Serve image previews
- File storage support: `DATABASE`, `FILESYSTEM`

---

## 📦 Technologies Used

- **Language:** Java 17 ☕️
- **Backend Framework:** Spring Boot 3 🌱
- **Security:** Spring Security with JWT 🚔
- **Database:** Spring Data JPA + PostgreSQL 🐘
- **Mapping:** MapStruct for DTO mapping 🏗
- **Validation:** Jakarta Validation 📏
- **API Documentation:** Swagger/OpenAPI via `springdoc-openapi` 📄
- **Boilerplate Code Reduction:** Lombok 🌶 
 - **Build Tool:** Maven 🪶

---

## 🧩 Entity Overview

<details>
<summary><strong>User</strong></summary>

- `id`, `firstName`, `lastName`, `email`, `phoneNumber`, `password`, `photo`, `status`, `createdAt`, `visible`  
- Relations:
  - `roles` (ManyToMany)
  - `orders` (OneToMany)
  - `refreshTokens` (OneToMany)
</details>

<details>
<summary><strong>Role</strong></summary>

- `id`, `roleType`, `createdAt`, `visible`  
- Enum values: `CUSTOMER`, `EMPLOYEE`, `MANAGER`, `ADMIN`
</details>

<details>
<summary><strong>Dish</strong></summary>

- `id`, `name`, `price`, `quantityAvailable`, `dishCategory`, `createdAt`, `visible`
- Enum values: `HOT`, `COLD`, `SOUP`, `GRILL`, `APPETIZER`, `DESSERT`
</details>

<details>
<summary><strong>Order</strong></summary>

- `id`, `number`, `discount`, `totalPrice`, `orderStatus`, `createdAt`, `visible`
- Enum values: `PENDING`, `PREPARING`, `COMPLETED`, `CANCELED`
</details>

<details>
<summary><strong>OrderItem</strong></summary>

- `id`, `quantity`, `price`, `note`, `orderType`, `createdAt`
- Enum values: `DINE_IN`, `TO_GO`, `DELIVERY`
</details>

<details>
<summary><strong>Attachment</strong></summary>

- `id`, `originalName`, `size`, `extension`, `contentType`, `filePath`, `content`, `createdAt`, `visible`
- Storage types: `DATABASE`, `FILESYSTEM`
</details>

<details>
<summary><strong>RefreshToken</strong></summary>

- `id`, `token`, `expiryDate`, `createdAt`
</details>

---

## 🛡️ Security

- JWT authentication with stateless sessions
- Role-based access control with method-level security
- Email OTP verification flow
- CSRF disabled (stateless API)
- Swagger endpoints whitelisted

---

## 🎨 Based on open-source figma:
Figma [link](https://www.figma.com/file/0rsOUjLNTiZA5LTv6CfGzU/Food-POS-Dark---Tablet-Device-(Community)?node-id=0%3A1)
[pdf](/src/resources/restoFigma.pdf)

## ✍️ Author
Created by [Akbar](https://github.com/MuhammadAkbar007).
