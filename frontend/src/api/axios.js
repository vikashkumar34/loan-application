import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:9000', // The proxy target
  withCredentials: true,
});

// Add a request interceptor to include the token in all requests
instance.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default instance;
