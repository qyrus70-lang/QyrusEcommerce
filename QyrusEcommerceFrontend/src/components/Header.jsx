import React, { useState, useEffect } from 'react';
import { authAPI } from '../services/api';
import { Link, useNavigate } from 'react-router-dom';

const Header = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token')); // Check if user is logged in
  const [categories, setCategories] = useState({});
  const [hoveredCategory, setHoveredCategory] = useState(''); // Track hovered category
  const [isDropdownOpen, setIsDropdownOpen] = useState(false); // Track if the dropdown is open
  const [search, setSearch] = useState('');
  const navigate = useNavigate();

  // Fetch categories from API
  const fetchCategories = async () => {
    try {
      const { data } = await authAPI.getCategories(); // Fetch categories
      setCategories(data.categories || {}); // Set the fetched categories
    } catch (err) {
      console.error('Failed to fetch categories:', err);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  // Handle category click
  const handleCategoryClick = (category) => {
    navigate(`/products?category=${category}&subcategory=none`);
  };

  // Handle subcategory click
  const handleSubcategoryClick = (category, subcategory) => {
    navigate(`/products?category=${category}&subcategory=${subcategory}`);
  };

  // Toggle dropdown visibility
  const toggleDropdown = () => {
    setIsDropdownOpen((prevState) => !prevState);
  };

  // Handle search submission
  const handleSearch = (e) => {
    e.preventDefault();
    console.log('Search Query:', search);
    // Trigger search API or navigation logic here
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setIsLoggedIn(false);
    navigate('/login');
  };

  return (
    <header className="bg-blue-600 text-white p-4 shadow-md">
      <div className="container mx-auto flex justify-between items-center">
        {/* Logo */}
        <button onClick={() => navigate('/')} className="text-xl font-bold">
          MyShop
        </button>

        {/* Search Bar */}

        <form
        onSubmit={handleSearch}
        className="flex items-center space-x-4 bg-gray-100 p-2 rounded shadow relative md:w-1/2" // Full width
        >
        {/* Categories Dropdown */}
        <div className="relative">
            <button
            onClick={toggleDropdown} // Toggle dropdown visibility
            className="px-4 py-2 bg-white text-black rounded-l border border-gray-300"
            >
            Categories
            </button>

            {isDropdownOpen && (
            <div className="absolute left-0 top-full mt-2 bg-white text-black shadow-lg rounded z-10">
                {Object.keys(categories).map((category) => (
                <div
                    key={category}
                    className="relative group"
                    onMouseEnter={() => setHoveredCategory(category)} // Show subcategories
                    onMouseLeave={() => setHoveredCategory('')} // Hide subcategories
                >
                    <button
                    onClick={() => handleCategoryClick(category)}
                    className="block px-4 py-2 hover:bg-gray-200 w-full text-left"
                    >
                    {category}
                    </button>
                    {hoveredCategory === category && categories[category]?.length > 0 && (
                    <div className="absolute left-full top-0 bg-white text-black shadow-lg rounded z-10">
                        {categories[category].map((subcategory) => (
                        <button
                            key={subcategory}
                            onClick={() => handleSubcategoryClick(category, subcategory)}
                            className="block px-4 py-2 hover:bg-gray-200 w-full text-left"
                        >
                            {subcategory}
                        </button>
                        ))}
                    </div>
                    )}
                </div>
                ))}
            </div>
            )}
        </div>

        {/* Search Input */}
        <input
            type="text"
            placeholder="Search products..."
            className="p-2 border rounded flex-grow text-black" // Flex-grow to stretch
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ minWidth: '300px' }} // Optional minimum width
        />
        <button
            type="submit"
            className="px-4 py-2 bg-blue-600 text-white rounded-r hover:bg-blue-700"
        >
            Search
        </button>
        </form>


        {/* Login Button */}
        <nav className="flex space-x-6 items-center">
        {!isLoggedIn ? (
          <button
            onClick={() => navigate('/login')}
            className="px-4 py-2 bg-white text-blue-600 rounded hover:bg-gray-100"
          >
            Login
          </button>): (
            <>
              {/* Cart Icon */}
              <Link to="/cart" className="relative">
                <span className="material-icons">shopping_cart</span>
              </Link>

              {/* Profile Dropdown */}
              <div className="relative group">
                <button className="flex items-center space-x-2">
                  <span className="material-icons">account_circle</span>
                </button>
                <div className="absolute right-0 mt-2 bg-white text-black shadow-lg rounded hidden group-hover:block w-48">
                  <ul className="py-2">
                    <li>
                      <Link to="/account" className="block px-4 py-2 hover:bg-gray-200">
                        Account
                      </Link>
                    </li>
                    <li>
                      <Link to="/wishlist" className="block px-4 py-2 hover:bg-gray-200">
                        Wishlist
                      </Link>
                    </li>
                    <li>
                      <Link to="/orders" className="block px-4 py-2 hover:bg-gray-200">
                        My Orders
                      </Link>
                    </li>
                    <li>
                      <Link to="/contact" className="block px-4 py-2 hover:bg-gray-200">
                        Contact Us
                      </Link>
                    </li>
                    <li>
                      <button
                        onClick={handleLogout}
                        className="block w-full text-left px-4 py-2 hover:bg-gray-200"
                      >
                        Logout
                      </button>
                    </li>
                  </ul>
                </div>
              </div>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
