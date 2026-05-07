# End-to-End Loan Disbursement System

Complete implementation of a loan disbursement system with backend (Java Spring Boot) and frontend (React).

## Project Overview

This system provides a comprehensive solution for managing loan applications through a complete workflow:
- **User Registration & Authentication**
- **Loan Application Submission**
- **Admin Approval/Rejection**
- **Automated Disbursement Processing**
- **Transaction Reference Generation & Audit Trail**

## Quick Start

### Backend Setup

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend runs on: `http://localhost:8080`

### Frontend Setup

```bash
cd frontend
npm install
npm start
```

Frontend runs on: `http://localhost:3000`

## System Architecture

### Backend Stack
- Java 21
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data JPA
- H2 Database (In-Memory)

### Frontend Stack
- React 18.2
- React Router v6
- Axios
- Tailwind CSS

## Database Schema

### Entities

**1. User**
```
├── id (Primary Key)
├── username (Unique)
├── email (Unique)
├── password (Encrypted)
├── fullName
├── role (USER | ADMIN)
├── createdAt
└── updatedAt
```

**2. LoanApplication**
```
├── id (Primary Key)
├── user_id (Foreign Key → User)
├── amount
├── termMonths
├── purpose
├── bankAccountNumber
├── ifscCode
├── status (SUBMITTED | APPROVED | REJECTED | DISBURSED)
├── submittedDate
├── approvedDate
├── rejectedDate
├── rejectionReason
├── disbursedDate
└── transactionReference (Unique, 12-digit)
```

**3. Disbursement**
```
├── id (Primary Key)
├── loan_application_id (Foreign Key, Unique)
├── transactionReference (Unique, 12-digit)
├── requestedDate (Audit trail)
├── approvedDate (Audit trail)
├── disbursedDate (Audit trail)
├── disbursedByAdmin
└── remarks
```

### Entity Relationships
```
User
  ├── 1:N → LoanApplication
         ├── 1:1 → Disbursement
```

## API Endpoints

### Public Endpoints
```
POST /api/auth/register          - User registration
POST /api/auth/login             - User login (returns JWT)
GET  /h2-console                 - H2 Database console
```

### Authenticated User Endpoints
```
POST   /api/loans/apply          - Submit new loan
GET    /api/loans/my-applications - Get user's loans
GET    /api/loans/{id}           - Get specific loan details
```

### Admin-Only Endpoints
```
GET    /api/admin/loans          - View all loans
GET    /api/admin/loans/status/{status} - Filter by status
PUT    /api/admin/loans/{id}/status    - Approve/Reject
POST   /api/admin/loans/{id}/disburse  - Process disbursement
GET    /api/admin/loans/{id}/disbursement - Get disbursement details
```

## User Interface

### Authentication Pages
- **Register**: Create new account
- **Login**: Authenticate and receive JWT token

### User Dashboard
- Submit new loan applications
- View personal loan applications
- Track application status with visual progress bar
- View transaction references and disbursement dates

### Admin Dashboard
- View all loan applications
- Filter applications by status
- Approve or reject applications with reason
- Process disbursement for approved loans
- Two-step confirmation modal to prevent accidental processing
- View complete audit trail

## Security Features

### Authentication & Authorization
- JWT token-based authentication
- Role-based access control (RBAC)
- Password encryption using BCrypt
- Token expiration: 24 hours
- CORS enabled for frontend

### Validation
- Input validation on all endpoints
- Email validation
- Username/Email uniqueness check
- Loan status state machine validation

## Acceptance Criteria Implementation

### ✅ 1. User Registration
- Only new users can register
- Validates username and email uniqueness
- Returns error if duplicate found

### ✅ 2. Admin Role Protection
- Only users with ADMIN role can access disbursement controls
- Role checked in JWT token on every request
- Non-admin users get 403 Forbidden

### ✅ 3. Loan Application Validation
- Users cannot submit new loan if they have existing PENDING or APPROVED application
- System checks LoanApplication table for conflicting statuses
- Throws exception with clear message

### ✅ 4. Admin Status Filtering
- `GET /api/admin/loans/status/{status}` endpoint
- Supports: SUBMITTED, APPROVED, REJECTED, DISBURSED
- Returns filtered list of applications

### ✅ 5. Disbursement Logic
**All four sub-criteria implemented:**

**i) Status Validation**
- Application must be APPROVED before disbursement
- System throws exception if attempting to disburse non-approved loan

**ii) Unique 12-Digit Transaction Reference**
- Method: `generateUniqueTransactionReference()`
- Generates random number between 100000000000 and 999999999999
- Checks database for uniqueness
- Retries up to 10 times on collision
- Stored in LoanApplication and Disbursement tables

**iii) Timestamp to Exact Second**
- Uses `LocalDateTime.now()` for precise accuracy
- Stored in `disbursedDate` field
- Format: ISO 8601 with nanosecond precision
- Retrievable via API response

