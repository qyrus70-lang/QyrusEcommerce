import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { authAPI } from "../services/api";
import { useUser } from "../context/UserContext";

const PaymentPage = () => {
  const [paymentMethod, setPaymentMethod] = useState("");
  const [cardDetails, setCardDetails] = useState({ number: "", expiry: "", cvv: "" });
  const [upiId, setUpiId] = useState("");
  const location = useLocation();
  const navigate = useNavigate();
  const { email } = useUser();

  const { addressId, products } = location.state || {};

  const handlePayment = async () => {
    if (!paymentMethod) {
      alert("Please select a payment method.");
      return;
    }

    if (paymentMethod === "UPI" && !upiId) {
      alert("Please enter your UPI ID.");
      return;
    }

    if ((paymentMethod === "Credit Card" || paymentMethod === "Debit Card") && (!cardDetails.number || !cardDetails.expiry || !cardDetails.cvv)) {
      alert("Please fill all card details.");
      return;
    }

    try {
      // Mock Create Order API Call
      const orderData = {
        email: email, // Replace with user's email
        addressId,
        products,
        paymentMethod,
      };

      await authAPI.createOrder(orderData);

      // Redirect to "My Orders" page after successful order creation
      navigate("/my-orders");
    } catch (err) {
      alert("Failed to place order. Please try again.");
    }
  };

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Payment</h1>

      <div className="space-y-4">
        <h2 className="text-xl font-bold">Select Payment Method</h2>
        {["Credit Card", "Debit Card", "UPI", "Net Banking"].map((method) => (
          <div key={method} className="flex items-center space-x-4">
            <input
              type="radio"
              name="paymentMethod"
              value={method}
              checked={paymentMethod === method}
              onChange={() => setPaymentMethod(method)}
              className="mr-2"
            />
            <label>{method}</label>
          </div>
        ))}
      </div>

      {/* Conditional Input for Payment Details */}
      {paymentMethod === "Credit Card" || paymentMethod === "Debit Card" ? (
        <div className="mt-6">
          <h3 className="font-bold">Enter Card Details</h3>
          <input
            type="text"
            placeholder="Card Number"
            value={cardDetails.number}
            onChange={(e) => setCardDetails({ ...cardDetails, number: e.target.value })}
            className="block w-full p-2 border rounded mb-2"
          />
          <input
            type="text"
            placeholder="Expiry Date (MM/YY)"
            value={cardDetails.expiry}
            onChange={(e) => setCardDetails({ ...cardDetails, expiry: e.target.value })}
            className="block w-full p-2 border rounded mb-2"
          />
          <input
            type="text"
            placeholder="CVV"
            value={cardDetails.cvv}
            onChange={(e) => setCardDetails({ ...cardDetails, cvv: e.target.value })}
            className="block w-full p-2 border rounded mb-2"
          />
        </div>
      ) : paymentMethod === "UPI" ? (
        <div className="mt-6">
          <h3 className="font-bold">Enter UPI ID</h3>
          <input
            type="text"
            placeholder="UPI ID"
            value={upiId}
            onChange={(e) => setUpiId(e.target.value)}
            className="block w-full p-2 border rounded"
          />
        </div>
      ) : null}

      <div className="mt-6 flex justify-end space-x-4">
        <button
          onClick={() => navigate(-1)}
          className="px-6 py-3 bg-gray-300 text-black rounded hover:bg-gray-400"
        >
          Cancel
        </button>
        <button
          onClick={handlePayment}
          className="px-6 py-3 bg-green-600 text-white rounded hover:bg-green-700"
        >
          Pay Now
        </button>
      </div>
    </div>
  );
};

export default PaymentPage;
