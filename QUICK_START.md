# Quick Start Guide - Loan Disbursement System

## System Requirements

- **Java**: JDK 21 or higher
- **Maven**: 3.8 or higher
- **Node.js**: 16+ and npm 8+
- **Git**: Optional

## 5-Minute Quick Start

### Step 1: Start Backend (3 minutes)

```bash
# Navigate to backend
cd backend

# Install dependencies & build
mvn clean install

# Start the server
mvn spring-boot:run
```

✅ Backend running on: `http://localhost:8080`

**Verify it's working:**
- Open browser to `http://localhost:8080/h2-console`
- Default credentials: username `sa`, password (empty)

### Step 2: Start Frontend (2 minutes)

```bash
# In a new terminal, navigate to frontend
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

✅ Frontend opens at: `http://localhost:3000`

---

## Testing the System

### Quick Test Scenario

**1. Register as User**
- Go to `http://localhost:3000`
- Click "Register"
- Fill in details:
  - Full Name: John Doe
  - Email: john@example.com
  - Username: john_user
  - Password: Test@1234
- Click "Register"

**2. Login as User**
- Click "Login"
- Username: `john_user`
- Password: `Test@1234`
- Click "Login"

**3. Submit Loan Application**
- Click "Apply for New Loan"
- Fill in:
  - Amount: 100000
  - Term: 12 months
  - Purpose: Business expansion
  - Account: 1234567890
  - IFSC: HDFC0001234
- Click "Submit Application"

**4. Login as Admin**
- Open new incognito/private window
- Go to `http://localhost:3000`
- Click "Login"
- Create admin account first:
  - Go to Register
  - Username: admin_user
  - Password: Admin@1234
  - Email: admin@example.com
  - Full Name: Admin User
  - Click Register
- Now login with: `admin_user` / `Admin@1234`
- You should see "Admin Dashboard"

**5. Approve Loan (Admin)**
- Click on "Submitted" filter
- Find John's loan application
- Click "Approve" button
- Confirm status update

**6. Disburse Amount (Admin)**
- Click on "Approved" filter
- Find John's loan (should show "Approved" status)
- Click "Disburse" button
- Modal appears with confirmation
- Click "Continue" → "Confirm Disbursement"
- See transaction reference: e.g., "384756920183"

**7. Check User Dashboard**
- Switch back to John's account (or login again)
- See loan status changed to "DISBURSED"
- Progress bar at 100%
- Transaction reference visible

---

## API Testing with cURL

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@1234",
    "email": "test@example.com",
    "fullName": "Test User"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "Test@1234"}'
```

**Response includes `token` - copy it for next requests**

### Submit Loan
```bash
curl -X POST http://localhost:8080/api/loans/apply \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000,
    "termMonths": 6,
    "purpose": "Personal use",
    "bankAccountNumber": "9876543210",
    "ifscCode": "ICIC0000001"
  }'
```

### Admin: Get All Loans
```bash
curl -X GET http://localhost:8080/api/admin/loans \
  -H "Authorization: Bearer ADMIN_TOKEN_HERE"
```

### Admin: Filter by Status
```bash
curl -X GET http://localhost:8080/api/admin/loans/status/SUBMITTED \
  -H "Authorization: Bearer ADMIN_TOKEN_HERE"
```

### Admin: Approve Loan
```bash
curl -X PUT http://localhost:8080/api/admin/loans/1/status \
  -H "Authorization: Bearer ADMIN_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"status": "APPROVED"}'
```

### Admin: Disburse Loan
```bash
curl -X POST http://localhost:8080/api/admin/loans/1/disburse \
  -H "Authorization: Bearer ADMIN_TOKEN_HERE"
```

---

## Database Access

### H2 Console

**URL:** `http://localhost:8080/h2-console`

**Credentials:**
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (leave empty)

**Sample Queries:**

