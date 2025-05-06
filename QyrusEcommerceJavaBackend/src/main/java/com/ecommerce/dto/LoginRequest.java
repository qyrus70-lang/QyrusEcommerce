package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String user_email;
    
    @NotBlank
    private String user_password;
} 