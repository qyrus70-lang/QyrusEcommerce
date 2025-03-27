package com.ecommerce.controller;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.SignupRequest;
import com.ecommerce.dto.VerifyEmailRequest;
import com.ecommerce.service.AuthService;
import com.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private static final Logger log = Logger.getLogger(AuthController.class.getName());

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        userService.login(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody SignupRequest request) {
        String token = userService.signup(request);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Signup successful. Please verify your email.");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        userService.verifyEmail(request.getToken(), request.getOtp());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email verified successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> requestMap) {
        String email = requestMap.get("email");
        
        // Call the service to handle password reset logic
        String token = authService.initiatePasswordReset(email);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset instructions sent to your email");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> requestMap) {
        String password = requestMap.get("password");
        String otp = requestMap.get("otp");
        
        // Call the service to handle password reset
        authService.resetPassword(password, otp);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        
        return ResponseEntity.ok(response);
    }
} 