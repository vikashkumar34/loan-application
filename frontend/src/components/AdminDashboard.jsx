import React, { useState, useEffect } from 'react';
import axios from '../api/axios';
import { useNavigate } from 'react-router-dom';

export default function AdminDashboard() {
  const navigate = useNavigate();
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [selectedApp, setSelectedApp] = useState(null);
  const [statusUpdate, setStatusUpdate] = useState('');
  const [rejectionReason, setRejectionReason] = useState('');
  const [disbursementModal, setDisbursementModal] = useState(false);
  const [confirmDisburse, setConfirmDisburse] = useState(false);

  useEffect(() => {
    fetchApplications();
  }, []);

  const fetchApplications = async () => {
    try {
      const response = await axios.get('/api/admin/loans');
      setApplications(response.data || []);
    } catch (err) {
      alert('Failed to load applications');
    } finally {
      setLoading(false);
    }
  };

  const fetchByStatus = async (status) => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/admin/loans/status/${status}`);
      setApplications(response.data || []);
      setFilter(status);
    } catch (err) {
      alert('Failed to filter applications');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = (app, status) => {
    setSelectedApp(app);
    setStatusUpdate(status);
    setShowModal(true);
  };

  const submitStatusUpdate = async () => {
    try {
      const payload = {
        status: statusUpdate,
        rejectionReason: rejectionReason || null
      };
      await axios.put(`/api/admin/loans/${selectedApp.id}/status`, payload);
      alert('Status updated successfully');
      setShowModal(false);
      setStatusUpdate('');
      setRejectionReason('');
      fetchApplications();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update status');
    }
  };

  const handleDisburse = (app) => {
    setSelectedApp(app);
    setDisbursementModal(true);
    setConfirmDisburse(false);
  };

  const submitDisburse = async () => {
    try {
      const response = await axios.post(`/api/admin/loans/${selectedApp.id}/disburse`);
      alert('Disbursement processed successfully');
      const disbData = response.data;
      alert(`Transaction Reference: ${disbData.transactionReference}`);
      setDisbursementModal(false);
      setConfirmDisburse(false);
      setSelectedApp(null);
      fetchApplications();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to process disbursement');
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
    <div className="max-w-7xl mx-auto p-6">
      {/* Filter Buttons */}
      <div className="bg-white rounded-lg shadow-lg p-4 mb-6">
        <h2 className="font-bold mb-3">Filter by Status:</h2>
        <div className="flex gap-2 flex-wrap">
          <button onClick={() => { setFilter(''); fetchApplications(); }} className={`px-4 py-2 rounded transition ${filter === '' ? 'bg-blue-500 text-white' : 'bg-gray-200 text-gray-800 hover:bg-gray-300'}`}>All</button>
          <button onClick={() => fetchByStatus('SUBMITTED')} className={`px-4 py-2 rounded transition ${filter === 'SUBMITTED' ? 'bg-yellow-500 text-white' : 'bg-yellow-100 text-yellow-800 hover:bg-yellow-200'}`}>Submitted</button>
          <button onClick={() => fetchByStatus('APPROVED')} className={`px-4 py-2 rounded transition ${filter === 'APPROVED' ? 'bg-green-500 text-white' : 'bg-green-100 text-green-800 hover:bg-green-200'}`}>Approved</button>
          <button onClick={() => fetchByStatus('DISBURSED')} className={`px-4 py-2 rounded transition ${filter === 'DISBURSED' ? 'bg-blue-500 text-white' : 'bg-blue-100 text-blue-800 hover:bg-blue-200'}`}>Disbursed</button>
          <button onClick={() => fetchByStatus('REJECTED')} className={`px-4 py-2 rounded transition ${filter === 'REJECTED' ? 'bg-red-500 text-white' : 'bg-red-100 text-red-800 hover:bg-red-200'}`}>Rejected</button>
        </div>
      </div>

      {/* Applications Table */}
      <div className="bg-white rounded-lg shadow-lg p-6 overflow-x-auto">
        <h2 className="text-2xl font-bold mb-4">Loan Applications {filter && `(${filter})`}</h2>
        {loading ? <p>Loading...</p> : applications.length === 0 ? <p>No applications found</p> : (
          <table className="w-full border-collapse">
            <thead>
              <tr className="bg-gray-200 border-b-2 border-gray-400">
                <th className="p-3 text-left">ID</th>
                <th className="p-3 text-left">User</th>
                <th className="p-3 text-left">Amount</th>
                <th className="p-3 text-left">Status</th>
                <th className="p-3 text-left">Submitted</th>
                <th className="p-3 text-center">Actions</th>
              </tr>
            </thead>
            <tbody>
              {applications.map(app => (
                <tr key={app.id} className="border-b border-gray-300 hover:bg-gray-50">
                  <td className="p-3">{app.id}</td>
                  <td className="p-3">{app.userFullName}</td>
                  <td className="p-3">₹{app.amount}</td>
                  <td className="p-3"><span className={`px-3 py-1 rounded-full text-sm font-bold ${getStatusColor(app.status)}`}>{app.status}</span></td>
                  <td className="p-3 text-sm">{new Date(app.submittedDate).toLocaleDateString()}</td>
                  <td className="p-3 text-center">
                    {app.status === 'SUBMITTED' && (
                      <>
                        <button onClick={() => handleUpdateStatus(app, 'APPROVED')} className="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded text-sm mr-2">Approve</button>
                        <button onClick={() => handleUpdateStatus(app, 'REJECTED')} className="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-sm">Reject</button>
                      </>
                    )}
                    {app.status === 'APPROVED' && <button onClick={() => handleDisburse(app)} className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-sm">Disburse</button>}
                    {app.status === 'DISBURSED' && <span className="text-gray-500 text-sm">✓ Disbursed</span>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Status Update Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white rounded-lg p-6 w-96">
            <h2 className="text-xl font-bold mb-4">Update Status to {statusUpdate}</h2>
            <p className="text-gray-600 mb-4">Loan ID: {selectedApp?.id}</p>
            {statusUpdate === 'REJECTED' && (
              <div className="mb-4">
                <label className="block text-gray-700 font-bold mb-2">Rejection Reason</label>
                <textarea
                  value={rejectionReason}
                  onChange={(e) => setRejectionReason(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                  placeholder="Enter reason for rejection"
                  rows="3"
                  required
                />
              </div>
            )}
            <div className="flex gap-3 mt-6">
              <button onClick={() => setShowModal(false)} className="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-bold py-2 rounded">Cancel</button>
              <button onClick={submitStatusUpdate} className="flex-1 bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 rounded">Confirm</button>
            </div>
          </div>
        </div>
      )}

      {/* Disbursement Confirmation Modal */}
      {disbursementModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white rounded-lg p-6 w-96">
            <h2 className="text-xl font-bold mb-4">Confirm Disbursement</h2>
            <p className="text-gray-600 mb-2">Loan ID: {selectedApp?.id}</p>
            <p className="text-gray-600 mb-4">Amount: ₹{selectedApp?.amount}</p>
            {!confirmDisburse ? (
              <div className="mb-4 p-3 bg-yellow-100 border border-yellow-400 rounded text-sm text-yellow-800">
                ⚠️ Please carefully review the loan details before proceeding.
              </div>
            ) : (
              <div className="mb-4 p-3 bg-blue-100 border border-blue-400 rounded text-sm text-blue-800">
                ✓ Ready to disburse. Click confirm to proceed.
              </div>
            )}
            {!confirmDisburse ? (
              <div className="flex gap-3">
                <button onClick={() => setDisbursementModal(false)} className="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-bold py-2 rounded">Cancel</button>
                <button onClick={() => setConfirmDisburse(true)} className="flex-1 bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 rounded">Continue</button>
              </div>
            ) : (
              <div className="flex gap-3">
                <button onClick={() => setConfirmDisburse(false)} className="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-bold py-2 rounded">Back</button>
                <button onClick={submitDisburse} className="flex-1 bg-green-500 hover:bg-green-600 text-white font-bold py-2 rounded">Confirm Disbursement</button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
