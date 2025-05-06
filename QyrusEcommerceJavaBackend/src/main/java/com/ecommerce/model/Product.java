package com.ecommerce.model;

import lombok.Data;
import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Double price;
    private String image;
    private String category;
    private String subcategory;
    private String description;
    private Integer rating;
    
    @ElementCollection
    private Set<String> sizes;
    
    @ElementCollection
    @CollectionTable(name = "product_colors")
    private List<Color> colors;
    
    @ElementCollection
    @Column(name = "providers")
    private Set<String> distributors;
    
    @ElementCollection
    private List<String> comments;
}

@Embeddable
@Data
class Color {
    private String name;
    private String hex;
} 