import React, { useState, useEffect } from 'react';
import axios from '../api/axios';

export default function AdminHistory() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await axios.get('/api/admin/history');
        setHistory(response.data);
      } catch (err) {
        setError('Failed to fetch admin history.');
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, []);

  if (loading) {
    return <div className="text-center p-6">Loading history...</div>;
  }

  if (error) {
    return <div className="p-4 bg-red-100 text-red-700 rounded">{error}</div>;
  }

  return (
    <div className="p-6 bg-white rounded-lg shadow-md">
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Admin Action History</h2>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white">
          <thead>
            <tr>
              <th className="py-2 px-4 border-b text-left">Loan ID</th>
              <th className="py-2 px-4 border-b text-left">User</th>
              <th className="py-2 px-4 border-b text-left">Action</th>
              <th className="py-2 px-4 border-b text-left">Admin</th>
              <th className="py-2 px-4 border-b text-left">Timestamp</th>
            </tr>
          </thead>
          <tbody>
            {history.map((log, index) => (
              <tr key={index}>
                <td className="py-2 px-4 border-b text-left">{log.loanApplicationId}</td>
                <td className="py-2 px-4 border-b text-left">{log.userFullName}</td>
                <td className="py-2 px-4 border-b text-left">{log.action}</td>
                <td className="py-2 px-4 border-b text-left">{log.adminUsername}</td>
                <td className="py-2 px-4 border-b text-left">{new Date(log.timestamp).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
