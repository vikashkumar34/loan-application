# Technical Specification - Loan Disbursement System

## 1. Entity Relationship (ER) Diagram

### Database Tables & Columns

#### TABLE: users
```sql
CREATE TABLE users (
  id                BIGINT PRIMARY KEY AUTO_INCREMENT,
  username          VARCHAR(255) NOT NULL UNIQUE,
  password          VARCHAR(255) NOT NULL,
  email             VARCHAR(255) NOT NULL UNIQUE,
  full_name         VARCHAR(255) NOT NULL,
  role              VARCHAR(50) NOT NULL,
  created_at        TIMESTAMP NOT NULL,
  updated_at        TIMESTAMP NOT NULL
);
```

**Constraints:**
- PK: id
- UK: username, email
- NN: username, password, email, fullName, role, createdAt, updatedAt

**Attributes:**
- `role` - Enum: USER, ADMIN

---

#### TABLE: loan_applications
```sql
CREATE TABLE loan_applications (
  id                      BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id                 BIGINT NOT NULL,
  amount                  DECIMAL(15, 2) NOT NULL,
  term_months             INT NOT NULL,
  purpose                 VARCHAR(500) NOT NULL,
  bank_account_number     VARCHAR(50) NOT NULL,
  ifsc_code               VARCHAR(20) NOT NULL,
  status                  VARCHAR(50) NOT NULL,
  submitted_date          TIMESTAMP NOT NULL,
  approved_date           TIMESTAMP,
  rejected_date           TIMESTAMP,
  rejection_reason        VARCHAR(500),
  disbursed_date          TIMESTAMP,
  transaction_reference   VARCHAR(12),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**Constraints:**
- PK: id
- FK: user_id → users(id)
- UK: none (multiple loans per user allowed)
- NN: user_id, amount, termMonths, purpose, bankAccountNumber, ifscCode, status, submittedDate

**Attributes:**
- `status` - Enum: SUBMITTED, APPROVED, REJECTED, DISBURSED
- `amount` - DECIMAL for financial precision
- `termMonths` - INT representing months
- `transactionReference` - 12-digit unique identifier (null until disbursed)

---

#### TABLE: disbursements
```sql
CREATE TABLE disbursements (
  id                      BIGINT PRIMARY KEY AUTO_INCREMENT,
  loan_application_id     BIGINT NOT NULL UNIQUE,
  transaction_reference   VARCHAR(12) NOT NULL UNIQUE,
  requested_date          TIMESTAMP NOT NULL,
  approved_date           TIMESTAMP NOT NULL,
  disbursed_date          TIMESTAMP NOT NULL,
  disbursed_by_admin      VARCHAR(255) NOT NULL,
  remarks                 VARCHAR(500),
  FOREIGN KEY (loan_application_id) REFERENCES loan_applications(id),
  UNIQUE KEY (transaction_reference)
);
```

**Constraints:**
- PK: id
- FK: loan_application_id → loan_applications(id) [UNIQUE - 1:1 relationship]
- UK: loan_application_id, transaction_reference
- NN: loanApplicationId, transactionReference, requestedDate, approvedDate, disbursedDate, disbursedByAdmin

**Attributes:**
- `requestedDate` - Captures when loan was submitted
- `approvedDate` - Captures when loan was approved
- `disbursedDate` - Captures when disbursement was processed (to exact second)
- `disbursedByAdmin` - Username of admin who processed disbursement
- `transactionReference` - 12-digit unique reference number

---

### Relationship Diagram

```
┌─────────────────────┐
│       users         │
├─────────────────────┤
│ id (PK)             │
│ username (UK)       │
│ email (UK)          │
│ password            │
│ full_name           │
│ role (ENUM)         │
│ created_at          │
│ updated_at          │
└──────┬──────────────┘
       │
       │ 1:N
       │
       ▼
