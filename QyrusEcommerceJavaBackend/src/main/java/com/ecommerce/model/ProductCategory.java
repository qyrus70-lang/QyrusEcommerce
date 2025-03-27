package com.ecommerce.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_categories")
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ElementCollection
    @CollectionTable(name = "product_subcategories")
    private List<String> subcategories;

    public ProductCategory(String name, List<String> subcategories) {
        this.name = name;
        this.subcategories = subcategories;
    }
}