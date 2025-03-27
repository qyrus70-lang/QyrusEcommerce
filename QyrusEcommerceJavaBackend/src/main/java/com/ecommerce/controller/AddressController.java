package com.ecommerce.controller;

import com.ecommerce.dto.AddressRequest;
import com.ecommerce.dto.AddressResponse;
import com.ecommerce.service.AddressService;
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
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/create-address")
    public ResponseEntity<Map<String, Object>> createAddress(@RequestBody AddressRequest request) {
        log.info("Creating address for user: {}", request.getEmail());
        
        String addressId = addressService.createAddress(request.getEmail(), request.getAddress());
        List<AddressResponse> addresses = addressService.getAddresses(request.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Address added successfully");
        response.put("address_id", addressId);
        response.put("addresses", addresses);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-addresses")
    public ResponseEntity<Map<String, Object>> getAddresses(@RequestParam String email) {
        log.info("Fetching addresses for user: {}", email);
        
        List<AddressResponse> addresses = addressService.getAddresses(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("addresses", addresses);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-address")
    public ResponseEntity<Map<String, Object>> deleteAddress(@RequestBody Map<String, String> requestMap) {
        String email = requestMap.get("email");
        String addressId = requestMap.get("addressId");
        
        log.info("Deleting address for user: {}, addressId: {}", email, addressId);
        
        addressService.deleteAddress(email, addressId);
        List<AddressResponse> addresses = addressService.getAddresses(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Address deleted successfully");
        response.put("addresses", addresses);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-address")
    public ResponseEntity<Map<String, Object>> updateAddress(@RequestBody Map<String, String> requestMap) {
        String email = requestMap.get("email");
        String addressId = requestMap.get("addressId");
        String address = requestMap.get("newAddress");
        
        log.info("Updating address for user: {}", email);
        
        addressService.updateAddress(email, addressId, address);
        List<AddressResponse> addresses = addressService.getAddresses(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Address updated successfully");
        response.put("addresses", addresses);
        return ResponseEntity.ok(response);
    }
} 