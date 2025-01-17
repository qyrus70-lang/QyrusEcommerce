import React, { useState, useEffect } from "react";
import { authAPI } from "../services/api";
import { useUser } from "../context/UserContext";

const MyAddress = () => {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [newAddress, setNewAddress] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAddressId, setEditingAddressId] = useState(null);
  const [updatedAddress, setUpdatedAddress] = useState("");
  const { email } = useUser();

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
      await authAPI.createAddress(email, newAddress);
      fetchAddresses();
      setNewAddress("");
      setIsModalOpen(false);
    } catch (err) {
      setError("Failed to add address.");
    }
  };

  const handleDeleteAddress = async (addressId) => {
    try {
      await authAPI.deleteAddress(email, addressId);
      fetchAddresses();
    } catch (err) {
      setError("Failed to delete address.");
    }
  };

  const handleUpdateAddress = async () => {
    try {
      await authAPI.updateAddress(email, editingAddressId, updatedAddress);
      fetchAddresses();
      setEditingAddressId(null);
    } catch (err) {
      setError("Failed to update address.");
    }
  };

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6 text-center">My Address</h1>
      {error && <div className="text-red-500 mb-4 text-center">{error}</div>}

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {/* Add New Address Card */}
        <div
          className="p-6 border rounded-lg shadow-lg flex flex-col items-center justify-center hover:shadow-xl transition cursor-pointer"
          onClick={() => setIsModalOpen(true)}
        >
          <span className="text-blue-600 text-5xl font-bold">+</span>
          <p className="mt-2 text-gray-500">Add Address</p>
        </div>

        {/* Address Cards */}
        {addresses.map((address) => (
          <div
            key={address.address_id}
            className="p-6 border rounded-lg shadow-lg hover:shadow-xl transition flex flex-col justify-between relative"
          >
            {editingAddressId === address.address_id ? (
              <>
                <textarea
                  value={updatedAddress}
                  onChange={(e) => setUpdatedAddress(e.target.value)}
                  className="w-full p-3 border rounded mb-4 text-lg resize-none"
                  rows="4"
                ></textarea>
                <div className="flex justify-end space-x-4">
                  <button
                    onClick={handleUpdateAddress}
                    className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-lg"
                  >
                    Save
                  </button>
                  <button
                    onClick={() => setEditingAddressId(null)}
                    className="px-6 py-2 bg-gray-300 text-black rounded hover:bg-gray-400 text-lg"
                  >
                    Cancel
                  </button>
                </div>
              </>
            ) : (
              <>
                <p className="text-gray-800 text-lg mb-4 text-center whitespace-pre-line">
                  {address.address}
                </p>
                <div className="absolute top-4 right-4 flex space-x-4">
                  <button
                    onClick={() => {
                      setEditingAddressId(address.address_id);
                      setUpdatedAddress(address.address);
                    }}
                    className="text-blue-600 hover:text-blue-800 text-lg"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDeleteAddress(address.address_id)}
                    className="text-red-600 hover:text-red-800 text-lg"
                  >
                    Delete
                  </button>
                </div>
              </>
            )}
          </div>
        ))}
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

export default MyAddress;
