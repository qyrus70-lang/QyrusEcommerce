import React, { useState, useEffect } from "react";
import { authAPI } from "../services/api";
import { useUser } from "../context/UserContext";
import { useLocation, useNavigate } from 'react-router-dom';


const AddressSelection = () => {
  const [addresses, setAddresses] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [newAddress, setNewAddress] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const { email } = useUser();
  const location = useLocation();
  const navigate = useNavigate();

  const { products } = location.state || {};

  const handleNext = () => {
    if (!selectedAddressId) {
      alert("Please select an address!");
      return;
    }

    // Proceed to the order confirmation or payment page
    navigate('/payment', {
      state: {
        addressId: selectedAddressId,
        products,
      },
    });
  };

  const fetchAddresses = async () => {
    setLoading(true);
    setError("");
    try {
      const { data } = await authAPI.getAddresses(email);
      setAddresses(data.addresses || []);
    } catch (err) {
      setError("Failed to fetch addresses.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (email) {
      fetchAddresses();
    }
  }, [email]);

  const handleAddAddress = async () => {
    try {
      const { data } = await authAPI.createAddress(email, newAddress);
      fetchAddresses();
      setSelectedAddressId(data.address_id); // Automatically select the newly added address
      setNewAddress("");
      setIsModalOpen(false);
    } catch (err) {
      setError("Failed to add address.");
    }
  };

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6 text-center">Select Address</h1>
      {error && <div className="text-red-500 mb-4 text-center">{error}</div>}

      {loading ? (
        <p className="text-center">Loading addresses...</p>
      ) : (
        <div className="space-y-4">
          {addresses.map((address) => (
            <div
              key={address.address_id}
              className="flex items-center p-4 border rounded-lg shadow hover:shadow-lg transition"
            >
              <input
                type="radio"
                name="address"
                value={address.address_id}
                checked={selectedAddressId === address.address_id}
                onChange={() => setSelectedAddressId(address.address_id)}
                className="mr-4"
              />
              <p className="text-gray-800 text-lg whitespace-pre-line">{address.address}</p>
            </div>
          ))}

          <button
            onClick={() => setIsModalOpen(true)}
            className="block w-full p-4 border rounded-lg shadow text-center text-blue-600 hover:bg-blue-100"
          >
            + Add New Address
          </button>
        </div>
      )}

      <div className="mt-6 text-center space-x-4">
        <button
          onClick={() => navigate(-1)}
          className="px-6 py-3 bg-gray-300 text-black rounded-lg hover:bg-gray-400"
        >
          Back
        </button>
        <button
          disabled={!selectedAddressId}
          onClick={handleNext}
          className="px-6 py-3 bg-blue-700 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed"
        >
          Next 
        </button>
      </div>

      {/* Add Address Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded shadow-md w-full max-w-md">
            <h2 className="text-xl font-bold mb-4 text-center">Add New Address</h2>
            <textarea
              value={newAddress}
              onChange={(e) => setNewAddress(e.target.value)}
              className="w-full p-3 border rounded mb-4 resize-none"
              rows="4"
              placeholder="Enter your new address here..."
            ></textarea>
            <div className="flex justify-end space-x-4">
              <button
                onClick={handleAddAddress}
                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                Save
              </button>
              <button
                onClick={() => setIsModalOpen(false)}
                className="px-4 py-2 bg-gray-300 text-black rounded hover:bg-gray-400"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AddressSelection;
