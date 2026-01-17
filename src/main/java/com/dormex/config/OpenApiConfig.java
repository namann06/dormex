package com.dormex.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Dormex - Hostel Management System API",
        version = "1.0.0",
        description = """
            **Dormex** is a comprehensive backend API for managing hostel operations.
            
            ## Features
            - 🔐 Authentication (Email/Password & Google OAuth2)
            - 👥 Student Management
            - 🏠 Room & Block Management
            - 📝 Complaint Management
            - 🍽️ Mess Menu Management
            - 📊 Dashboard & Analytics
            
            ## Authentication
            This API uses JWT (JSON Web Token) for authentication. 
            After logging in, include the token in the Authorization header:
            ```
            Authorization: Bearer <your_jwt_token>
            ```
            
            ## Roles
            - **ADMIN**: Full access to all endpoints
            - **STUDENT**: Limited access to student-specific endpoints
            """,
        contact = @Contact(
            name = "Dormex Support",
            email = "support@dormex.com",
            url = "https://dormex.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Local Development Server"
        ),
        @Server(
            url = "https://api.dormex.com",
            description = "Production Server"
        )
    },
    security = @SecurityRequirement(name = "Bearer Authentication")
)
@SecurityScheme(
    name = "Bearer Authentication",
    description = "JWT Bearer token authentication. Login to get your token.",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // Configuration is handled via annotations
}
