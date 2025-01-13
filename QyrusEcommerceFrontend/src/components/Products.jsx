import React, { useState, useEffect } from 'react';
import { authAPI } from '../services/api'; // Use your API service here
import { useNavigate } from 'react-router-dom'; 

const Products = () => {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]); // State for categories
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState(''); // Default category is empty initially
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // Fetch categories from API
  const fetchCategories = async () => {
    setLoading(true);
    setError('');
    try {
      const { data } = await authAPI.getCategories(); // Fetch API response
      const categoriesObject = data.categories || {}; // Ensure categories key exists
      const formattedCategories = Object.keys(categoriesObject).map((category) => ({
        label: category, // Main category (e.g., Men, Women, etc.)
        value: category, // Same as label for simplicity
        subcategories: categoriesObject[category], // Subcategories array (e.g., ["T-Shirts", "Jeans", "Shirts"])
      }));
      setCategories(formattedCategories); // Set the processed data
      if (formattedCategories.length > 0) {
        setCategory(formattedCategories[0].value); // Default to the first category
      }
    } catch (err) {
      setError('Failed to fetch categories');
    } finally {
      setLoading(false);
    }
  };
  

  // Fetch products from API
  const fetchProducts = async (selectedCategory = category) => {
    if (!selectedCategory) return; // Skip fetching if no category is selected
    setLoading(true);
    setError('');
    try {
      const { data } = await authAPI.getProducts(selectedCategory, page); // API call with category and page
      setProducts(data.products || []); // Assume API returns { products, total_pages }
      setTotalPages(data.total_pages || 1);
    } catch (err) {
      setError('Failed to fetch products');
    } finally {
      setLoading(false);
    }
  };

  // Search products from API
  const searchProducts = async () => {
    if (!search.trim()) return; // Ignore empty search
    setLoading(true);
    setError('');
    try {
      const { data } = await authAPI.searchProducts(search); // Search API call
      setProducts(data.products || []); // Assume API returns { products }
      setTotalPages(1); // Disable pagination for search results
    } catch (err) {
      setError('Search failed');
    } finally {
      setLoading(false);
    }
  };

  // Fetch categories and products on initial load
  useEffect(() => {
    fetchCategories();
  }, []);

  // Fetch products when category or page changes
  useEffect(() => {
    if (category) fetchProducts();
  }, [category, page]);

  const handleSearch = (e) => {
    e.preventDefault();
    searchProducts();
  };

  const handleCategoryChange = (e) => {
    setCategory(e.target.value); // Update category and reset pagination
    setPage(1);
  };

  return (
    <div className="container mx-auto p-6">
      {/* Error message */}
      {error && <div className="text-red-500 text-center mb-4">{error}</div>}


      {/* Products Grid */}
      {loading ? (
        <div className="text-center">Loading products...</div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {products.map((product) => (
  <div
    key={product.id}
    className="relative bg-white p-4 rounded-lg shadow hover:shadow-lg transition"
  >
    {localStorage.getItem('token') && (
      <button
        onClick={() => console.log('Adding to favorites:', product.id)}
        className="absolute top-2 right-2 bg-white p-2 rounded-full shadow hover:bg-gray-100"
      >
        <span className="material-icons text-red-500">favorite_border</span>
      </button>
    )}
    <img
      src={product.image}
      alt={product.name}
      className="w-full h-40 object-cover rounded cursor-pointer"
      onClick={() => navigate(`/product/${product.id}`)} // Navigate to product details page
    />
    <h3 className="mt-4 font-bold text-lg">{product.name}</h3>
    <p className="text-gray-700">${product.price}</p>
    {localStorage.getItem('token') && (
      <div className="mt-4 flex space-x-4">
        <button
          onClick={() => console.log('Adding to cart:', product)}
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Add to Cart
        </button>
        <button
          onClick={() => console.log('Buying now:', product)}
          className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
        >
          Buy Now
        </button>
      </div>
    )}
  </div>
))}


        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="mt-6 flex justify-center items-center space-x-4">
          <button
            disabled={page === 1}
            onClick={() => setPage((prev) => prev - 1)}
            className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400 disabled:opacity-50"
          >
            Previous
          </button>
          <span>
            Page {page} of {totalPages}
          </span>
          <button
            disabled={page === totalPages}
            onClick={() => setPage((prev) => prev + 1)}
            className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400 disabled:opacity-50"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};

export default Products;
