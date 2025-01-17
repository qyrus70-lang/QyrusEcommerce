import React, { useState, useEffect } from "react";
import { authAPI } from "../services/api";
import { useNavigate } from "react-router-dom";
import { useUser } from "../context/UserContext";

const Wishlist = () => {
  const [favorites, setFavorites] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const { email } = useUser();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchFavorites = async () => {
      setLoading(true);
      setError("");
      try {
        // 1. Fetch product IDs from getFavorites
        const { data } = await authAPI.getFavorites(email);
        // data.favorites is an array of product IDs

        // 2. Fetch product details for each ID
        const productDetailsPromises = data.favorites.map((productId) =>
          authAPI.getProductDetails(productId)
        );
        const products = await Promise.all(productDetailsPromises);

        // 3. Store the products in local state
        setFavorites(products.map((response) => response.data));
      } catch (err) {
        console.error("Failed to fetch wishlist:", err);
        setError("Failed to fetch wishlist.");
      } finally {
        setLoading(false);
      }
    };

    if (email) {
      fetchFavorites();
    }
  }, [email]);

  // Remove item from favorites
  const handleRemoveFavorite = async (productId) => {
    setError("");
    try {
      await authAPI.removeFavorite(email, productId);
      // Filter out the removed product from local state
      setFavorites((prev) => prev.filter((item) => item.id !== productId));
    } catch (err) {
      console.error("Failed to remove product from favorites:", err);
      setError("Failed to remove product from favorites.");
    }
  };

  if (loading) return <div>Loading wishlist...</div>;
  if (error) return <div className="text-red-500">{error}</div>;

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-4">My Wishlist</h1>
      {favorites.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {favorites.map((product) => (
            <div
              key={product.id}
              className="p-4 border rounded shadow hover:shadow-lg transition"
            >
              <img
                src={product.image}
                alt={product.name}
                className="w-full h-40 object-cover rounded cursor-pointer"
                onClick={() =>
                  navigate(`/product/${product.id}`, {
                    state: { previousPath: "/wishlist" },
                  })
                }
              />
              <h2 className="text-xl font-bold mt-2">{product.name}</h2>
              <p className="text-gray-700 font-bold">Price: ${product.price}</p>

              {/* Remove button */}
              <button
                onClick={() => handleRemoveFavorite(product.id)}
                className="mt-2 px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
              >
                Remove
              </button>
            </div>
          ))}
        </div>
      ) : (
        <p>Your wishlist is empty.</p>
      )}
    </div>
  );
};

export default Wishlist;
