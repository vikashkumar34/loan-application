import React, { useState, useEffect } from 'react';
import axios from '../api/axios'; // Corrected import
import { useNavigate } from 'react-router-dom';

export default function UserDashboard() {
  const navigate = useNavigate();
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    amount: '',
    termMonths: '',
    purpose: '',
    bankAccountNumber: '',
    ifscCode: ''
  });

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
    fetchApplications();
  }, []);

  const fetchApplications = async () => {
    try {
      const response = await axios.get('/api/loans/my-applications');
      setApplications(response.data.data || []);
    } catch (err) {
      setError('Failed to load applications');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmitLoan = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...formData,
        amount: parseFloat(formData.amount),
        termMonths: parseInt(formData.termMonths)
      };

      const response = await axios.post('/api/loans/apply', payload);

      if (response.data.success) {
        alert('Loan application submitted successfully');
        setFormData({
          amount: '',
          termMonths: '',
          purpose: '',
          bankAccountNumber: '',
          ifscCode: ''
        });
        setShowForm(false);
        fetchApplications();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to submit application');
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
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

  const getProgressPercentage = (status) => {
    const progress = {
      SUBMITTED: 25,
      APPROVED: 50,
      REJECTED: 0,
      DISBURSED: 100
    };
    return progress[status] || 0;
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <div className="bg-blue-600 text-white p-4 shadow-lg">
        <div className="max-w-6xl mx-auto flex justify-between items-center">
          <h1 className="text-2xl font-bold">Loan Dashboard</h1>
          <button
            onClick={handleLogout}
            className="bg-red-500 hover:bg-red-600 px-4 py-2 rounded"
          >
            Logout
          </button>
        </div>
      </div>

      <div className="max-w-6xl mx-auto p-6">
        {/* Button to Open Form */}
        <div className="mb-6">
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-6 rounded-lg transition"
          >
            {showForm ? 'Cancel' : 'Apply for New Loan'}
          </button>
        </div>

        {/* Loan Application Form */}
        {showForm && (
          <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
            <h2 className="text-2xl font-bold mb-4">New Loan Application</h2>
            <form onSubmit={handleSubmitLoan}>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-gray-700 font-bold mb-2">Loan Amount</label>
                  <input
                    type="number"
                    name="amount"
                    value={formData.amount}
                    onChange={handleInputChange}
                    required
                    step="0.01"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    placeholder="Enter amount"
                  />
                </div>

                <div>
                  <label className="block text-gray-700 font-bold mb-2">Term (Months)</label>
                  <input
                    type="number"
                    name="termMonths"
                    value={formData.termMonths}
                    onChange={handleInputChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    placeholder="Enter term in months"
                  />
                </div>

                <div className="md:col-span-2">
                  <label className="block text-gray-700 font-bold mb-2">Purpose</label>
                  <textarea
                    name="purpose"
                    value={formData.purpose}
                    onChange={handleInputChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    placeholder="Enter loan purpose"
                    rows="3"
                  />
                </div>

                <div>
                  <label className="block text-gray-700 font-bold mb-2">Bank Account Number</label>
                  <input
                    type="text"
                    name="bankAccountNumber"
                    value={formData.bankAccountNumber}
                    onChange={handleInputChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    placeholder="Enter account number"
                  />
                </div>

                <div>
                  <label className="block text-gray-700 font-bold mb-2">IFSC Code</label>
                  <input
                    type="text"
                    name="ifscCode"
                    value={formData.ifscCode}
                    onChange={handleInputChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    placeholder="Enter IFSC code"
                  />
                </div>
              </div>

              <button
                type="submit"
                className="mt-4 bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-6 rounded-lg transition"
              >
                Submit Application
              </button>
            </form>
          </div>
        )}

        {/* Applications List */}
        <div className="bg-white rounded-lg shadow-lg p-6">
          <h2 className="text-2xl font-bold mb-4">My Loan Applications</h2>
          
          {loading ? (
            <p className="text-gray-500">Loading...</p>
          ) : applications.length === 0 ? (
            <p className="text-gray-500">No loan applications found</p>
          ) : (
            <div className="space-y-4">
              {applications.map(app => (
                <div key={app.id} className="border border-gray-300 rounded-lg p-4">
                  <div className="flex justify-between items-start mb-3">
                    <div>
                      <h3 className="font-bold text-lg">Loan ID: {app.id}</h3>
                      <p className="text-gray-600">Amount: ₹{app.amount}</p>
                      <p className="text-gray-600">Term: {app.termMonths} months</p>
                      <p className="text-gray-600">Purpose: {app.purpose}</p>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-sm font-bold ${getStatusColor(app.status)}`}>
                      {app.status}
                    </span>
                  </div>

                  {/* Progress Bar */}
                  <div className="mb-3">
                    <div className="flex justify-between text-sm text-gray-600 mb-1">
                      <span>Progress</span>
                      <span>{getProgressPercentage(app.status)}%</span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-3">
                      <div
                        className="bg-gradient-to-r from-yellow-400 via-green-400 to-blue-500 h-3 rounded-full transition-all duration-300"
                        style={{ width: `${getProgressPercentage(app.status)}%` }}
                      ></div>
                    </div>
                    <div className="flex justify-between text-xs text-gray-500 mt-1">
                      <span>Submitted</span>
                      <span>Approved</span>
                      <span>Disbursed</span>
                    </div>
                  </div>

                  <div className="text-sm text-gray-500">
                    <p>Submitted: {new Date(app.submittedDate).toLocaleDateString()}</p>
                    {app.approvedDate && <p>Approved: {new Date(app.approvedDate).toLocaleDateString()}</p>}
                    {app.disbursedDate && (
                      <>
                        <p>Disbursed: {new Date(app.disbursedDate).toLocaleDateString()}</p>
                        <p>Transaction Ref: {app.transactionReference}</p>
                      </>
                    )}
                    {app.rejectedDate && <p>Rejected: {new Date(app.rejectedDate).toLocaleDateString()}</p>}
                    {app.rejectionReason && <p>Reason: {app.rejectionReason}</p>}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
