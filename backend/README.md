# Loan Disbursement System - Backend

## Overview
This is a Spring Boot 3.x backend for an End-to-End Loan Disbursement System with JWT authentication, role-based access control, and a complete disbursement workflow.

## Tech Stack
- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Security with JWT**
- **Spring Data JPA**
- **H2 Database (In-Memory)**
- **Maven**

## Project Structure

```
backend/
├── src/main/
│   ├── java/com/loanapp/
│   │   ├── LoanDisbursementApplication.java (Main App)
│   │   ├── config/
│   │   │   └── SecurityConfig.java (JWT & CORS Config)
│   │   ├── controller/
│   │   │   ├── AuthController.java (Register/Login)
│   │   │   ├── LoanController.java (User Loan Operations)
│   │   │   └── AdminController.java (Admin Operations)
│   │   ├── dto/
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── LoanApplicationRequest.java
│   │   │   ├── LoanApplicationResponse.java
│   │   │   ├── StatusUpdateRequest.java
│   │   │   ├── DisbursementResponse.java
│   │   │   └── ApiResponse.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── LoanApplication.java
│   │   │   ├── Disbursement.java
│   │   │   ├── Role.java (Enum)
│   │   │   └── LoanStatus.java (Enum)
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── LoanApplicationRepository.java
│   │   │   └── DisbursementRepository.java
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── JwtAuthenticationEntryPoint.java
│   │   └── service/
│   │       ├── AuthService.java
│   │       ├── LoanService.java
│   │       └── DisbursementService.java
│   └── resources/
│       └── application.properties
└── pom.xml
```

## Entity Relationship Diagram

### Database Schema

**User Entity**
```
- id (PK, Long)
- username (UNIQUE, NOT NULL)
- password (NOT NULL)
- email (UNIQUE, NOT NULL)
- fullName (NOT NULL)
- role (ENUM: USER, ADMIN)
- createdAt (NOT NULL, updatable=false)
- updatedAt (NOT NULL)
```

**LoanApplication Entity**
```
- id (PK, Long)
- user_id (FK → User, NOT NULL)
- amount (DECIMAL, NOT NULL)
- termMonths (INT, NOT NULL)
- purpose (NOT NULL)
- bankAccountNumber (NOT NULL)
- ifscCode (NOT NULL)
- status (ENUM: SUBMITTED, APPROVED, REJECTED, DISBURSED)
- submittedDate (NOT NULL, updatable=false)
- approvedDate (nullable)
- rejectedDate (nullable)
- rejectionReason (nullable)
- disbursedDate (nullable)
- transactionReference (nullable)
```

**Disbursement Entity**
```
- id (PK, Long)
- loan_application_id (FK → LoanApplication, UNIQUE)
- transactionReference (UNIQUE, 12-digit, NOT NULL)
- requestedDate (NOT NULL) - timestamp of submission
- approvedDate (NOT NULL) - timestamp of approval
- disbursedDate (NOT NULL) - timestamp of disbursement
- disbursedByAdmin (NOT NULL) - username of admin
- remarks (nullable)
```

### Relationships
- **User → LoanApplication**: 1:N (One user has many loan applications)
- **LoanApplication → Disbursement**: 1:1 (One loan has one disbursement)

## Setup Instructions

### Prerequisites
- Java 21 or higher
- Maven 3.8+

### Build & Run

1. **Navigate to backend directory**
```bash
cd backend
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Access H2 Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## API Endpoints

### Authentication Endpoints
```
POST /api/auth/register
- Body: { username, password, email, fullName }
- Response: { success, message, data: { username, role, userId } }

POST /api/auth/login
- Body: { username, password }
- Response: { success, message, data: { token, username, role, userId } }
```

### User Loan Endpoints (Authenticated)
```
POST /api/loans/apply
- Headers: Authorization: Bearer {token}
- Body: { amount, termMonths, purpose, bankAccountNumber, ifscCode }
- Response: { success, message, data: LoanApplicationResponse }

GET /api/loans/my-applications
- Headers: Authorization: Bearer {token}
- Response: { success, message, data: [LoanApplicationResponse] }

GET /api/loans/{id}
- Headers: Authorization: Bearer {token}
- Response: { success, message, data: LoanApplicationResponse }
```

### Admin Endpoints (ADMIN role required)
```
GET /api/admin/loans
- Headers: Authorization: Bearer {token}
- Response: { success, message, data: [LoanApplicationResponse] }

