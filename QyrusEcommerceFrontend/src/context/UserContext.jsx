import React, { createContext, useState, useContext } from 'react';

// Create UserContext
const UserContext = createContext();

// Create a custom hook to use the UserContext
export const useUser = () => useContext(UserContext);

const UserProvider = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));
  const [email, setEmail] = useState(localStorage.getItem('email') || '');

  const login = (userEmail) => {
    setIsLoggedIn(true);
    setEmail(userEmail); // Set email on login
    localStorage.setItem('email', userEmail); // Store email in localStorage
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('email'); // Remove email from localStorage
    setIsLoggedIn(false);
    setEmail(''); // Clear email in context
  };

  return (
    <UserContext.Provider value={{ isLoggedIn, email, login, logout }}>
      {children}
    </UserContext.Provider>
  );
};

export default UserProvider;
