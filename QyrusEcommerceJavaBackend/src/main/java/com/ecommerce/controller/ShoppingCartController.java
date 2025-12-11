package com.ecommerce.controller;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.dto.RemoveFromCartRequest;
import com.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartController {
    private final CartService cartService;

    @GetMapping("/get-cart")
    public ResponseEntity<Map<String, Object>> getCart(@RequestParam String email) {
        log.info("Fetching cart for user: {}", email);
        List<CartItemResponse> cart = cartService.getCartItems(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("cart", cart);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody AddToCartRequest request) {
        log.info("Adding item to cart: {}", request);
        
        if (request == null || request.getEmail() == null || request.getProductId() == null) {
            throw new IllegalArgumentException("Email and productId are required");
        }
        
        cartService.addToCart(
            request.getEmail(), 
            request.getProductId(), 
            request.getColor(), 
            request.getProvider(), 
            request.getSize(), 
            request.getQuantity()
        );
        
        List<CartItemResponse> updatedCart = cartService.getCartItems(request.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item added to cart successfully");
        response.put("cart", updatedCart);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-from-cart")
    public ResponseEntity<Map<String, Object>> removeFromCart(@RequestBody RemoveFromCartRequest request) {
        if (request == null || request.getEmail() == null || request.getCartItemId() == null) {
            throw new IllegalArgumentException("Email and cartItemId are required");
        }
        
        log.info("Removing item {} from cart for user {}", request.getCartItemId(), request.getEmail());
        cartService.removeFromCart(request.getEmail(), request.getCartItemId());
        List<CartItemResponse> updatedCart = cartService.getCartItems(request.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item removed from cart successfully");
        response.put("cart", updatedCart);
        return ResponseEntity.ok(response);
    }
} 