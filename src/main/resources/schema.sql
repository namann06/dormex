-- ===================================================================
-- DORMEX - Database Schema
-- ===================================================================
-- This file contains the complete database schema for the Dormex
-- Hostel Management System. Run this script to create all tables.
-- Note: JPA/Hibernate can auto-create tables with ddl-auto=update,
-- but this file serves as documentation and for manual setup.
-- ===================================================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS dormex_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE dormex_db;

-- ===================================================================
-- Users Table
-- Stores all user accounts (Admin and Students)
-- ===================================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    role ENUM('ADMIN', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
    auth_provider ENUM('LOCAL', 'GOOGLE') NOT NULL DEFAULT 'LOCAL',
    provider_id VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Blocks Table
-- Hostel blocks/buildings
-- ===================================================================
CREATE TABLE IF NOT EXISTS blocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    total_floors INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_blocks_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Rooms Table
-- Individual rooms within blocks
-- ===================================================================
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL,
    block_id BIGINT NOT NULL,
    floor_number INT NOT NULL DEFAULT 1,
    capacity INT NOT NULL DEFAULT 1,
    current_occupancy INT NOT NULL DEFAULT 0,
    room_type ENUM('SINGLE', 'DOUBLE', 'TRIPLE', 'DORMITORY') NOT NULL DEFAULT 'DOUBLE',
    status ENUM('AVAILABLE', 'FULL', 'MAINTENANCE', 'RESERVED') NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (block_id) REFERENCES blocks(id) ON DELETE CASCADE,
    UNIQUE KEY uk_room_block (room_number, block_id),
    INDEX idx_rooms_status (status),
    INDEX idx_rooms_block (block_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Students Table
-- Student records with room assignments
-- ===================================================================
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(20),
    guardian_name VARCHAR(100),
    guardian_phone VARCHAR(20),
    address TEXT,
    date_of_birth DATE,
    room_id BIGINT,
    status ENUM('ACTIVE', 'LEFT', 'TRANSFERRED', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    admission_date DATE NOT NULL,
    leaving_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL,
    INDEX idx_students_roll (roll_number),
    INDEX idx_students_status (status),
    INDEX idx_students_room (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Room Transfer Requests Table
-- Track room transfer requests from students
-- ===================================================================
CREATE TABLE IF NOT EXISTS room_transfer_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    current_room_id BIGINT NOT NULL,
    requested_room_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_remarks TEXT,
    processed_by BIGINT,
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (current_room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (requested_room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_transfer_status (status),
    INDEX idx_transfer_student (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Complaints Table
-- Student complaints and issues
-- ===================================================================
CREATE TABLE IF NOT EXISTS complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    category ENUM('MAINTENANCE', 'CLEANLINESS', 'FOOD', 'SECURITY', 'ROOMMATE', 'FACILITIES', 'OTHER') NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') NOT NULL DEFAULT 'MEDIUM',
    resolution_notes TEXT,
    resolved_by BIGINT,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (resolved_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_complaints_status (status),
    INDEX idx_complaints_student (student_id),
    INDEX idx_complaints_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Mess Menu Table
-- Daily mess menu items
-- ===================================================================
CREATE TABLE IF NOT EXISTS mess_menus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_date DATE NOT NULL,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    meal_type ENUM('BREAKFAST', 'LUNCH', 'SNACKS', 'DINNER') NOT NULL,
    items TEXT NOT NULL,
    special_note VARCHAR(255),
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_menu_date_meal (menu_date, meal_type),
    INDEX idx_menu_date (menu_date),
    INDEX idx_menu_day (day_of_week)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Refresh Tokens Table
-- Store refresh tokens for JWT authentication
-- ===================================================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_refresh_token (token),
    INDEX idx_refresh_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Activity Log Table (Optional - for audit trail)
-- ===================================================================
CREATE TABLE IF NOT EXISTS activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_activity_user (user_id),
    INDEX idx_activity_action (action),
    INDEX idx_activity_date (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Sample Data (Optional - for testing)
-- ===================================================================

-- Insert sample blocks
-- INSERT INTO blocks (name, description, total_floors) VALUES
-- ('Block A', 'Main hostel building - Boys', 4),
-- ('Block B', 'Secondary building - Boys', 3),
-- ('Block C', 'Girls hostel', 4);

-- ===================================================================
-- End of Schema
-- ===================================================================
