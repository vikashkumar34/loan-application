import React, { useState, useEffect } from 'react';
import axios from '../api/axios';
import { FaCheckCircle, FaUserCircle } from 'react-icons/fa';
import { Link } from 'react-router-dom';

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const role = sessionStorage.getItem('role');

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await axios.get('/api/user/profile');
        setProfile(response.data);
      } catch (err) {
        setError('Failed to fetch profile data.');
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  if (loading) {
    return <div className="text-center p-6">Loading...</div>;
  }

  if (error) {
    return <div className="p-4 bg-red-100 text-red-700 rounded">{error}</div>;
  }

  const profileImageUrl = profile.profileImagePath
    ? `http://localhost:9000/api/files/${profile.profileImagePath}`
    : null;

  const isUser = role === 'USER';

  return (
    <div className="p-8 bg-white rounded-lg shadow-md max-w-4xl mx-auto">
      <div className="flex items-center justify-between mb-6 border-b pb-4">
        <div className="flex items-center space-x-4">
          {profileImageUrl ? (
            <img className="h-24 w-24 object-cover rounded-full" src={profileImageUrl} alt="Profile" />
          ) : (
            <FaUserCircle size={96} className="text-gray-300" />
          )}
          <div>
            <h2 className="text-3xl font-bold text-gray-800">{profile.fullName}</h2>
            <p className="text-gray-500">@{profile.username}</p>
          </div>
        </div>
        <Link to="/update-profile" className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg">
          Edit Profile
        </Link>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-6">
        {/* Personal Details */}
        <div className="space-y-4">
          <h3 className="text-lg font-semibold text-gray-700 border-b pb-2">Personal Details</h3>
          <p><strong>Email:</strong> {profile.email}</p>
          <p><strong>Mobile No:</strong> {profile.mobileNumber || 'N/A'}</p>
          <p><strong>Address:</strong> {profile.address || 'N/A'}</p>
          {isUser && (
            <>
              <p><strong>Date of Birth:</strong> {profile.dateOfBirth || 'N/A'}</p>
              <p><strong>Gender:</strong> {profile.gender || 'N/A'}</p>
              <p><strong>Marital Status:</strong> {profile.maritalStatus || 'N/A'}</p>
              <p><strong>Father's/Mother's Name:</strong> {profile.parentName || 'N/A'}</p>
              <p><strong>Religion:</strong> {profile.religion || 'N/A'}</p>
            </>
          )}
        </div>

        {/* Financial & KYC Details */}
        <div className="space-y-4">
          <h3 className="text-lg font-semibold text-gray-700 border-b pb-2">Financial & KYC</h3>
          {isUser && (
            <>
              <p><strong>Job Status:</strong> {profile.jobStatus || 'N/A'}</p>
              <p><strong>Bank Account Number:</strong> {profile.bankAccountNumber || 'N/A'}</p>
              <p><strong>PAN Card:</strong> {profile.panCard || 'N/A'}</p>
              <p><strong>Aadhar Number:</strong> {profile.aadharNumber || 'N/A'}</p>
              <p><strong>KYC Status:</strong> {profile.kycStatus} {profile.kycStatus === 'VERIFIED' && <FaCheckCircle className="inline-block ml-2 text-green-500" />}</p>
            </>
          )}
          {!isUser && <p>Role: ADMIN</p>}
        </div>

        {/* Nominee Details */}
        {isUser && (
          <div className="md:col-span-2 space-y-4 pt-4 border-t">
            <h3 className="text-lg font-semibold text-gray-700">Nominee Details</h3>
            <p><strong>Nominee Name:</strong> {profile.nomineeName || 'N/A'}</p>
            <p><strong>Nominee Relationship:</strong> {profile.nomineeRelationship || 'N/A'}</p>
          </div>
        )}
      </div>
    </div>
  );
}