┌──────────────────────────────┐
│   loan_applications          │
├──────────────────────────────┤
│ id (PK)                      │
│ user_id (FK)                 │
│ amount                       │
│ term_months                  │
│ purpose                      │
│ bank_account_number          │
│ ifsc_code                    │
│ status (ENUM)                │
│ submitted_date               │
│ approved_date (nullable)     │
│ rejected_date (nullable)     │
│ rejection_reason (nullable)  │
│ disbursed_date (nullable)    │
│ transaction_reference (NK)   │
└──────┬───────────────────────┘
       │
       │ 1:1
       │
       ▼
┌────────────────────────────┐
│     disbursements          │
├────────────────────────────┤
│ id (PK)                    │
│ loan_application_id (FK,UK)│
│ transaction_reference (UK) │
│ requested_date (audit)     │
│ approved_date (audit)      │
│ disbursed_date (audit)     │
│ disbursed_by_admin         │
│ remarks (nullable)         │
└────────────────────────────┘
```

### Relationship Details

**1. User → LoanApplication (1:N)**
- One user can have multiple loan applications
- Each application belongs to exactly one user
- ON DELETE CASCADE: Delete user → cascades to all their applications
- ON DELETE CASCADE: Delete application → cascades to disbursement record

**2. LoanApplication → Disbursement (1:1)**
- One application can have at most one disbursement record
- Disbursement is created only after approval and disbursement action
- One-to-one enforced through UNIQUE constraint on loan_application_id

---

## 2. Security Configuration Details

### 2.1 JWT (JSON Web Token) Configuration

#### Token Structure
```
Header: {
  "alg": "HS512",
  "typ": "JWT"
}

Payload: {
  "sub": "username",
  "role": "USER|ADMIN",
  "iat": 1609459200,
  "exp": 1609545600
}

Signature: HMACSHA512(Base64(header) + "." + Base64(payload), secret)
```

#### Token Parameters
- **Algorithm**: HS512 (HMAC with SHA-512)
- **Secret Key**: Configurable in `application.properties` (jwt.secret)
- **Expiration**: 24 hours (86400000 milliseconds)
- **Claims**: 
  - `sub` (subject): username
  - `role`: User's role (USER/ADMIN)
  - `iat` (issued at): Timestamp
  - `exp` (expiration): Timestamp

#### Token Generation (JwtTokenProvider)
```java
public String generateToken(UserDetails userDetails) {
  Map<String, Object> claims = new HashMap<>();
  claims.put("role", userDetails.getAuthorities()
    .stream()
    .findFirst()
    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
    .orElse("USER"));
  return createToken(claims, userDetails.getUsername());
}
```

#### Token Validation
```java
public boolean validateToken(String token) {
  try {
    Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token);
    return true;
  } catch (Exception e) {
    return false; // Expired, invalid, or tampered
  }
}
```

#### Token Usage
```
Request Header:
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGUiOiJVU0VSIn0...
```

---

### 2.2 Authentication Flow

#### Registration Flow
```
1. User submits: { username, password, email, fullName }
   ↓
2. AuthService.register()
   - Check username uniqueness
   - Check email uniqueness
   - Hash password with BCrypt
   - Create User entity with role=USER
   - Save to database
   ↓
3. Return: { success, message, userId, role }
```

#### Login Flow
```
1. User submits: { username, password }
   ↓
2. AuthService.login()
   - Fetch user by username
   - Verify password with BCrypt
   - Generate JWT token using JwtTokenProvider
   - Return token + user details
   ↓
3. User stores token in localStorage
   ↓
4. All subsequent requests include:
   Authorization: Bearer {token}
```

#### Authorization Flow
```
1. Request arrives with Authorization header
   ↓
2. JwtAuthenticationFilter
   - Extract JWT from header
   - Validate token signature and expiration
   - Extract username and role from claims
   ↓
3. Create UsernamePasswordAuthenticationToken
   - Principal: username
   - Authorities: [ROLE_USER] or [ROLE_ADMIN]
   ↓
