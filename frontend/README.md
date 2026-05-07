# Loan Disbursement System - Frontend

## Overview
React-based frontend for the Loan Disbursement System with Tailwind CSS styling. Includes protected routes, user dashboard, and admin management interface.

## Tech Stack
- **React 18.2.0**
- **React Router v6**
- **Axios** (HTTP client)
- **Tailwind CSS 3.4**
- **Node.js & npm**

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── Register.jsx (User Registration)
│   │   ├── Login.jsx (Authentication)
│   │   ├── UserDashboard.jsx (User Loan Management)
│   │   ├── AdminDashboard.jsx (Admin Controls)
│   │   └── ProtectedRoute.jsx (Route Protection)
│   ├── App.jsx (Main App with Routing)
│   ├── index.jsx (Entry Point)
│   └── index.css (Tailwind Styles)
├── package.json
├── tailwind.config.js
├── postcss.config.js
└── README.md
```

## Features

### 1. Authentication
- **Registration**: New user signup with validation
- **Login**: JWT token-based authentication
- **Token Storage**: Stored in localStorage for persistent sessions
- **Role-Based**: Routes protect based on user role (USER/ADMIN)

### 2. User Dashboard
✅ **Protected Route for User Dashboard**
- Only accessible with USER role
- Displays all loan applications submitted by user
- Shows application status with progress bar

✅ **Visual Progress Bar (Submitted → Approved → Disbursed)**
- Color-coded progress visualization
- Shows percentage completion
- Displays milestone labels

✅ **Loan Application Form**
- Fields: Amount, Term, Purpose, Bank Account, IFSC Code
- Form validation
- Real-time error messages

✅ **Application Status Display**
- Status badge with color coding
- Submitted, Approved, Rejected, Disbursed states
- Audit dates (submitted, approved, disbursed)
- Transaction reference (when disbursed)

### 3. Admin Dashboard
✅ **Admin-Only Route**
- Only accessible with ADMIN role
- Redirects to login if not admin

✅ **View All Loan Applications**
- Table view of all submitted loans
- Sortable by submission date
- Displays: ID, User, Amount, Status, Date

✅ **Filter by Status**
- Quick filter buttons: All, Submitted, Approved, Disbursed, Rejected
- Updates table in real-time
- Shows filtered count

✅ **Status Management**
- Approve submitted applications
- Reject with reason
- Inline actions per application

✅ **Disbursement Controls**
- "Disburse" button for approved loans
- **Confirm Disbursement Modal** to prevent accidental funding
- Two-step confirmation process
- Displays transaction reference upon success

## Setup Instructions

### Prerequisites
- Node.js 16+ and npm 8+
- Backend running on `http://localhost:9000`

### Installation

1. **Navigate to frontend directory**
```bash
cd frontend
```

2. **Install dependencies**
```bash
npm install
```

3. **Start development server**
```bash
npm start
```

The frontend will open at `http://localhost:3000`

## Running in Production

```bash
npm run build
```

This creates an optimized build in the `build/` directory.

## Component Details

### Register Component (`Register.jsx`)
- Form fields: Full Name, Email, Username, Password
- Validation on submit
- Shows error messages
- Redirects to login on success

### Login Component (`Login.jsx`)
- Form fields: Username, Password
- Stores JWT token, role, userId to localStorage
- Role-based redirect:
  - ADMIN → `/admin/dashboard`
  - USER → `/user/dashboard`
- Shows error messages

### UserDashboard Component (`UserDashboard.jsx`)
- **Apply for Loan**: 
  - Toggle form visibility
  - Submit with required fields
  - Validation and error handling
  
- **Application List**:
  - Shows all user's loan applications
  - Progress bar visualization
  - Status badges
  - Audit trail (dates and transaction reference)
  
- **Logout**: Clear localStorage and redirect to login

### AdminDashboard Component (`AdminDashboard.jsx`)
- **Loan Management**:
  - View all applications in table format
  - Filter by status (All, Submitted, Approved, Disbursed, Rejected)
  
- **Status Update Modal**:
  - Approve or Reject applications
  - Rejection reason textarea
  - Confirmation before update
  
- **Disbursement Modal**:
  - **Two-Step Confirmation**:
    - Step 1: Review loan details with warning
    - Step 2: Confirm disbursement
  - Shows transaction reference on success
  - Prevents accidental processing
  
- **Logout**: Clear localStorage and redirect to login

### ProtectedRoute Component (`ProtectedRoute.jsx`)
- Checks for JWT token in localStorage
- Validates user role if required
- Redirects to login if not authenticated
- Wraps components that need protection

### App Component (`App.jsx`)
- React Router setup
- Route definitions
- Public routes (Register, Login)
- Protected routes with role verification
- Fallback redirect to login

## Styling

### Tailwind CSS Utility Classes Used
- Color schemes: Blue, Green, Red, Yellow gradients
- Responsive grid layouts
- Flexbox utilities
- Hover and transition effects
- Card and shadow components
- Form styling

### Color Scheme
- **Blue**: Primary action buttons, info
- **Green**: Approve/Success actions
- **Red**: Reject/Danger actions, Logout
- **Yellow**: Submitted/Pending status
- **Purple**: Gradient backgrounds

## API Integration

### Axios Configuration
- Base URL: `http://localhost:9000` (via proxy in package.json)
- Authorization header: `Bearer {token}`
- Content-Type: application/json
- CORS: Enabled on backend

### Key API Calls
```javascript
// Register
POST /api/auth/register

// Login
POST /api/auth/login

// User Applications
GET /api/loans/my-applications
POST /api/loans/apply

// Admin Operations
GET /api/admin/loans
GET /api/admin/loans/status/{status}
PUT /api/admin/loans/{id}/status
POST /api/admin/loans/{id}/disburse
```

## State Management

### localStorage Keys
```javascript
token      // JWT token
role       // USER or ADMIN
userId     // User ID from backend
username   // Username
```

### Component State
- Form data
- Loading states
- Error messages
- Modal visibility
- Application list

## Browser Support
- Chrome/Chromium (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Development Tips

### Enable Debug Logging
Open browser DevTools → Application → LocalStorage to see stored auth data

### API Testing
Use browser Network tab to inspect API calls and responses

### Hot Reload
Changes to components automatically refresh in development mode

## Troubleshooting

### "Backend not reachable"
- Ensure backend is running on `http://localhost:9000`
- Check CORS settings in backend
- Verify frontend proxy in package.json

### "Token expired"
- User needs to login again
- Token is valid for 24 hours

### "Unauthorized" error
- Check token in localStorage
- Verify role matches required role
- Login again if token is invalid

### Forms not submitting
- Check browser console for errors
- Verify all required fields filled
- Ensure network tab shows successful API call

## Accessibility
- Semantic HTML structure
- Form labels for inputs
- Color contrast ratios meet WCAG standards
- Button focus states for keyboard navigation

## Performance Optimizations
- Code splitting via React Router
- Lazy loading of components
- Efficient re-renders with React keys
- CSS optimization with Tailwind

## Future Enhancements
- Add loading skeletons for better UX
- Implement pagination for loan lists
- Add search functionality
- Export loan data to PDF/CSV
- Real-time notifications
- Dark mode support
- Mobile-responsive improvements
- Add analytics dashboard
