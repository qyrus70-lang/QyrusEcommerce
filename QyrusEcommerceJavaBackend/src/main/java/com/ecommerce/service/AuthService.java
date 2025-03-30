package com.ecommerce.service;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    // You might need to inject an email service
    // @Autowired
    // private EmailService emailService;

    public String generatePasswordResetToken(String email) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Generate a reset token
        String token = UUID.randomUUID().toString();
        
        // In a real application, you would:
        // 1. Store the token in the database
        // 2. Set an expiration time
        // 3. Send an email with the reset link

        // Sample comment
        
        // For this implementation, we'll just return the token
        return token;
    }
    
    public String initiatePasswordReset(String email) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Generate a reset token
        String token = UUID.randomUUID().toString();
        
        // Use fixed OTP value
        String otp = "reset123";
        
        // Set the token, OTP and expiration time in the user entity
        user.setResetToken(token);
        user.setResetOtp(otp);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24)); // Token valid for 24 hours
        
        // Save the updated user
        userRepository.save(user);
        
        // In a real application, you would send an email with the reset link and OTP
        // emailService.sendPasswordResetEmail(user.getEmail(), token, otp);
        
        return token;
    }
    
    public void resetPassword(String newPassword, String otp) {
        // Find user by OTP
        User user = userRepository.findByResetOtp(otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        
        // Check if token is expired
        if (user.getResetTokenExpiry() == null || 
            user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }
        
        // Update password with a simple hash
        user.setPassword(hashPassword(newPassword));
        
        // Clear reset token data
        user.setResetToken(null);
        user.setResetOtp(null);
        user.setResetTokenExpiry(null);
        
        // Save updated user
        userRepository.save(user);
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
} 