```sql
-- View all users
SELECT * FROM users;

-- View all loans
SELECT * FROM loan_applications;

-- View disbursements
SELECT * FROM disbursements;

-- Find loans by status
SELECT * FROM loan_applications WHERE status = 'APPROVED';

-- Count loans by status
SELECT status, COUNT(*) FROM loan_applications GROUP BY status;

-- Find loan with transaction reference
SELECT * FROM disbursements WHERE transaction_reference = '123456789012';
```

---

## Common Issues & Solutions

### Port Already in Use

**Error:** `Port 8080 already in use`

**Solution 1: Use different port**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

**Solution 2: Kill process using port**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux
lsof -ti:8080 | xargs kill -9
```

### CORS Error from Frontend

**Error:** `Access to XMLHttpRequest blocked by CORS policy`

**Solutions:**
1. Ensure backend is running
2. Check backend is on `http://localhost:8080`
3. Clear browser cache
4. Try incognito/private window

### JWT Token Expired

**Error:** `Unauthorized - Valid JWT token required`

**Solution:**
- Login again to get new token
- Tokens expire after 24 hours

### H2 Console Not Accessible

**Error:** Cannot reach `http://localhost:8080/h2-console`

**Solution:**
1. Check backend is running
2. Check no firewall blocking port 8080
3. Verify `spring.h2.console.enabled=true` in `application.properties`

### npm install fails

**Error:** `npm ERR!` or dependency issues

**Solution:**
```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and package-lock
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

---

## Project Structure Overview

```
loan-application/
├── backend/                 ← Java Spring Boot API
│   ├── src/main/java/      ← Java source code
│   ├── pom.xml             ← Maven configuration
│   └── README.md           ← Detailed backend docs
│
├── frontend/                ← React app
│   ├── src/                ← React components
│   ├── public/             ← Static assets
│   ├── package.json        ← npm configuration
│   └── README.md           ← Detailed frontend docs
│
├── README.md               ← Project overview
├── TECHNICAL_SPECIFICATION.md  ← ER diagram & security details
└── QUICK_START.md          ← This file
```

---

## Key Features Checklist

✅ **User Management**
- Register new users
- Login with JWT authentication
- Role-based access (USER/ADMIN)

✅ **Loan Management**
- Submit loan applications
- View personal loan status
- Prevent duplicate pending applications

✅ **Admin Controls**
- View all loan applications
- Filter by status
- Approve/Reject loans
- Disburse with confirmation

✅ **Disbursement**
- Unique 12-digit transaction reference
- Precise timestamp tracking
- Complete audit trail
- Confirmation modal

✅ **Frontend UI**
- Visual progress bar (Submitted → Approved → Disbursed)
- Status badges with color coding
- Responsive design with Tailwind CSS
- Protected routes

---

## Next Steps

1. **Explore the Code**
   - Backend: [backend/README.md](backend/README.md)
   - Frontend: [frontend/README.md](frontend/README.md)

2. **Read Technical Details**
   - [TECHNICAL_SPECIFICATION.md](TECHNICAL_SPECIFICATION.md)

3. **Customize**
   - Change JWT secret in `backend/src/main/resources/application.properties`
   - Update CORS origins for your domain
   - Customize UI styling with Tailwind

4. **Deploy**
   - Backend: Deploy JAR file to server (Heroku, AWS, Azure, etc.)
   - Frontend: Deploy `npm run build` output to CDN
   - Database: Migrate from H2 to PostgreSQL/MySQL

---

## Support

For more details:
- Backend issues: See [backend/README.md](backend/README.md)
- Frontend issues: See [frontend/README.md](frontend/README.md)
- Architecture: See [TECHNICAL_SPECIFICATION.md](TECHNICAL_SPECIFICATION.md)

---

**Happy building! 🚀**

For production deployment, ensure to:
- [ ] Change JWT secret
- [ ] Use secure database (PostgreSQL/MySQL)
- [ ] Enable HTTPS
- [ ] Configure environment-specific settings
- [ ] Set up monitoring and logging
- [ ] Run security audits
