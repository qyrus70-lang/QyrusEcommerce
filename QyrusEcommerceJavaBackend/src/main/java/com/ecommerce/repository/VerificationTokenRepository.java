package com.ecommerce.repository;

import com.ecommerce.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByTokenAndOtp(String token, String otp);
} 