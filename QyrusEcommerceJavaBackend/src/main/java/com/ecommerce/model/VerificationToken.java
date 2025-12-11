package com.ecommerce.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    private String token;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String otp;
    private LocalDateTime expiryDate;
    private boolean used;
} 