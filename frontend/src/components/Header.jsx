import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FaBell, FaUserCircle } from 'react-icons/fa';
import axios from '../api/axios';

const Header = () => {
  const navigate = useNavigate();
  const [greeting, setGreeting] = useState('');
  const [isNotificationOpen, setIsNotificationOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [profileImage, setProfileImage] = useState(null);
  const [notificationCount, setNotificationCount] = useState(0);
  const [notifications, setNotifications] = useState([]);

  const username = sessionStorage.getItem('username') || 'User';
  const role = sessionStorage.getItem('role');

  const fetchProfileImage = async () => {
    try {
      const response = await axios.get('/api/user/profile');
      if (response.data && response.data.profileImagePath) {
        setProfileImage(`http://localhost:9000/api/files/${response.data.profileImagePath}`);
      } else {
        setProfileImage(null);
      }
    } catch (error) {
      console.error('Failed to fetch profile image', error);
      setProfileImage(null);
    }
  };

  const fetchNotifications = async () => {
    try {
      const response = await axios.get('/api/notifications');
      if (response.data) {
        setNotifications(response.data.notifications || []);
        setNotificationCount(response.data.unreadCount || 0);
      }
    } catch (error) {
      console.error('Failed to fetch notifications', error);
    }
  };

  useEffect(() => {
    const getGreeting = () => {
      const hour = new Date().getHours();
      if (hour < 12) return 'Good Morning';
      if (hour < 18) return 'Good Afternoon';
      return 'Good Evening';
    };
    setGreeting(getGreeting());
    fetchProfileImage();
    fetchNotifications();

    window.addEventListener('profileUpdate', fetchProfileImage);
    return () => {
      window.removeEventListener('profileUpdate', fetchProfileImage);
    };
  }, []);

  const handleNotificationClick = async (notification) => {
    setIsNotificationOpen(false);
    navigate(`/loans/${notification.loanApplicationId}`);

    if (!notification.read) {
      try {
        await axios.patch(`/api/notifications/${notification.id}/read`);
        setNotificationCount(prev => Math.max(0, prev - 1));
        setNotifications(prev => prev.map(n => n.id === notification.id ? { ...n, read: true } : n));
      } catch (error) {
        console.error('Failed to mark notification as read', error);
      }
    }
  };

  const handleLogout = () => {
    setIsProfileOpen(false);
    sessionStorage.clear();
    navigate('/login');
  };

  const handleLinkClick = () => {
    setIsProfileOpen(false);
  };

  const userMenu = (
    <>
      <Link to="/profile" onClick={handleLinkClick} className="block px-4 py-2 text-gray-700 hover:bg-gray-100">View Profile</Link>
      <Link to="/update-profile" onClick={handleLinkClick} className="block px-4 py-2 text-gray-700 hover:bg-gray-100">Update Profile</Link>
      <Link to="/change-password" onClick={handleLinkClick} className="block px-4 py-2 text-gray-700 hover:bg-gray-100">Change Password</Link>
      <Link to="/request-history" onClick={handleLinkClick} className="block px-4 py-2 text-gray-700 hover:bg-gray-100">Request History</Link>
      <Link to="/kyc-details" onClick={handleLinkClick} className="block px-4 py-2 text-gray-700 hover:bg-gray-100">KYC Details</Link>
    </>
  );

  const adminMenu = (
    <>
      <Link to="/profile" onClick={handleLinkClick} className="block px-4 py-2 text-gray-700 hover:bg-gray-100">View Profile</Link>
      <Link to="/user-management" onClick={handleLinkClick} className="block px-4 py-2 text-gray-700 hover:bg-gray-100">Add/Remove Users</Link>
      <Link to="/update-profile" onClick={handleLinkClick} className="block px-4 py-2 text-gray-700 hover:bg-gray-100">Update Profile</Link>
      <Link to="/change-password" onClick={handleLinkClick} className="block px-4 py-2 text-gray-700 hover:bg-gray-100">Change Password</Link>
      <Link to="/admin-history" onClick={handleLinkClick} className="block px-4 py-2 text-gray-700 hover:bg-gray-100">Admin History</Link>
    </>
  );

  const dashboardUrl = role === 'ADMIN' ? '/admin/dashboard' : '/user/dashboard';

  return (
    <header className="bg-white shadow-md">
      <div className="container mx-auto px-4 py-3 flex justify-between items-center">
        {/* Left Alignment */}
        <div className="flex items-center space-x-8">
          <Link to={dashboardUrl} className="flex items-center space-x-2">
            <img src="/logo.png" alt="App Logo" className="h-8 w-8" />
            <h1 className="text-xl font-bold text-gray-800">Neurealm Loan Service</h1>
          </Link>
          <p className="text-gray-600">{greeting}, {username}</p>
        </div>

        {/* Right Alignment */}
        <div className="flex items-center space-x-4">
          {/* Notification Icon */}
          <div className="relative">
            <button onClick={() => setIsNotificationOpen(!isNotificationOpen)} className="relative text-gray-600 hover:text-gray-800">
              <FaBell size={20} />
              {notificationCount > 0 && (
                <span className="absolute -top-2 -right-2 h-5 w-5 rounded-full bg-red-500 text-white text-xs flex items-center justify-center">
                  {notificationCount}
                </span>
              )}
            </button>
            {isNotificationOpen && (
              <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg py-1 z-10">
                <div className="font-bold px-4 py-2 border-b">Notifications</div>
                {notifications.length > 0 ? notifications.map(n => (
                  <div key={n.id} onClick={() => handleNotificationClick(n)} className={`px-4 py-2 cursor-pointer hover:bg-gray-100 ${!n.read ? 'bg-blue-50' : ''}`}>
                    <p className="text-sm text-gray-700">{n.message}</p>
                    <p className="text-xs text-gray-500">{new Date(n.timestamp).toLocaleString()}</p>
                  </div>
                )) : <p className="px-4 py-2 text-gray-500">No new notifications.</p>}
              </div>
            )}
          </div>

          {/* Avatar/Profile Dropdown */}
          <div className="relative">
            <button onClick={() => setIsProfileOpen(!isProfileOpen)} className="flex items-center space-x-2 text-gray-600 hover:text-gray-800">
              {profileImage ? (
                <img src={profileImage} alt="Profile" className="h-8 w-8 rounded-full object-cover" />
              ) : (
                <FaUserCircle size={24} />
              )}
            </button>
            {isProfileOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-1 z-10">
                {role === 'ADMIN' ? adminMenu : userMenu}
                <div className="border-t border-gray-200 my-1" />
                <button onClick={handleLogout} className="block w-full text-left px-4 py-2 text-red-600 hover:bg-red-50">Logout</button>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
