package com.ecommerce.service;

import com.ecommerce.dto.CreateOrderRequest;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findByIdAndUser(request.getAddressId(), user)
            .orElseThrow(() -> new RuntimeException("Address not found"));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus("confirmed");

        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderRequest.ProductOrder productOrder : request.getProducts()) {
            Product product = productRepository.findById(productOrder.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(productOrder.getQuantity());
            orderItem.setColor(productOrder.getColor());
            orderItem.setSize(productOrder.getSize());
            orderItem.setProvider(productOrder.getProvider());
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        return orderRepository.save(order);
    }

    public List<Order> getOrders(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUser(user);
    }

    @Transactional
    public Order cancelOrder(String email, String orderId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findByIdAndUser(orderId, user)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("cancelled".equals(order.getStatus())) {
            throw new RuntimeException("Order is already cancelled");
        }

        order.setStatus("cancelled");
        return orderRepository.save(order);
    }
} 