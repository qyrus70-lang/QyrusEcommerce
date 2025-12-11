package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull
    private String email;
    
    @NotNull
    private String addressId;
    
    @NotNull
    private String paymentMethod;
    
    @NotEmpty
    private List<ProductOrder> products;

    @Data
    public static class ProductOrder {
        @NotNull
        private Long productId;
        private Integer quantity;
        private String color;
        private String size;
        private String provider;
    }
} 