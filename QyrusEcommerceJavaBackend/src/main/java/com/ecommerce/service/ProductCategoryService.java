package com.ecommerce.service;

import com.ecommerce.model.ProductCategory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductCategoryService {
    
    public List<ProductCategory> getAllCategories() {
        return Arrays.asList(
            new ProductCategory("Men", Arrays.asList("T-Shirts", "Jeans", "Shirts")),
            new ProductCategory("Women", Arrays.asList("Dresses", "Tops", "Skirts")),
            new ProductCategory("Kids", Arrays.asList("Toys", "Clothing", "Books")),
            new ProductCategory("Accessories", Arrays.asList("Watches", "Bags", "Jewelry"))
        );
    }
} 