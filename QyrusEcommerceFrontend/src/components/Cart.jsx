import React, { useState } from 'react';

const Cart = () => {
  const [cartItems, setCartItems] = useState([
    // Dummy data for cart items. Replace with API data later.
    { id: 1, name: 'Product 1', price: 100, quantity: 1, image: 'https://via.placeholder.com/150' },
    { id: 2, name: 'Product 2', price: 200, quantity: 2, image: 'https://via.placeholder.com/150' },
  ]);

  const handleCheckout = () => {
    console.log('Proceeding to checkout...');
  };

  return (
    <div className="container mx-auto p-6">
      <h2 className="text-2xl font-bold mb-4">Your Cart</h2>
      {cartItems.length === 0 ? (
        <div>Your cart is empty.</div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {cartItems.map((item) => (
              <div key={item.id} className="flex items-center space-x-4 border p-4 rounded shadow">
                <img src={item.image} alt={item.name} className="w-20 h-20 object-cover rounded" />
                <div>
                  <h3 className="font-bold">{item.name}</h3>
                  <p>${item.price}</p>
                  <p>Quantity: {item.quantity}</p>
                </div>
              </div>
            ))}
          </div>
          <button
            onClick={handleCheckout}
            className="mt-6 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Checkout
          </button>
        </>
      )}
    </div>
  );
};

export default Cart;
