// src/App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Login, Signup, ForgotPassword } from './components';
import Products from './components/Products';
import Header from './components/Header';
import Cart from './components/Cart';
import UserProvider from './context/UserContext';
import ProductDetails from './components/ProductDetails';

const App = () => {
  return (
    <UserProvider>
    <Router>
      <Header />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/products" element={<Products />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/" element={<Navigate to="/products" replace />} />
        <Route path="/product/:productId" element={<ProductDetails />} /> {/* Product Details Route */}
        <Route path="/cart" element={<Cart />} />
      </Routes>
    </Router>
    </UserProvider>
  );
};

export default App;