package com.dormex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Dormex - Hostel Management System
 * 
 * Main application entry point for the Spring Boot application.
 * This application provides a complete backend for managing hostel operations
 * including student management, room allocation, complaints, and mess menus.
 * 
 * @author Dormex Team
 * @version 1.0.0
 */
@SpringBootApplication
public class DormexApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormexApplication.class, args);
    }

}
