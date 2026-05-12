import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Register from './components/Register';
import Login from './components/Login';
import UserDashboard from './components/UserDashboard';
import AdminDashboard from './components/AdminDashboard';
import ProtectedRoute from './components/ProtectedRoute';
import MainLayout from './components/MainLayout';
import Profile from './components/Profile';
import UpdateProfile from './components/UpdateProfile';
import ChangePassword from './components/ChangePassword';
import RequestHistory from './components/RequestHistory';
import KycDetails from './components/KycDetails';
import UserManagement from './components/UserManagement';
import AdminHistory from './components/AdminHistory';
import LoanDetails from './components/LoanDetails';
import AdminKycReview from './components/AdminKycReview'; // Import the new component
import './index.css';

function App() {
  return (
    <Router>
      <Routes>
        {/* Public Routes */}
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />

        {/* Protected User Routes */}
        <Route path="/user/dashboard" element={<ProtectedRoute requiredRole="USER"><MainLayout><UserDashboard /></MainLayout></ProtectedRoute>} />
        <Route path="/profile" element={<ProtectedRoute><MainLayout><Profile /></MainLayout></ProtectedRoute>} />
        <Route path="/update-profile" element={<ProtectedRoute><MainLayout><UpdateProfile /></MainLayout></ProtectedRoute>} />
        <Route path="/change-password" element={<ProtectedRoute><MainLayout><ChangePassword /></MainLayout></ProtectedRoute>} />
        <Route path="/request-history" element={<ProtectedRoute requiredRole="USER"><MainLayout><RequestHistory /></MainLayout></ProtectedRoute>} />
        <Route path="/kyc-details" element={<ProtectedRoute requiredRole="USER"><MainLayout><KycDetails /></MainLayout></ProtectedRoute>} />

        {/* Loan Details Route (accessible by both roles) */}
        <Route path="/loans/:id" element={<ProtectedRoute><MainLayout><LoanDetails /></MainLayout></ProtectedRoute>} />

        {/* Protected Admin Routes */}
        <Route path="/admin/dashboard" element={<ProtectedRoute requiredRole="ADMIN"><MainLayout><AdminDashboard /></MainLayout></ProtectedRoute>} />
        <Route path="/user-management" element={<ProtectedRoute requiredRole="ADMIN"><MainLayout><UserManagement /></MainLayout></ProtectedRoute>} />
        <Route path="/admin-history" element={<ProtectedRoute requiredRole="ADMIN"><MainLayout><AdminHistory /></MainLayout></ProtectedRoute>} />
        <Route path="/admin/kyc-review" element={<ProtectedRoute requiredRole="ADMIN"><MainLayout><AdminKycReview /></MainLayout></ProtectedRoute>} />

        {/* Redirect */}
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;
