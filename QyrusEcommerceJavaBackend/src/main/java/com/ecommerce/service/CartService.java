package com.ecommerce.service;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartItem addToCart(AddToCartRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setColor(request.getColor());
        cartItem.setProvider(request.getProvider());
        cartItem.setSize(request.getSize());
        cartItem.setQuantity(request.getQuantity());

        return cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCart(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    public void removeFromCart(String email, String cartItemId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        cartItemRepository.deleteByIdAndUser(cartItemId, user);
    }
} 