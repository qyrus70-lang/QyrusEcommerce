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
    
    private String product_name;
    private Double product_price;
    private String product_image;
    private String product_category;
    private String product_subcategory;
    private String product_description;
    private Integer product_rating;
    
    @ElementCollection
    private Set<String> product_sizes;
    
    @ElementCollection
    @CollectionTable(name = "product_colors")
    private List<Color> product_colors;
    
    @ElementCollection
    @Column(name = "providers")
    private Set<String> product_distributors;
    
    @ElementCollection
    private List<String> product_comments;
}

@Embeddable
@Data
class Color {
    private String name;
    private String hex;
} 