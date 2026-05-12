import React, { useState, useEffect } from 'react';
import axios from '../api/axios';

export default function RequestHistory() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await axios.get('/api/loans/my-applications');
        setRequests(response.data || []);
      } catch (err) {
        setError('Failed to fetch request history.');
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, []);

  if (loading) {
    return (
      <div className="p-6 bg-white rounded-lg shadow-md text-center">
        <p>Loading history...</p>
      </div>
    );
  }

  if (error) {
    return <div className="p-4 mb-4 bg-red-100 text-red-700 rounded">{error}</div>;
  }

  return (
    <div className="p-6 bg-white rounded-lg shadow-md">
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Request History</h2>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border">
          <thead className="bg-gray-50">
            <tr>
              <th className="py-3 px-4 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Request ID</th>
              <th className="py-3 px-4 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Request Name (Purpose)</th>
              <th className="py-3 px-4 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th className="py-3 px-4 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Request Created At</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {requests.length > 0 ? (
              requests.map(req => (
                <tr key={req.id} className="hover:bg-gray-50">
                  <td className="py-4 px-4 whitespace-nowrap">{req.id}</td>
                  <td className="py-4 px-4 whitespace-nowrap">{req.purpose}</td>
                  <td className="py-4 px-4 whitespace-nowrap">
                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                      req.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                      req.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                      req.status === 'SUBMITTED' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-blue-100 text-blue-800'
                    }`}>
                      {req.status}
                    </span>
                  </td>
                  <td className="py-4 px-4 whitespace-nowrap">{new Date(req.submittedDate).toLocaleDateString()}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="4" className="text-center py-4">No request history found.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
