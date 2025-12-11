package com.ecommerce.model;

import lombok.Data;
import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private String color;
    private String provider;
    private String size;
    private Integer quantity;
    
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
} 