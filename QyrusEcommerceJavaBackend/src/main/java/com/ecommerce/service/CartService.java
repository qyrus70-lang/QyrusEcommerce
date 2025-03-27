package com.ecommerce.service;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addToCart(String email, Long productId, String color, String provider, String size, int quantity) {
        log.info("Adding item to cart for user: {}, product: {}", email, productId);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
            
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
            
        // Check if the same product with same attributes already exists in cart
        List<CartItem> existingItems = cartItemRepository.findAll().stream()
            .filter(item -> item.getUser().getEmail().equals(email) &&
                   item.getProduct().getId().equals(productId) &&
                   item.getColor().equals(color) &&
                   item.getProvider().equals(provider) &&
                   item.getSize().equals(size))
            .collect(Collectors.toList());
            
        if (!existingItems.isEmpty()) {
            // Update quantity of existing item
            CartItem existingItem = existingItems.get(0);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
            log.info("Updated quantity of existing cart item");
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setId(UUID.randomUUID().toString());
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setColor(color);
            cartItem.setProvider(provider);
            cartItem.setSize(size);
            cartItem.setQuantity(quantity);
            
            cartItemRepository.save(cartItem);
            log.info("Added new item to cart with id: {}", cartItem.getId());
        }
    }

    @Transactional
    public void removeFromCart(String email, String cartItemId) {
        if (email == null || cartItemId == null) {
            throw new IllegalArgumentException("Email and cartItemId must not be null");
        }

        log.info("Attempting to remove cart item: {} for user: {}", cartItemId, email);
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("Cart item does not belong to user");
        }

        cartItemRepository.delete(cartItem);
        cartItemRepository.flush();
        log.info("Successfully removed item from cart");
    }

    public List<CartItemResponse> getCartItems(String email) {
        log.info("Fetching cart for user {}", email);
        return cartItemRepository.findAll().stream()
            .filter(item -> item.getUser().getEmail().equals(email))
            .map(this::convertToCartItemResponse)
            .collect(Collectors.toList());
    }

    private CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        Product product = productRepository.findById(cartItem.getProduct().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
        return CartItemResponse.builder()
            .cartItemId(cartItem.getId())
            .productId(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .image(product.getImage())
            .color(cartItem.getColor())
            .provider(cartItem.getProvider())
            .size(cartItem.getSize())
            .quantity(cartItem.getQuantity())
            .build();
    }
} 