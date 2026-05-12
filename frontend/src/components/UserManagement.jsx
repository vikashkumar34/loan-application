import React, { useState, useEffect } from 'react';
import axios from '../api/axios';

export default function UserManagement() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    fullName: '',
    email: '',
    role: 'USER'
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await axios.get('/api/admin/users');
      setUsers(response.data);
    } catch (err) {
      setError('Failed to fetch users.');
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
      await axios.post('/api/admin/users', formData);
      alert('User created successfully!');
      setFormData({ username: '', password: '', fullName: '', email: '', role: 'USER' });
      setShowForm(false);
      fetchUsers();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to create user.');
    }
  };

  const handleDelete = async (userId) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        await axios.delete(`/api/admin/users/${userId}`);
        alert('User deleted successfully!');
        fetchUsers();
      } catch (err) {
        alert('Failed to delete user.');
      }
    }
  };

  return (
    <div className="p-6 bg-white rounded-lg shadow-md">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-gray-800">User Management</h2>
        <button onClick={() => setShowForm(!showForm)} className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg">
          {showForm ? 'Cancel' : 'Add User'}
        </button>
      </div>

      {showForm && (
        <div className="mb-6 p-6 border rounded-lg">
          <h3 className="text-xl font-semibold mb-4">Add New User</h3>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Form fields */}
            <input name="username" value={formData.username} onChange={handleChange} placeholder="Username" required className="p-2 border rounded" />
            <input name="password" type="password" value={formData.password} onChange={handleChange} placeholder="Password" required className="p-2 border rounded" />
            <input name="fullName" value={formData.fullName} onChange={handleChange} placeholder="Full Name" required className="p-2 border rounded" />
            <input name="email" type="email" value={formData.email} onChange={handleChange} placeholder="Email" required className="p-2 border rounded" />
            <select name="role" value={formData.role} onChange={handleChange} className="p-2 border rounded">
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
            <div className="md:col-span-2 flex justify-end">
              <button type="submit" className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded-lg">Create User</button>
            </div>
          </form>
        </div>
      )}

      {loading ? <p>Loading users...</p> : error ? <p className="text-red-500">{error}</p> : (
        <div className="overflow-x-auto">
          <table className="min-w-full bg-white">
            <thead>
              <tr>
                <th className="py-2 px-4 border-b">ID</th>
                <th className="py-2 px-4 border-b">Username</th>
                <th className="py-2 px-4 border-b">Full Name</th>
                <th className="py-2 px-4 border-b">Email</th>
                <th className="py-2 px-4 border-b">Role</th>
                <th className="py-2 px-4 border-b">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td className="py-2 px-4 border-b">{user.id}</td>
                  <td className="py-2 px-4 border-b">{user.username}</td>
                  <td className="py-2 px-4 border-b">{user.fullName}</td>
                  <td className="py-2 px-4 border-b">{user.email}</td>
                  <td className="py-2 px-4 border-b">{user.role}</td>
                  <td className="py-2 px-4 border-b">
                    <button onClick={() => handleDelete(user.id)} className="bg-red-500 hover:bg-red-600 text-white font-bold py-1 px-3 rounded-lg">Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
