import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { authAPI } from '../services/api';

const ProductDetails = () => {
  const { productId } = useParams(); // Get product ID from the URL
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [selectedColor, setSelectedColor] = useState('');
  const [selectedProvider, setSelectedProvider] = useState('');
  const [selectedSize, setSelectedSize] = useState('');

  // Fetch product details
  const fetchProductDetails = async () => {
    try {
      const { data } = await authAPI.getProductDetails(productId);
      setProduct(data);

      // Set default selections
      if (data.colors?.length > 0) setSelectedColor(data.colors[0].hex);
      if (data.providers?.length > 0) setSelectedProvider(data.providers[0]);
      if (data.sizes?.length > 0) setSelectedSize(data.sizes[0]);
    } catch (err) {
      console.error('Failed to fetch product details:', err);
    }
  };

  useEffect(() => {
    fetchProductDetails();
  }, [productId]);

  if (!product) return <div>Loading...</div>;

  return (
    <div className="container mx-auto p-6">
      <div className="flex flex-col md:flex-row space-y-4 md:space-y-0 md:space-x-6">
        {/* Product Image */}
        <div className="flex-1">
          <img src={product.image} alt={product.name} className="w-full h-96 object-cover rounded" />
        </div>

        {/* Product Details */}
        <div className="flex-1">
          <h1 className="text-3xl font-bold">{product.name}</h1>
          <p className="text-gray-700 mt-2">
            {product.category} / {product.subcategory}
          </p>
          <p className="text-xl font-bold mt-4">${product.price}</p>

          {/* Color Selection */}
          <div className="mt-4">
            <label className="font-bold">Color:</label>
            <div className="flex space-x-2 mt-2">
              {product.colors.map((color) => (
                <div
                  key={color.hex}
                  className={`w-8 h-8 rounded-full cursor-pointer`}
                  style={{
                    backgroundColor: color.hex,
                    border: selectedColor === color.hex ? '2px solid black' : 'none',
                  }}
                  title={color.name}
                  onClick={() => setSelectedColor(color.hex)}
                ></div>
              ))}
            </div>
          </div>

          {/* Size Selection */}
          <div className="mt-4">
            <label className="font-bold">Size:</label>
            <select
              value={selectedSize}
              onChange={(e) => setSelectedSize(e.target.value)}
              className="p-2 border rounded"
            >
              {product.sizes.map((size) => (
                <option key={size} value={size}>
                  {size}
                </option>
              ))}
            </select>
          </div>

          {/* Provider Selection */}
          <div className="mt-4">
            <label className="font-bold">Provider:</label>
            <select
              value={selectedProvider}
              onChange={(e) => setSelectedProvider(e.target.value)}
              className="p-2 border rounded"
            >
              {product.providers.map((provider) => (
                <option key={provider} value={provider}>
                  {provider}
                </option>
              ))}
            </select>
          </div>

          {/* Quantity Selection */}
          <div className="mt-4">
            <label className="font-bold">Quantity:</label>
            <input
              type="number"
              min="1"
              value={quantity}
              onChange={(e) => setQuantity(Number(e.target.value))}
              className="p-2 border rounded w-16"
            />
          </div>

          {/* Buttons */}
          <div className="mt-6 flex space-x-4">
            <button
              onClick={() =>
                console.log('Buying:', { productId, quantity, selectedColor, selectedProvider, selectedSize })
              }
              className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
            >
              Buy Now
            </button>
            <button
              onClick={() =>
                console.log('Adding to cart:', { productId, quantity, selectedColor, selectedProvider, selectedSize })
              }
              className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
            >
              Add to Cart
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetails;
