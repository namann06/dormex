# Dormex - Hostel Management System

<p align="center">
  <strong>A comprehensive backend solution for hostel management</strong>
</p>

## 📋 Overview

Dormex is a modern, secure, and scalable backend system for managing hostel operations. Built with Spring Boot, it provides RESTful APIs for student management, room allocation, complaint handling, and mess menu management.

## 🚀 Features

- **Authentication & Authorization**
  - Email/Password login with JWT tokens
  - Google OAuth2 integration
  - Role-based access control (ADMIN, STUDENT)
  - Secure password hashing with BCrypt

- **Student Management**
  - CRUD operations for student records
  - Room assignment and transfers
  - Student status tracking (ACTIVE, LEFT, TRANSFERRED)

- **Room Management**
  - Block and floor organization
  - Room capacity and occupancy tracking
  - Vacancy management and room transfers

- **Complaint Management**
  - Raise and track complaints
  - Category-based complaint organization
  - Status workflow (OPEN → IN_PROGRESS → RESOLVED)

- **Mess Menu Management**
  - Daily and weekly menu management
  - Meal type organization (Breakfast, Lunch, Dinner)

- **Dashboard & Analytics**
  - Real-time statistics
  - Occupancy reports
  - Complaint analytics



## 📁 Project Structure

```
dormex/
├── src/
│   ├── main/
│   │   ├── java/com/dormex/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST API controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Custom exceptions & handlers
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── security/        # Security components
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Test classes
├── .env.example                 # Environment template
├── .gitignore
├── pom.xml
└── README.md
```



