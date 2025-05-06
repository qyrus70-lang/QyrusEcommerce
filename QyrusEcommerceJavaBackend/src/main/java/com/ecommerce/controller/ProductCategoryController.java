package com.ecommerce.controller;

import com.ecommerce.model.ProductCategory;
import com.ecommerce.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @GetMapping("/get-product-categories/")
    public ResponseEntity<Map<String, Map<String, List<String>>>> getProductCategories() {
        List<ProductCategory> categories = productCategoryService.getAllCategories();
        
        Map<String, List<String>> categoryMap = new HashMap<>();
        for (ProductCategory category : categories) {
            categoryMap.put(category.getName(), category.getSubcategories());
        }
        
        return ResponseEntity.ok(Map.of("categories", categoryMap));
    }
}