// src/services/api.js
import axios from 'axios';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: 'http://localhost:8888', // Change this to your API base URL
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add token to requests if it exists
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authAPI = {
  login: (email, password) => {
    return api.post('/auth/login', { email, password });
  },

  signup: (email, password) => {
    return api.post('/auth/signup', { email, password });
  },

  verifyEmail: (otp, token) => {
    return api.post('/auth/verify-email', { otp, token });
  },

  forgotPassword: (email) => {
    return api.post('/auth/forgot-password', { email });
  },

  resetPassword: (newPassword, otp, token) => {
    return api.post('/auth/reset-password', { 
      password: newPassword, 
      otp, 
      token 
    });
  },

  // Get products by category and page
  getProducts: (category, page) => {
    return api.get('/get-products', { params: { category, page } }); // Query params
  },

  // Search products across all categories
  searchProducts: (query) => {
    return api.get('/search-products', { params: { query } }); // Query param
  },

  getCategories: () => {
    return api.get('/get-product-categories'); // API call to fetch categories
  },

  getProductDetails: (productId) => {
    return api.get(`/get-product-details/${productId}`);
  },
};

export default api;