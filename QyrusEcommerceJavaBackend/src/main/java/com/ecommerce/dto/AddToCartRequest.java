package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddToCartRequest {
    @NotBlank
    @Email
    private String email;
    
    @NotNull
    private Long productId;
    
    @NotBlank
    private String color;
    
    @NotBlank
    private String provider;
    
    @NotBlank
    private String size;
    
    @NotNull
    @Min(1)
    private Integer quantity;
} 