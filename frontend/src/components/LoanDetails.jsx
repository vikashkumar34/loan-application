import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from '../api/axios';

export default function LoanDetails() {
  const { id } = useParams();
  const [loan, setLoan] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchLoanDetails = async () => {
      try {
        const response = await axios.get(`/api/loans/${id}`);
        if (response.data && response.data.success) {
          setLoan(response.data.data);
        } else {
          // This will be triggered if the API returns success:false
          setError(response.data.message || `Loan application with ID ${id} not found.`);
        }
      } catch (err) {
        // This will be triggered for 404s or other network errors
        setError(err.response?.data?.message || `Could not find loan application with ID ${id}.`);
      } finally {
        setLoading(false);
      }
    };

    fetchLoanDetails();
  }, [id]);

  if (loading) {
    return (
      <div className="p-8 bg-white rounded-lg shadow-md max-w-4xl mx-auto text-center">
        <p>Loading loan details...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-8 bg-white rounded-lg shadow-md max-w-4xl mx-auto text-center">
        <h2 className="text-2xl font-bold text-red-600 mb-4">Error</h2>
        <p className="text-gray-600 mb-6">{error}</p>
        <Link to="/request-history" className="text-blue-500 hover:underline">
          Return to Request History
        </Link>
      </div>
    );
  }

  if (!loan) {
    return (
      <div className="p-8 bg-white rounded-lg shadow-md max-w-4xl mx-auto text-center">
        <p>No loan details found.</p>
      </div>
    );
  }

  return (
    <div className="p-8 bg-white rounded-lg shadow-md max-w-4xl mx-auto">
      <h2 className="text-3xl font-bold text-gray-800 mb-6">Loan Application Details</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="space-y-4">
          <p><strong>Loan ID:</strong> {loan.id}</p>
          <p><strong>Applicant Name:</strong> {loan.userFullName}</p>
          <p><strong>Amount:</strong> ₹{loan.amount}</p>
          <p><strong>Term:</strong> {loan.termMonths} months</p>
          <p><strong>Purpose:</strong> {loan.purpose}</p>
        </div>
        <div className="space-y-4">
          <p><strong>Status:</strong> <span className="font-semibold">{loan.status}</span></p>
          <p><strong>Submitted On:</strong> {new Date(loan.submittedDate).toLocaleDateString()}</p>
          {loan.approvedDate && <p><strong>Approved On:</strong> {new Date(loan.approvedDate).toLocaleDateString()}</p>}
          {loan.disbursedDate && <p><strong>Disbursed On:</strong> {new Date(loan.disbursedDate).toLocaleDateString()}</p>}
          {loan.transactionReference && <p><strong>Transaction Ref:</strong> {loan.transactionReference}</p>}
        </div>
      </div>
    </div>
  );
}
