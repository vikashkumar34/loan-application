# Database Setup and Documentation

## Overview
This directory contains the database schema, scripts, and documentation for the Loan Disbursement System.

## Database Connection Details
- **Host**: localhost
- **Port**: 5432
- **Database**: loan_disbursement
- **Username**: postgres
- **Password**: postgres

## Files
- `db_schema.sql` - Original H2/MySQL compatible schema
- `postgresql_schema.sql` - PostgreSQL-specific DDL script
- `mock_data.sql` - Sample data insertion script
- `README.md` - This documentation file

## Database Schema

### Entity Relationship Diagram (ERD)

```
┌─────────────────┐       ┌─────────────────────┐       ┌─────────────────┐
│     users       │       │ loan_applications   │       │ disbursements   │
├─────────────────┤       ├─────────────────────┤       ├─────────────────┤
│ id (PK)         │◄──────┤ user_id (FK)        │       │ id (PK)         │
│ username (UQ)   │       │ id (PK)             │◄──────┤ loan_app_id (FK)│
│ password        │       │ amount              │   │   │ transaction_ref │
│ email (UQ)      │       │ term_months         │   │   │ requested_date  │
│ full_name       │       │ purpose             │   │   │ approved_date   │
│ role (ENUM)     │       │ bank_account_number │   │   │ disbursed_date  │
│ created_at      │       │ ifsc_code           │   │   │ disbursed_by    │
│ updated_at      │       │ status (ENUM)       │   │   │ remarks         │
└─────────────────┘       │ submitted_date      │   │   └─────────────────┘
                          │ approved_date       │   │
                          │ rejected_date       │   │
                          │ rejection_reason    │   │
                          │ disbursed_date      │   │
                          │ transaction_ref (UQ)│   │
                          └─────────────────────┘   │
                                                     │
                                                     │
                                                     ▼
```

### Tables Description

#### 1. users
Stores user account information including authentication details and roles.

**Columns:**
- `id` - Primary key, auto-increment
- `username` - Unique username for login
- `password` - BCrypt hashed password
- `email` - Unique email address
- `full_name` - User's full name
- `role` - User role (USER/ADMIN)
- `created_at` - Account creation timestamp
- `updated_at` - Last update timestamp (auto-updated)

#### 2. loan_applications
Stores loan application details submitted by users.

**Columns:**
- `id` - Primary key, auto-increment
- `user_id` - Foreign key to users table
- `amount` - Loan amount requested
- `term_months` - Loan term in months
- `purpose` - Purpose of the loan
- `bank_account_number` - User's bank account
- `ifsc_code` - Bank's IFSC code
- `status` - Application status (SUBMITTED/APPROVED/REJECTED/DISBURSED)
- `submitted_date` - When application was submitted
- `approved_date` - When application was approved (nullable)
- `rejected_date` - When application was rejected (nullable)
- `rejection_reason` - Reason for rejection (nullable)
- `disbursed_date` - When loan was disbursed (nullable)
- `transaction_reference` - Unique transaction reference (nullable)

#### 3. disbursements
Stores disbursement transaction details for approved loans.

**Columns:**
- `id` - Primary key, auto-increment
- `loan_application_id` - Foreign key to loan_applications (unique)
- `transaction_reference` - Unique 12-digit transaction reference
- `requested_date` - When loan was originally requested
- `approved_date` - When loan was approved
- `disbursed_date` - When disbursement occurred
- `disbursed_by_admin` - Admin username who processed disbursement
- `remarks` - Additional remarks (nullable)

## Sample Data

### Users Created:
1. **admin** - Administrator account
   - Username: `admin`
   - Password: `admin`
   - Role: ADMIN

2. **user1** - Regular user
   - Username: `user1`
   - Password: `user@123`
   - Role: USER

3. **user2** - Regular user with disbursed loan
   - Username: `user2`
   - Password: `user@123`
   - Role: USER

4. **user3** - Regular user with rejected loan
   - Username: `user3`
   - Password: `user@123`
   - Role: USER

### Loan Applications:
- **user1**: ₹50,000 for 6 months (Education) - Status: SUBMITTED
- **user2**: ₹75,000 for 12 months (Home renovation) - Status: DISBURSED
- **user3**: ₹1,00,000 for 24 months (Business expansion) - Status: REJECTED

### Disbursements:
- **user2's loan**: Transaction reference `123456789012`, disbursed by admin

## Useful Queries

### Get all users
```sql
SELECT id, username, email, role, created_at FROM users ORDER BY id;
```

### Get loan applications with user details
```sql
SELECT la.id, u.username, la.amount, la.term_months, la.purpose, la.status, la.submitted_date
FROM loan_applications la
JOIN users u ON la.user_id = u.id
ORDER BY la.submitted_date DESC;
```

### Get disbursements with details
```sql
SELECT d.id, u.username, la.amount, d.transaction_reference, d.disbursed_date, d.disbursed_by_admin
FROM disbursements d
JOIN loan_applications la ON d.loan_application_id = la.id
JOIN users u ON la.user_id = u.id
ORDER BY d.disbursed_date DESC;
```

### Get loan statistics
```sql
SELECT status, COUNT(*) as count, SUM(amount) as total_amount
FROM loan_applications
GROUP BY status;
```

## Connecting to Database

### Using psql (Command Line)
```bash
psql -h localhost -p 5432 -U postgres -d loan_disbursement
```

### Using JDBC URL (for Spring Boot)
```
jdbc:postgresql://localhost:5432/loan_disbursement
```

## Notes
- All passwords are BCrypt hashed
- Transaction references are unique 12-digit numbers
- Foreign key constraints ensure data integrity
- Indexes are created on frequently queried columns
- The `updated_at` field is automatically maintained via triggers