4. Set in SecurityContext
   ↓
5. Controller method @PreAuthorize checks authority
   - If passes: execute method
   - If fails: return 403 Forbidden
```

---

### 2.3 CORS Configuration

#### Allowed Origins
```
http://localhost:3000    (React development server)
http://localhost:3001    (Alternative port)
```

#### Allowed Methods
```
GET, POST, PUT, DELETE, OPTIONS, PATCH
```

#### Allowed Headers
```
Authorization
Content-Type
*               (All custom headers)
```

#### Configuration Code
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
  CorsConfiguration configuration = new CorsConfiguration();
  configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",
    "http://localhost:3001"
  ));
  configuration.setAllowedMethods(Arrays.asList(
    "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
  ));
  configuration.setAllowedHeaders(Arrays.asList("*"));
  configuration.setAllowCredentials(true);
  configuration.setMaxAge(3600L);
  
  UrlBasedCorsConfigurationSource source = 
    new UrlBasedCorsConfigurationSource();
  source.registerCorsConfiguration("/**", configuration);
  return source;
}
```

---

### 2.4 Endpoint Security Configuration

#### Public Endpoints (No Authentication)
```
POST    /api/auth/register          [PERMIT_ALL]
POST    /api/auth/login             [PERMIT_ALL]
GET     /h2-console/**              [PERMIT_ALL]
```

#### Authenticated Endpoints (USER or ADMIN)
```
POST    /api/loans/apply            [AUTHENTICATED]
GET     /api/loans/my-applications  [AUTHENTICATED]
GET     /api/loans/{id}             [AUTHENTICATED]
```

#### Admin-Only Endpoints
```
GET     /api/admin/loans            [ROLE_ADMIN]
GET     /api/admin/loans/status/*   [ROLE_ADMIN]
PUT     /api/admin/loans/{id}/status [ROLE_ADMIN]
POST    /api/admin/loans/{id}/disburse [ROLE_ADMIN]
GET     /api/admin/loans/{id}/disbursement [ROLE_ADMIN]
```

#### Security Configuration Code
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  http
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(authz -> authz
      .requestMatchers("/api/auth/register").permitAll()
      .requestMatchers("/api/auth/login").permitAll()
      .requestMatchers("/h2-console/**").permitAll()
      .requestMatchers("/api/loans/**").authenticated()
      .requestMatchers("/api/admin/**").hasRole("ADMIN")
      .anyRequest().authenticated()
    )
    .sessionManagement(session -> 
      session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );
  
  http.addFilterBefore(
    jwtAuthenticationFilter,
    UsernamePasswordAuthenticationFilter.class
  );
  
  return http.build();
}
```

---

### 2.5 Password Security

#### Hashing Algorithm
```
Algorithm: BCrypt
Strength: 10 (cost factor)
Format: $2a$10$salt$hash
```

#### Implementation
```java
@Bean
public PasswordEncoder passwordEncoder() {
  return new BCryptPasswordEncoder();
}

// During registration
user.setPassword(passwordEncoder.encode(rawPassword));

// During login
if (passwordEncoder.matches(rawPassword, user.getPassword())) {
  // Authentication successful
}
```

---

### 2.6 Exception Handling

#### JwtAuthenticationEntryPoint
```java
@Component
public class JwtAuthenticationEntryPoint 
  implements AuthenticationEntryPoint {
  
  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) 
    throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", 401);
    body.put("error", "Unauthorized");
    body.put("message", "Valid JWT token required");
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), body);
  }
}
```

**Response:**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Unauthorized access - Valid JWT token required",
  "path": "/api/admin/loans"
}
```

---

## 3. Disbursement Service Logic

### 3.1 Transaction Reference Generation

