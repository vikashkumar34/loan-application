-- Loan Disbursement System Database Schema
-- PostgreSQL DDL Script

-- =====================================================
-- CREATE DATABASE (if not exists)
-- =====================================================
-- Note: Run this separately if needed
-- CREATE DATABASE "loan-disbursement";

-- Connect to the database
-- \c loan-disbursement;

-- =====================================================
-- ENUM TYPES
-- =====================================================
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE loan_status AS ENUM ('SUBMITTED', 'APPROVED', 'REJECTED', 'DISBURSED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- =====================================================
-- LOAN APPLICATIONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS loan_applications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    term_months INTEGER NOT NULL,
    purpose TEXT NOT NULL,
    bank_account_number VARCHAR(255) NOT NULL,
    ifsc_code VARCHAR(255) NOT NULL,
    status loan_status NOT NULL DEFAULT 'SUBMITTED',
    submitted_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_date TIMESTAMP WITHOUT TIME ZONE,
    rejected_date TIMESTAMP WITHOUT TIME ZONE,
    rejection_reason TEXT,
    disbursed_date TIMESTAMP WITHOUT TIME ZONE,
    transaction_reference VARCHAR(12),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (transaction_reference)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_loan_applications_user_id ON loan_applications(user_id);
CREATE INDEX IF NOT EXISTS idx_loan_applications_status ON loan_applications(status);
CREATE INDEX IF NOT EXISTS idx_loan_applications_submitted_date ON loan_applications(submitted_date);
CREATE INDEX IF NOT EXISTS idx_loan_applications_transaction_ref ON loan_applications(transaction_reference);

-- =====================================================
-- DISBURSEMENTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS disbursements (
    id BIGSERIAL PRIMARY KEY,
    loan_application_id BIGINT NOT NULL UNIQUE,
    transaction_reference VARCHAR(12) NOT NULL UNIQUE,
    requested_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    approved_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    disbursed_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    disbursed_by_admin VARCHAR(255) NOT NULL,
    remarks TEXT,

    FOREIGN KEY (loan_application_id) REFERENCES loan_applications(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_disbursements_loan_app_id ON disbursements(loan_application_id);
CREATE INDEX IF NOT EXISTS idx_disbursements_transaction_ref ON disbursements(transaction_reference);
CREATE INDEX IF NOT EXISTS idx_disbursements_disbursed_date ON disbursements(disbursed_date);
CREATE INDEX IF NOT EXISTS idx_disbursements_admin ON disbursements(disbursed_by_admin);

-- =====================================================
-- TRIGGER FOR UPDATED_AT (PostgreSQL)
-- =====================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
