import React, { useState, useEffect } from 'react';
import axios from '../api/axios';
import { FaCheckCircle, FaTimesCircle, FaHourglassHalf } from 'react-icons/fa';

export default function KycDetails() {
  const [kycStatus, setKycStatus] = useState('');
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchKycData = async () => {
      try {
        // Fetch profile to get the overall KYC status
        const profileRes = await axios.get('/api/user/profile');
        setKycStatus(profileRes.data.kycStatus || 'PENDING');

        // Fetch uploaded documents
        const docsRes = await axios.get('/api/kyc/documents');
        setDocuments(docsRes.data || []);
      } catch (err) {
        console.error("Failed to fetch KYC data.", err);
        setKycStatus('ERROR');
      } finally {
        setLoading(false);
      }
    };
    fetchKycData();
  }, []);

  const getStatusInfo = () => {
    switch (kycStatus) {
      case 'VERIFIED':
        return { icon: <FaCheckCircle className="text-green-500 text-5xl" />, text: 'Your KYC is verified.', color: 'text-green-500' };
      case 'SUBMITTED':
        return { icon: <FaHourglassHalf className="text-yellow-500 text-5xl" />, text: 'Your documents are under review.', color: 'text-yellow-500' };
      case 'REJECTED':
        return { icon: <FaTimesCircle className="text-red-500 text-5xl" />, text: 'Your KYC was rejected. Please contact support.', color: 'text-red-500' };
      default:
        return { icon: <FaTimesCircle className="text-gray-500 text-5xl" />, text: 'Your KYC is pending. Please complete submission.', color: 'text-gray-500' };
    }
  };

  if (loading) {
    return <div className="text-center p-6">Loading KYC Status...</div>;
  }

  const { icon, text, color } = getStatusInfo();

  return (
    <div className="p-8 bg-white rounded-lg shadow-md max-w-2xl mx-auto">
      <h2 className="text-2xl font-bold text-gray-800 mb-6 text-center">KYC Status</h2>

      <div className="flex flex-col items-center text-center mb-8">
        <div className="mb-4">{icon}</div>
        <p className={`text-xl font-semibold ${color}`}>{text}</p>
      </div>

      <div className="border-t pt-6">
        <h3 className="text-lg font-semibold text-gray-700 mb-4">Submitted Documents</h3>
        {documents.length > 0 ? (
          <ul className="list-disc list-inside space-y-2">
            {documents.map(doc => (
              <li key={doc.id}>
                <a href={`http://localhost:9000/api/files/${doc.filePath}`} target="_blank" rel="noopener noreferrer" className="text-blue-500 hover:underline">
                  {doc.documentType} (Uploaded on: {new Date(doc.uploadedAt).toLocaleDateString()})
                </a>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-gray-500">No documents have been submitted yet.</p>
        )}
      </div>
    </div>
  );
}