#### Algorithm
```java
private String generateUniqueTransactionReference() throws Exception {
  String transactionRef;
  int maxAttempts = 10;
  int attempts = 0;

  do {
    // Generate 12-digit random number
    long randomNum = 100000000000L + 
                     new Random().nextLong(900000000000L);
    transactionRef = String.valueOf(randomNum);
    attempts++;

    if (attempts >= maxAttempts) {
      throw new Exception("Failed to generate unique reference");
    }
  } while (disbursementRepository
    .findByTransactionReference(transactionRef)
    .isPresent());  // Check uniqueness in DB

  return transactionRef;
}
```

#### Properties
- **Length**: Exactly 12 digits
- **Format**: Numeric only (0-9)
- **Range**: 100000000000 to 999999999999
- **Uniqueness**: Database check on every generation
- **Retry Logic**: Up to 10 attempts to find unique value
- **Collision Probability**: ~1 in 900 billion (negligible)

### 3.2 Disbursement Processing

#### State Machine
```
LoanApplication Status Flow:
SUBMITTED → (approved) → APPROVED → (disbursed) → DISBURSED
         ↘ (rejected)  ↗ REJECTED

Constraints:
1. Can only disburse if status = APPROVED
2. Cannot change status if already DISBURSED or REJECTED
3. Cannot disburse same loan twice
```

#### Processing Logic
```java
public DisbursementResponse disburseAmount(
    Long loanApplicationId, 
    String adminUsername) throws Exception {
  
  // 1. Fetch loan application
  LoanApplication app = loanApplicationRepository
    .findById(loanApplicationId)
    .orElseThrow(() -> new Exception("Loan not found"));

  // 2. Validate current status = APPROVED
  if (app.getStatus() != LoanStatus.APPROVED) {
    throw new Exception(
      "Cannot disburse. Current status: " + app.getStatus()
    );
  }

  // 3. Check not already disbursed
  if (disbursementRepository
    .findByLoanApplicationId(loanApplicationId).isPresent()) {
    throw new Exception("Loan already disbursed");
  }

  // 4. Generate unique 12-digit transaction reference
  String transactionReference = 
    generateUniqueTransactionReference();

  // 5. Create disbursement with precise timestamps
  LocalDateTime disbursedDateNow = LocalDateTime.now();
  Disbursement disbursement = Disbursement.builder()
    .loanApplication(app)
    .transactionReference(transactionReference)
    .requestedDate(app.getSubmittedDate())      // Audit: submission
    .approvedDate(app.getApprovedDate())        // Audit: approval
    .disbursedDate(disbursedDateNow)            // Exact second
    .disbursedByAdmin(adminUsername)
    .build();

  // 6. Save disbursement
  Disbursement saved = disbursementRepository.save(disbursement);

  // 7. Update loan application
  app.setStatus(LoanStatus.DISBURSED);
  app.setDisbursedDate(disbursedDateNow);
  app.setTransactionReference(transactionReference);
  loanApplicationRepository.save(app);

  // 8. Return response with all details
  return mapToResponse(saved);
}
```

### 3.3 Audit Trail

#### Audit Data Captured

| Field | Source | Purpose |
|-------|--------|---------|
| requestedDate | LoanApplication.submittedDate | When user submitted |
| approvedDate | LoanApplication.approvedDate | When admin approved |
| disbursedDate | LocalDateTime.now() | Exact disbursement time |
| disbursedByAdmin | Authentication.getName() | Who processed it |
| transactionReference | Generated | Transaction identifier |

#### Timestamp Format
```
ISO 8601: 2024-01-15T14:30:45.123456
Precision: Nanoseconds
Timezone: System default (UTC recommended in production)
```

#### Audit Query Example
```java
// Get complete disbursement audit trail
Optional<Disbursement> disbursement = 
  disbursementRepository.findByLoanApplicationId(loanId);

if (disbursement.isPresent()) {
  Disbursement d = disbursement.get();
  
  System.out.println("Submitted at: " + d.getRequestedDate());
  System.out.println("Approved at: " + d.getApprovedDate());
  System.out.println("Disbursed at: " + d.getDisbursedDate());
  System.out.println("Approved by: " + d.getDisbursedByAdmin());
  System.out.println("Ref: " + d.getTransactionReference());
}
```

