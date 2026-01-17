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

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming Language |
| Spring Boot | 3.2.0 | Application Framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data JPA | 3.x | Data Persistence |
| MySQL | 8.x | Database |
| JWT (jjwt) | 0.12.3 | Token-based Authentication |
| OAuth2 | - | Google Login Integration |
| Maven | 3.x | Build Tool |
| Swagger/OpenAPI | 2.3.0 | API Documentation |
| Lombok | - | Boilerplate Reduction |

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

## 🔧 Setup & Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Google Cloud Console account (for OAuth2)

### Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/dormex.git
cd dormex
```

### Step 2: Configure Environment Variables

```bash
# Copy the example environment file
cp .env.example .env

# Edit .env with your configuration
# IMPORTANT: Never commit .env to version control!
```

### Step 3: Configure MySQL Database

```sql
-- Create the database
CREATE DATABASE dormex_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create a user (optional)
CREATE USER 'dormex_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON dormex_db.* TO 'dormex_user'@'localhost';
FLUSH PRIVILEGES;
```

### Step 4: Configure Google OAuth2 (Optional)

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Navigate to APIs & Services → Credentials
4. Create OAuth 2.0 Client ID
5. Add authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
6. Copy Client ID and Client Secret to your `.env` file

### Step 5: Run the Application

```bash
# Using Maven
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/dormex-1.0.0.jar
```

### Step 6: Access the Application

- **API Base URL**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 🔐 Environment Variables

| Variable | Required | Description | Default |
|----------|----------|-------------|---------|
| `DB_HOST` | No | MySQL host | localhost |
| `DB_PORT` | No | MySQL port | 3306 |
| `DB_NAME` | No | Database name | dormex_db |
| `DB_USERNAME` | Yes | Database username | - |
| `DB_PASSWORD` | Yes | Database password | - |
| `JWT_SECRET` | Yes | JWT signing key (min 32 chars) | - |
| `JWT_EXPIRATION` | No | Token expiry (ms) | 86400000 (24h) |
| `GOOGLE_CLIENT_ID` | Yes* | Google OAuth client ID | - |
| `GOOGLE_CLIENT_SECRET` | Yes* | Google OAuth client secret | - |
| `CORS_ALLOWED_ORIGINS` | No | Allowed frontend URLs | localhost:3000 |
| `ADMIN_EMAIL` | No | Default admin email | admin@dormex.com |
| `ADMIN_PASSWORD` | Yes | Default admin password | - |

*Required only if Google OAuth is enabled

## 📚 API Documentation

Once the application is running, access the interactive API documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### API Endpoints Overview

| Module | Endpoint Prefix | Description |
|--------|-----------------|-------------|
| Auth | `/api/auth` | Authentication endpoints |
| Users | `/api/users` | User management |
| Students | `/api/students` | Student CRUD operations |
| Rooms | `/api/rooms` | Room management |
| Blocks | `/api/blocks` | Block/floor management |
| Complaints | `/api/complaints` | Complaint handling |
| Mess Menu | `/api/mess-menu` | Menu management |
| Dashboard | `/api/dashboard` | Analytics & stats |

## 🔒 Security

- All passwords are hashed using BCrypt
- JWT tokens for stateless authentication
- Role-based access control (RBAC)
- CORS protection enabled
- Input validation on all endpoints
- SQL injection prevention via JPA

### ⚠️ Important Security Notes

1. **NEVER commit `.env` file** - It's already in `.gitignore`
2. Use strong passwords (min 12 characters)
3. Generate a secure JWT secret (min 256 bits)
4. In production, use HTTPS only
5. Rotate secrets periodically

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## 📦 Building for Production

```bash
# Build production JAR
./mvnw clean package -Dmaven.test.skip=true

# Run with production profile
java -jar -Dspring.profiles.active=prod target/dormex-1.0.0.jar
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support, email support@dormex.com or create an issue in this repository.

---

<p align="center">
  Made with ❤️ by the Dormex Team
</p>
