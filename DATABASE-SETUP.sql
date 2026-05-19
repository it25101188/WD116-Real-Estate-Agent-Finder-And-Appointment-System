-- Real Estate System Database Setup
-- Run these commands in your MySQL Workbench or Command Line

-- 1. Create the database
CREATE DATABASE IF NOT EXISTS realestate_db;
USE realestate_db;

-- Note: Hibernate with ddl-auto=update usually handles table creation.
-- If you are having issues, ensure your MySQL user has CREATE/UPDATE permissions.

-- Sample queries to check if tables were created:
-- SHOW TABLES;
-- SELECT * FROM users;
-- SELECT * FROM agents;
-- SELECT * FROM appointments;
-- SELECT * FROM reviews;

-- If you need to manually create the tables (not recommended if Hibernate is working):
/*
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS agents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(255),
    specialization VARCHAR(255),
    license_number VARCHAR(255),
    bio TEXT,
    years_experience INT,
    office_address VARCHAR(255),
    rating DOUBLE DEFAULT 0.0,
    total_reviews INT DEFAULT 0,
    available BOOLEAN DEFAULT TRUE
);
*/
