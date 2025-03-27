package com.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;

@Data
public class AddToCartRequest {
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Product ID is required")
    @JsonProperty("product_id")
    private Long productId;

    private String color;
    private String provider;
    private String size;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;
} 