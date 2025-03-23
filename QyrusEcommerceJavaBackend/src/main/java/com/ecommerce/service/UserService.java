package com.ecommerce.service;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.SignupRequest;
import com.ecommerce.model.User;
import com.ecommerce.model.VerificationToken;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Transactional
    public String signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // In real app, encrypt password
        user.setVerified(false);
        userRepository.save(user);

        // Create verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setOtp("123456"); // In real app, generate random OTP
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(verificationToken);

        return token;
    }

    public void login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) { // In real app, use password encoder
            throw new RuntimeException("Invalid password");
        }

        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified");
        }
    }

    @Transactional
    public void verifyEmail(String token, String otp) {
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndOtp(token, otp)
            .orElseThrow(() -> new RuntimeException("Invalid token or OTP"));

        if (verificationToken.isUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);
    }
} 