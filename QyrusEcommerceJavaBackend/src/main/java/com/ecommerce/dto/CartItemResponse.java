package com.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    @JsonProperty("cart_item_id")
    private String cartItemId;
    
    @JsonProperty("product_id")
    private Long productId;
    
    private String name;
    private double price;
    private String image;
    private String color;
    private String provider;
    private String size;
    private int quantity;
} 