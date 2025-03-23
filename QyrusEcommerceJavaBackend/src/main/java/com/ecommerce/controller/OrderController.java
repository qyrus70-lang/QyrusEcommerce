package com.ecommerce.controller;

import com.ecommerce.dto.CreateOrderRequest;
import com.ecommerce.model.Order;
import com.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order created successfully");
        response.put("order_id", order.getId());
        response.put("order_status", order.getStatus());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-orders")
    public ResponseEntity<Map<String, Object>> getOrders(@RequestParam String email) {
        List<Order> orders = orderService.getOrders(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("orders", orders);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel-order")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @RequestParam String email,
            @RequestParam String orderId) {
        Order cancelledOrder = orderService.cancelOrder(email, orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order cancelled successfully");
        response.put("order_id", cancelledOrder.getId());
        response.put("order_status", cancelledOrder.getStatus());
        return ResponseEntity.ok(response);
    }
} 