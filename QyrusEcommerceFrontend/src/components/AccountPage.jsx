import React, { useState, useEffect } from "react";
import { authAPI } from "../services/api";
import Header from "./Header"; // Reuse the same header component
import { useUser } from '../context/UserContext';

const AccountPage = () => {
  const [accountDetails, setAccountDetails] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    age: "",
    phone: "",
    country: "",
  });
  const [countries] = useState(["India", "USA", "Canada", "UK", "Australia"]); // Add more countries as needed
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { isLoggedIn, email, logout } = useUser();

  // Fetch account details
  const fetchAccountDetails = async () => {
    try {
      const { data } = await authAPI.getAccountDetails(email);
      setAccountDetails(data);
      setFormData({
        name: data.name,
        age: data.age,
        phone: data.phone,
        country: data.country,
      });
    } catch (err) {
      setError("Failed to fetch account details");
    }
  };

  useEffect(() => {
    fetchAccountDetails();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleUpdate = async () => {
    setLoading(true);
    setError("");
    try {
      await authAPI.updateAccountDetails(email, formData);
      setAccountDetails(formData); // Update UI with new details
      setIsEditing(false); // Exit edit mode
    } catch (err) {
      setError("Failed to update account details");
    } finally {
      setLoading(false);
    }
  };

  if (!accountDetails) return <div>Loading...</div>;

  return (
    <div>
      <div className="container mx-auto p-6">
        <div className="flex justify-between items-center mb-4">
          {/* Title */}
          <h1 className="text-3xl font-bold">Account Details</h1>
          
          {/* Edit Button */}
          {!isEditing && (
            <button
              onClick={() => setIsEditing(true)}
              className="px-4 py-2 bg-gray-300 text-black rounded hover:bg-gray-400"
            >
              Edit
            </button>
          )}
        </div>
        
        {error && <div className="text-red-500 mb-4">{error}</div>}
  
        <div className="space-y-4">
          {/* Name */}
          <div>
            <label className="block font-bold">Name</label>
            {isEditing ? (
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                className="p-2 border rounded w-full"
              />
            ) : (
              <p>{accountDetails.name}</p>
            )}
          </div>
  
          {/* Age */}
          <div>
            <label className="block font-bold">Age</label>
            {isEditing ? (
              <input
                type="number"
                name="age"
                value={formData.age}
                onChange={handleInputChange}
                className="p-2 border rounded w-full"
              />
            ) : (
              <p>{accountDetails.age}</p>
            )}
          </div>
  
          {/* Phone */}
          <div>
            <label className="block font-bold">Phone</label>
            {isEditing ? (
              <input
                type="text"
                name="phone"
                value={formData.phone}
                onChange={handleInputChange}
                className="p-2 border rounded w-full"
              />
            ) : (
              <p>{accountDetails.phone}</p>
            )}
          </div>
  
          {/* Country */}
          <div>
            <label className="block font-bold">Country</label>
            {isEditing ? (
              <select
                name="country"
                value={formData.country}
                onChange={handleInputChange}
                className="p-2 border rounded w-full"
              >
                {countries.map((country) => (
                  <option key={country} value={country}>
                    {country}
                  </option>
                ))}
              </select>
            ) : (
              <p>{accountDetails.country}</p>
            )}
          </div>
  
          {/* Save and Cancel Buttons */}
          {isEditing && (
            <div className="flex space-x-4 mt-4">
              <button
                onClick={handleUpdate}
                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                disabled={loading}
              >
                {loading ? "Saving..." : "Save"}
              </button>
              <button
                onClick={() => setIsEditing(false)}
                className="px-4 py-2 bg-gray-300 text-black rounded hover:bg-gray-400"
              >
                Cancel
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
  
};

export default AccountPage;
