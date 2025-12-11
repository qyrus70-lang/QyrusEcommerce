package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class VerifyEmailRequest {
    @NotBlank
    private String otp;
    
    @NotBlank
    private String token;
} 