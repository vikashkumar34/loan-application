import React, { useState, useEffect } from 'react';
import axios from '../api/axios';

export default function AdminKycReview() {
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUsersWithKyc = async () => {
      try {
        const response = await axios.get('/api/admin/users');
        // Filter for users who have submitted KYC
        setUsers(response.data.filter(u => u.kycStatus === 'SUBMITTED'));
      } catch (err) {
        // Handle error
      } finally {
        setLoading(false);
      }
    };
    fetchUsersWithKyc();
  }, []);

  const handleSelectUser = async (userId) => {
    try {
      const response = await axios.get(`/api/admin/kyc/documents/${userId}`);
      setSelectedUser(users.find(u => u.id === userId));
      setDocuments(response.data);
    } catch (err) {
      // Handle error
    }
  };

  const handleApprove = async () => {
    try {
      await axios.post(`/api/admin/kyc/approve/${selectedUser.id}`);
      alert('KYC Approved!');
      // Refresh list
      const response = await axios.get('/api/admin/users');
      setUsers(response.data.filter(u => u.kycStatus === 'SUBMITTED'));
      setSelectedUser(null);
      setDocuments([]);
    } catch (err) {
      alert('Failed to approve KYC.');
    }
  };

  return (
    <div className="p-6 bg-white rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-6">KYC Review</h2>
      <div className="grid grid-cols-3 gap-6">
        <div className="col-span-1 border-r pr-6">
          <h3 className="font-semibold mb-4">Users Awaiting KYC Approval</h3>
          {loading ? <p>Loading...</p> : (
            <ul>
              {users.map(user => (
                <li key={user.id} onClick={() => handleSelectUser(user.id)} className="cursor-pointer p-2 hover:bg-gray-100 rounded">
                  {user.fullName} (@{user.username})
                </li>
              ))}
            </ul>
          )}
        </div>
        <div className="col-span-2">
          {selectedUser ? (
            <div>
              <h3 className="font-semibold mb-4">Reviewing: {selectedUser.fullName}</h3>
              <p><strong>PAN:</strong> {selectedUser.panCard}</p>
              <p><strong>Account:</strong> {selectedUser.bankAccountNumber}</p>
              <h4 className="font-semibold mt-4">Documents:</h4>
              <ul>
                {documents.map(doc => (
                  <li key={doc.id}><a href={`http://localhost:9000/api/files/${doc.filePath}`} target="_blank" rel="noopener noreferrer">{doc.documentType}</a></li>
                ))}
              </ul>
              <button onClick={handleApprove} className="mt-6 bg-green-500 text-white font-bold py-2 px-4 rounded">Approve KYC</button>
            </div>
          ) : <p>Select a user to review their documents.</p>}
        </div>
      </div>
    </div>
  );
}
