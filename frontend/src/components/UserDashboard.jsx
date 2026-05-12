import React, { useState, useEffect } from 'react';
import axios from '../api/axios';
import { FaQuestionCircle } from 'react-icons/fa';

export default function UserDashboard() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    jobStatus: '',
    annualIncome: '',
    cibilScore: '',
    loanType: '',
    amount: '',
    termMonths: '',
    purpose: '',
    bankAccountNumber: '',
    ifscCode: ''
  });
  const [interestRate, setInterestRate] = useState(null);

  useEffect(() => {
    fetchApplications();
  }, []);

  useEffect(() => {
    const { jobStatus, annualIncome, cibilScore, loanType } = formData;
    if (jobStatus === 'Salaried' && loanType && annualIncome && cibilScore) {
      const income = parseFloat(annualIncome);
      const cibil = parseInt(cibilScore);
      let baseRate = 0;

      if (loanType === 'Personal Loan') {
        if (income > 5000000) baseRate = 10;
        else if (income > 4000000) baseRate = 11;
        else baseRate = 12;
      } else if (loanType === 'Home Loan') {
        if (income > 5000000) baseRate = 7;
        else if (income > 4000000) baseRate = 7.5;
        else if (income > 3000000) baseRate = 8;
        else if (income > 2000000) baseRate = 8.5;
        else baseRate = 9;
      } else if (loanType === 'Education Loan') {
        if (income > 5000000) baseRate = 7;
        else if (income > 4000000) baseRate = 7.5;
        else if (income > 3000000) baseRate = 8;
        else baseRate = 8.5;
      }

      if (cibil <= 750) {
        baseRate += 1;
      }

      setInterestRate(baseRate);
    } else {
      setInterestRate(null);
    }
  }, [formData.jobStatus, formData.annualIncome, formData.cibilScore, formData.loanType]);

  const fetchApplications = async () => {
    try {
      const response = await axios.get('/api/loans/my-applications');
      setApplications(response.data || []);
    } catch (err) {
      console.error("Failed to fetch applications", err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('/api/loans/apply', formData);
      alert('Loan application submitted successfully!');
      setShowForm(false);
      fetchApplications();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to submit application.');
    }
  };

  const getStatusColor = (status) => {
    const colors = {
      SUBMITTED: 'bg-yellow-100 text-yellow-800',
      APPROVED: 'bg-green-100 text-green-800',
      REJECTED: 'bg-red-100 text-red-800',
      DISBURSED: 'bg-blue-100 text-blue-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  };

  return (
    <div className="max-w-6xl mx-auto p-6">
      <button onClick={() => setShowForm(!showForm)} className="bg-green-500 text-white font-bold py-2 px-4 rounded mb-6">
        {showForm ? 'Cancel Application' : 'Apply for New Loan'}
      </button>

      {showForm && (
        <form onSubmit={handleSubmit} className="p-6 bg-white rounded-lg shadow-md space-y-4">
          <h2 className="text-2xl font-bold">New Loan Application</h2>

          <select name="jobStatus" value={formData.jobStatus} onChange={handleChange} className="p-2 border rounded w-full">
            <option value="">Select Job Status</option>
            <option value="Salaried">Salaried</option>
            <option value="Self-Employed">Self-Employed</option>
          </select>

          {formData.jobStatus === 'Salaried' && (
            <>
              <input type="number" name="annualIncome" value={formData.annualIncome} onChange={handleChange} placeholder="Annual Income" required className="p-2 border rounded w-full" />
              <div className="relative">
                <input type="number" name="cibilScore" value={formData.cibilScore} onChange={handleChange} placeholder="CIBIL Score" required className="p-2 border rounded w-full" />
                <div className="absolute inset-y-0 right-0 flex items-center pr-3 group">
                  <FaQuestionCircle className="text-gray-400" />
                  <div className="absolute bottom-full right-0 mb-2 w-64 p-2 text-sm text-white bg-black rounded-lg opacity-0 group-hover:opacity-100 transition-opacity">
                    Your CIBIL score is a 3-digit number that represents your creditworthiness. A higher score increases your chances of loan approval.
                  </div>
                </div>
              </div>
            </>
          )}

          {formData.jobStatus && (
            <select name="loanType" value={formData.loanType} onChange={handleChange} className="p-2 border rounded w-full">
              <option value="">Select Loan Type</option>
              <option value="Personal Loan">Personal Loan</option>
              <option value="Home Loan">Home Loan</option>
              <option value="Education Loan">Education Loan</option>
            </select>
          )}

          {interestRate !== null && (
            <div className="p-3 bg-blue-100 text-blue-800 rounded-lg">
              <p className="font-semibold">Calculated Interest Rate: <span className="text-lg">{interestRate.toFixed(2)}%</span></p>
            </div>
          )}

          <input type="number" name="amount" value={formData.amount} onChange={handleChange} placeholder="Loan Amount" required className="p-2 border rounded w-full" />
          <input type="number" name="termMonths" value={formData.termMonths} onChange={handleChange} placeholder="Term (Months)" required className="p-2 border rounded w-full" />
          <textarea name="purpose" value={formData.purpose} onChange={handleChange} placeholder="Purpose of Loan" required className="p-2 border rounded w-full" />
          <input type="text" name="bankAccountNumber" value={formData.bankAccountNumber} onChange={handleChange} placeholder="Bank Account Number" required className="p-2 border rounded w-full" />
          <input type="text" name="ifscCode" value={formData.ifscCode} onChange={handleChange} placeholder="IFSC Code" required className="p-2 border rounded w-full" />

          <button type="submit" className="bg-blue-600 text-white font-bold py-2 px-4 rounded">Submit Application</button>
        </form>
      )}

      <div className="mt-8">
        <h2 className="text-2xl font-bold mb-4">My Loan Applications</h2>
        {loading ? <p>Loading applications...</p> : (
          <div className="space-y-4">
            {applications.map(app => (
              <div key={app.id} className="p-4 border rounded-lg bg-white shadow">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-bold text-lg">Loan ID: {app.id}</h3>
                    <p className="text-gray-600">Amount: ₹{app.amount}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-sm font-bold ${getStatusColor(app.status)}`}>
                    {app.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
