package com.ecommerce.service;

import com.ecommerce.model.Favorite;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.FavoriteRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addFavorite(String email, Long productId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Product already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
    }

    public List<Long> getFavorites(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return favoriteRepository.findByUser(user).stream()
            .map(favorite -> favorite.getProduct().getId())
            .collect(Collectors.toList());
    }

    @Transactional
    public void removeFavorite(String email, Long productId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        favoriteRepository.deleteByUserAndProduct(user, product);
    }
} 