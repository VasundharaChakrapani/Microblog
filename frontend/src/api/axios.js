import axios from 'axios';

const api = axios.create({
  baseURL: 'https://microblog-k5e9.onrender.com',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true
});

export const setAuthToken = (token) => {
  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common['Authorization'];
  }
};

export default api;
