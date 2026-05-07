-- Loan Disbursement System Database Schema
-- Generated DDL for H2 Database (used in development)
-- Compatible with MySQL/PostgreSQL with minor adjustments

-- Create database (for MySQL/PostgreSQL - comment out for H2)
-- CREATE DATABASE IF NOT EXISTS loan_disbursement_db;
-- USE loan_disbursement_db;

-- =====================================================
-- ENUM TYPES (for PostgreSQL - comment out for H2/MySQL)
-- =====================================================
-- CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
-- CREATE TYPE loan_status AS ENUM ('SUBMITTED', 'APPROVED', 'REJECTED', 'DISBURSED');

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- =====================================================
-- LOAN APPLICATIONS TABLE
-- =====================================================
CREATE TABLE loan_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    term_months INT NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    bank_account_number VARCHAR(255) NOT NULL,
    ifsc_code VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('SUBMITTED', 'APPROVED', 'REJECTED', 'DISBURSED')),
    submitted_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_date TIMESTAMP NULL,
    rejected_date TIMESTAMP NULL,
    rejection_reason VARCHAR(255) NULL,
    disbursed_date TIMESTAMP NULL,
    transaction_reference VARCHAR(12) NULL,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_transaction_ref (transaction_reference),

    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_submitted_date (submitted_date),
    INDEX idx_transaction_ref (transaction_reference)
);

-- =====================================================
-- DISBURSEMENTS TABLE
-- =====================================================
CREATE TABLE disbursements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_application_id BIGINT NOT NULL UNIQUE,
    transaction_reference VARCHAR(12) NOT NULL UNIQUE,
    requested_date TIMESTAMP NOT NULL,
    approved_date TIMESTAMP NOT NULL,
    disbursed_date TIMESTAMP NOT NULL,
    disbursed_by_admin VARCHAR(255) NOT NULL,
    remarks VARCHAR(500) NULL,

    FOREIGN KEY (loan_application_id) REFERENCES loan_applications(id) ON DELETE CASCADE,

    INDEX idx_loan_application_id (loan_application_id),
    INDEX idx_transaction_ref (transaction_reference),
    INDEX idx_disbursed_date (disbursed_date),
    INDEX idx_disbursed_by_admin (disbursed_by_admin)
);

-- =====================================================
-- SAMPLE DATA (Optional - for testing)
-- =====================================================

-- Insert admin user
INSERT INTO users (username, password, email, full_name, role, created_at, updated_at)
VALUES ('admin', '$2a$10$8K3W2QX8Jc8Jc8Jc8Jc8JeJc8Jc8Jc8Jc8Jc8Jc8Jc8Jc8Jc8Jc8Jc', 'admin@loanapp.com', 'System Administrator', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample regular user
INSERT INTO users (username, password, email, full_name, role, created_at, updated_at)
VALUES ('john_doe', '$2a$10$8K3W2QX8Jc8Jc8Jc8Jc8JeJc8Jc8Jc8Jc8Jc8Jc8Jc8Jc8Jc8Jc8Jc', 'john.doe@example.com', 'John Doe', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample loan application
INSERT INTO loan_applications (user_id, amount, term_months, purpose, bank_account_number, ifsc_code, status, submitted_date)
VALUES (2, 100000.00, 12, 'Home renovation', '1234567890', 'HDFC0001234', 'SUBMITTED', CURRENT_TIMESTAMP);

-- =====================================================
-- USEFUL QUERIES (Optional - for reference)
-- =====================================================

-- Get all loan applications with user details
-- SELECT la.*, u.username, u.email, u.full_name
-- FROM loan_applications la
-- JOIN users u ON la.user_id = u.id;

-- Get disbursements with loan details
-- SELECT d.*, la.amount, la.purpose, u.username
-- FROM disbursements d
-- JOIN loan_applications la ON d.loan_application_id = la.id
-- JOIN users u ON la.user_id = u.id;

-- Get loan statistics by status
-- SELECT status, COUNT(*) as count, SUM(amount) as total_amount
-- FROM loan_applications
-- GROUP BY status;

-- Get recent disbursements
-- SELECT d.*, la.amount, u.username
-- FROM disbursements d
-- JOIN loan_applications la ON d.loan_application_id = la.id
-- JOIN users u ON la.user_id = u.id
-- ORDER BY d.disbursed_date DESC
-- LIMIT 10;
