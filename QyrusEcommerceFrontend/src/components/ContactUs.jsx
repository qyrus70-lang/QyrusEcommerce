import React, { useState } from "react";
import { authAPI } from "../services/api";
import { useUser } from "../context/UserContext";

const ContactUs = () => {
  const [comments, setComments] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const { email } = useUser();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      await authAPI.recordContact(email, comments);
      setSuccess("Thank you for your feedback! We will get back to you soon.");
      setComments(""); // Clear the comments field after successful submission
    } catch (err) {
      setError("Failed to submit your comments. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-4">Contact Us</h1>
      {error && <div className="text-red-500 mb-4">{error}</div>}
      {success && <div className="text-green-500 mb-4">{success}</div>}
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label htmlFor="comments" className="block font-bold mb-2">
            Comments
          </label>
          <textarea
            id="comments"
            name="comments"
            value={comments}
            onChange={(e) => setComments(e.target.value)}
            rows="6"
            className="w-full p-3 border rounded"
            placeholder="Write your comments here..."
            required
          ></textarea>
        </div>
        <button
          type="submit"
          disabled={loading}
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          {loading ? "Submitting..." : "Submit"}
        </button>
      </form>
    </div>
  );
};

export default ContactUs;
