import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:9000', // The proxy target
  withCredentials: true,
});

export default instance;
