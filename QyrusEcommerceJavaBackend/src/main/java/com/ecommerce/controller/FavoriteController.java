package com.ecommerce.controller;

import com.ecommerce.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/add-favorite/")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @RequestParam String email,
            @RequestParam Long productId) {
        favoriteService.addFavorite(email, productId);
        List<Long> favorites = favoriteService.getFavorites(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product added to favorites successfully");
        response.put("favorites", favorites);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-favorites/")
    public ResponseEntity<Map<String, Object>> getFavorites(@RequestParam String email) {
        List<Long> favorites = favoriteService.getFavorites(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("favorites", favorites);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-favorite/")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @RequestParam String email,
            @RequestParam Long productId) {
        favoriteService.removeFavorite(email, productId);
        List<Long> favorites = favoriteService.getFavorites(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product removed from favorites successfully");
        response.put("favorites", favorites);
        return ResponseEntity.ok(response);
    }
} 