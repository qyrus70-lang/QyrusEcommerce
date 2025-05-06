package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;

@Data
public class AddressRequest {
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String user_email;
    
    @NotNull(message = "Address is required")
    private String user_address;
} 