GET /api/admin/loans/status/{status}
- Headers: Authorization: Bearer {token}
- Params: status = SUBMITTED|APPROVED|REJECTED|DISBURSED
- Response: { success, message, data: [LoanApplicationResponse] }

PUT /api/admin/loans/{id}/status
- Headers: Authorization: Bearer {token}
- Body: { status: APPROVED|REJECTED, rejectionReason?: string }
- Response: { success, message, data: LoanApplicationResponse }

POST /api/admin/loans/{id}/disburse
- Headers: Authorization: Bearer {token}
- Response: { success, message, data: DisbursementResponse }

GET /api/admin/loans/{id}/disbursement
- Headers: Authorization: Bearer {token}
- Response: { success, message, data: DisbursementResponse }
```

## Security Configuration

### JWT Configuration
- **Algorithm**: HS512
- **Secret Key**: Configured in `application.properties` (jwt.secret)
- **Expiration**: 24 hours (86400000ms)

### CORS Configuration
- **Allowed Origins**: 
  - http://localhost:3000
  - http://localhost:3001
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS, PATCH
- **Credentials**: Enabled

### Endpoint Security
```
Public:
- /api/auth/register
- /api/auth/login
- /h2-console/**

Authenticated:
- /api/loans/**

Admin Only:
- /api/admin/**
```

## Acceptance Criteria Implementation

### 1. User Registration
✅ **Only new users can register**
- Validation in `AuthService.register()` checks if username/email already exists
- Throws exception if duplicate found

### 2. Admin-Only Disbursement Controls
✅ **User with ADMIN role can access disbursement controls**
- `@PreAuthorize("hasRole('ADMIN')")` decorator on `AdminController`
- JWT token includes role claim verified on every request

### 3. Loan Application Validation
✅ **Users cannot submit new loan if they have existing PENDING or APPROVED application**
- Logic in `LoanService.submitLoanApplication()`
- Checks for applications with status SUBMITTED or APPROVED
- Throws exception if found

### 4. Admin Filtering by Status
✅ **Admin can filter applications by status**
- `GET /api/admin/loans/status/{status}` endpoint
- Supports: SUBMITTED, APPROVED, REJECTED, DISBURSED

### 5. Disbursement Logic
✅ **Application can only be DISBURSED if APPROVED**
- Validation in `DisbursementService.disburseAmount()`
- Checks `app.getStatus() == LoanStatus.APPROVED`

✅ **Generate unique 12-digit transactionReference**
- Method: `generateUniqueTransactionReference()`
- Generates random 12-digit number, checks uniqueness in database
- Retries up to 10 times if collision

✅ **Timestamp disbursementDate to exact second**
- Uses `LocalDateTime.now()` for precise second-level accuracy
- Format: `YYYY-MM-DD'T'HH:mm:ss.SSSSS`

✅ **Audit details (requestedDate and approvedDate)**
- `Disbursement` entity stores:
  - `requestedDate`: Captured from `LoanApplication.submittedDate`
  - `approvedDate`: Captured from `LoanApplication.approvedDate`
  - `disbursedDate`: Timestamp of disbursement action

## Testing the System

### Test Scenario 1: User Registration & Login
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123","email":"john@example.com","fullName":"John Doe"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'
```

### Test Scenario 2: Admin Disbursement
```bash
# Get all loans
curl -X GET http://localhost:8080/api/admin/loans \
  -H "Authorization: Bearer {token}"

# Approve a loan
curl -X PUT http://localhost:8080/api/admin/loans/1/status \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"status":"APPROVED"}'

# Disburse
curl -X POST http://localhost:8080/api/admin/loans/1/disburse \
  -H "Authorization: Bearer {token}"
```

## Configuration

### application.properties Key Settings
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=create-drop

# JWT
jwt.secret=your_secret_key_change_this_in_production
jwt.expiration=86400000
```

> ⚠️ **IMPORTANT**: Change `jwt.secret` in production!

## Dependencies
- Spring Boot Starter Web
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (jjwt)
- Lombok
- Validation

## Troubleshooting

**Port 8080 already in use**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

**JWT Token Invalid**
- Ensure token is prefixed with "Bearer "
- Check token expiration
- Verify secret key matches

**CORS Errors**
- Verify frontend is running on allowed origin
- Check browser console for detailed CORS error

## Future Enhancements
- Add email notifications for status updates
- Implement loan approval workflow with multi-level authorization
- Add loan repayment tracking
- Implement audit logging
- Add rate limiting and API throttling
- Implement database encryption for sensitive data
