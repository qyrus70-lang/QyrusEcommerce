package com.ecommerce.controller;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.model.CartItem;
import com.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add-to-cart")
    public ResponseEntity<Map<String, Object>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        CartItem cartItem = cartService.addToCart(request);
        List<CartItem> updatedCart = cartService.getCart(request.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item added to cart successfully");
        response.put("cart", updatedCart);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-cart")
    public ResponseEntity<Map<String, Object>> getCart(@RequestParam String email) {
        List<CartItem> cart = cartService.getCart(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("cart", cart);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-from-cart")
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @RequestParam String email,
            @RequestParam String cartItemId) {
        cartService.removeFromCart(email, cartItemId);
        List<CartItem> updatedCart = cartService.getCart(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item removed from cart successfully");
        response.put("cart", updatedCart);
        return ResponseEntity.ok(response);
    }
} 