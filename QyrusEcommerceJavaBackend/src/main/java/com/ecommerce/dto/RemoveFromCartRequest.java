package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class RemoveFromCartRequest {
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Cart item ID is required")
    @JsonProperty("cart_item_id")
    private String cartItemId;
} 