---

## 4. Request/Response Examples

### 4.1 Register Request/Response

**Request:**
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123!",
  "email": "john@example.com",
  "fullName": "John Doe"
}
```

**Response (Success - 201):**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "username": "john_doe",
    "role": "USER",
    "userId": 1,
    "message": "Registration successful"
  }
}
```

**Response (Error - 400):**
```json
{
  "success": false,
  "message": "Username already exists",
  "data": null
}
```

---

### 4.2 Disbursement Request/Response

**Request:**
```http
POST /api/admin/loans/1/disburse
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "Disbursement processed successfully",
  "data": {
    "id": 1,
    "loanApplicationId": 1,
    "transactionReference": "345678901234",
    "requestedDate": "2024-01-10T10:15:30.123456",
    "approvedDate": "2024-01-12T14:45:20.654321",
    "disbursedDate": "2024-01-15T09:30:45.987654",
    "disbursedByAdmin": "admin_user",
    "remarks": null
  }
}
```

**Response (Error - 400):**
```json
{
  "success": false,
  "message": "Loan application must be APPROVED before disbursement",
  "data": null
}
```

---

## 5. Performance Considerations

### Database Optimization

**Indexes:**
```sql
-- User lookups
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);

-- Loan lookups
CREATE INDEX idx_loan_user_id ON loan_applications(user_id);
CREATE INDEX idx_loan_status ON loan_applications(status);
CREATE INDEX idx_loan_submitted_date ON loan_applications(submitted_date);

-- Disbursement lookups
CREATE INDEX idx_disbursement_loan_id ON disbursements(loan_application_id);
CREATE INDEX idx_disbursement_tx_ref ON disbursements(transaction_reference);
```

### Query Optimization

```java
// Good: Specify only needed fields
@Query("SELECT new com.loanapp.dto.LoanApplicationResponse(...) " +
       "FROM LoanApplication WHERE status = ?1")
List<LoanApplicationResponse> findByStatus(LoanStatus status);

// Avoid: Loading unnecessary associations
@Query("SELECT la FROM LoanApplication la " +
       "LEFT JOIN FETCH la.user WHERE la.status = ?1")
List<LoanApplication> findByStatusEager(LoanStatus status);
```

### Pagination (Future Enhancement)

```java
Page<LoanApplication> findByStatus(
  LoanStatus status,
  Pageable pageable
);

// Usage
Pageable page = PageRequest.of(0, 20, 
  Sort.by("submittedDate").descending());
```

---

## 6. Compliance & Standards

- **Data Protection**: BCrypt password hashing
- **API Standards**: RESTful, JSON format
- **JWT Standards**: RFC 7519 compliant
- **Error Handling**: Consistent HTTP status codes
- **Timestamp**: ISO 8601 format
- **Decimal Precision**: 2 decimal places for currency

---

## 7. Testing Checklist

- [ ] User registration uniqueness validation
- [ ] Password encryption verification
- [ ] JWT token generation and validation
- [ ] Role-based access control
- [ ] Loan application duplicate prevention
- [ ] Status transition validation
- [ ] Transaction reference uniqueness
- [ ] Timestamp precision
- [ ] Audit trail completeness
- [ ] CORS functionality
- [ ] H2 console access
- [ ] Error message clarity

---

## 8. Deployment Checklist

- [ ] Change JWT secret in `application.properties`
- [ ] Switch database from H2 to production (PostgreSQL/MySQL)
- [ ] Enable HTTPS/SSL
- [ ] Update CORS origins to production domains
- [ ] Configure logging levels
- [ ] Set up monitoring and alerting
- [ ] Enable audit logging to persistent storage
- [ ] Run database migrations
- [ ] Test all endpoints with production configuration
