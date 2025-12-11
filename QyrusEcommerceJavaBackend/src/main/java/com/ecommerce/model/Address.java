package com.ecommerce.model;

import lombok.Data;
import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String address;
    
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
} 