**iv) Audit Details**
- `Disbursement` entity captures:
  - `requestedDate`: When loan was submitted
  - `approvedDate`: When loan was approved by admin
  - `disbursedDate`: When amount was disbursed
  - `disbursedByAdmin`: Which admin processed it

### ✅ 6. Frontend Requirements

**i) Visual Progress Bar (Submitted → Approved → Disbursed)**
- Component: `UserDashboard.jsx`
- Shows percentage completion (25% → 50% → 100%)
- Color-coded gradient background
- Displays milestone labels
- Updates based on loan status

**ii) Confirm Disbursement Modal**
- Component: `AdminDashboard.jsx`
- Two-step confirmation process:
  1. Review loan details with warning message
  2. Confirm disbursement action
- Prevents accidental funding
- Shows transaction reference upon success

## Testing the System

### Scenario 1: User Registration & Loan Application

```bash
# 1. Register new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "secure123",
    "email": "john@example.com",
    "fullName": "John Doe"
  }'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "john", "password": "secure123"}'

# 3. Submit loan application
curl -X POST http://localhost:8080/api/loans/apply \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100000,
    "termMonths": 12,
    "purpose": "Home renovation",
    "bankAccountNumber": "1234567890",
    "ifscCode": "HDFC0001234"
  }'
```

### Scenario 2: Admin Approval & Disbursement

```bash
# 1. Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'

# 2. View all loans
curl -X GET http://localhost:8080/api/admin/loans \
  -H "Authorization: Bearer <admin-token>"

# 3. Approve loan
curl -X PUT http://localhost:8080/api/admin/loans/1/status \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{"status": "APPROVED"}'

# 4. Disburse amount
curl -X POST http://localhost:8080/api/admin/loans/1/disburse \
  -H "Authorization: Bearer <admin-token>"
```

## H2 Database Console

Access and verify database:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Configuration

### Backend (application.properties)
```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
jwt.secret=your_secret_key_change_in_production
jwt.expiration=86400000
```

### Frontend (package.json)
```json
"proxy": "http://localhost:8080"
```

## Security Considerations

### Production Deployment
1. **Change JWT Secret**: Use strong, random secret in production
2. **Enable HTTPS**: Configure SSL/TLS
3. **Database**: Switch from H2 to PostgreSQL/MySQL
4. **Password Policy**: Enforce strong passwords
5. **Rate Limiting**: Add request throttling
6. **CORS Whitelist**: Restrict to known origins only
7. **API Keys**: Implement API key authentication
8. **Audit Logging**: Store all transactions in audit table

## Performance Optimizations
- Database indexing on frequently queried fields
- Lazy loading for associations
- Pagination for large result sets
- Query optimization with proper SELECT statements
- Frontend code splitting and lazy loading
- Tailwind CSS tree-shaking for smaller bundle

## Troubleshooting

### Common Issues

**Backend won't start**
```
Error: Port 8080 already in use
Solution: mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

**CORS Error from Frontend**
```
Check backend SecurityConfig.java for allowed origins
Ensure frontend URL is in CORS whitelist
```

**JWT Token Invalid**
```
Verify Bearer prefix in Authorization header
Check token hasn't expired (24 hours)
Ensure secret key matches between issuer and validator
```

**Loan Application Won't Submit**
```
Check user doesn't have pending/approved application
Verify all required fields filled
Check backend logs for validation errors
```

## File Structure

```
loan-application/
├── backend/
│   ├── src/main/
│   │   ├── java/com/loanapp/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   └── resources/
│   ├── pom.xml
│   └── README.md
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── App.jsx
│   │   ├── index.jsx
│   │   └── index.css
│   ├── public/
│   ├── package.json
│   ├── tailwind.config.js
│   ├── postcss.config.js
│   └── README.md
└── README.md
```

## Key Features Summary

✅ **Complete Authentication System**
- Registration with validation
- JWT-based login
- Role-based authorization

✅ **Loan Management**
- User can submit applications
- Validation prevents duplicate pending/approved loans
- Complete application lifecycle tracking

✅ **Admin Controls**
- Approve/reject applications
- Filter by status for easy management
- Process disbursements with audit trail

✅ **Disbursement Engine**
- Unique transaction references
- Precise timestamp tracking
- Complete audit trail
- Confirmation modal for safety

✅ **Professional UI**
- Responsive design with Tailwind CSS
- Visual progress tracking
- Two-step confirmation dialogs
- Status badges and color coding

## Future Enhancements

1. **Email Notifications**: Auto-send status updates to users
2. **Multi-level Approval**: Implement approval workflows
3. **Loan Repayment**: Track and manage repayments
4. **Analytics Dashboard**: Loan statistics and reports
5. **Document Management**: Upload and store loan documents
6. **Mobile App**: React Native mobile application
7. **Microservices**: Split into microservices architecture
8. **Advanced Security**: 2FA, biometric authentication

## Support & Documentation

For detailed information, see:
- [Backend README](backend/README.md)
- [Frontend README](frontend/README.md)

## License

MIT License - Feel free to use for educational purposes.

## Contact

For questions or issues, please refer to the detailed component documentation in respective README files.
