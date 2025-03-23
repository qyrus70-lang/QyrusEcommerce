package com.ecommerce.controller;

import com.ecommerce.model.Address;
import com.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/create-address")
    public ResponseEntity<Map<String, Object>> createAddress(
            @RequestParam String email,
            @RequestParam String address) {
        Address newAddress = addressService.createAddress(email, address);
        List<Address> addresses = addressService.getAddresses(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Address added successfully");
        response.put("address_id", newAddress.getId());
        response.put("addresses", addresses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-addresses")
    public ResponseEntity<Map<String, Object>> getAddresses(@RequestParam String email) {
        List<Address> addresses = addressService.getAddresses(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("addresses", addresses);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-address")
    public ResponseEntity<Map<String, Object>> deleteAddress(
            @RequestParam String email,
            @RequestParam String addressId) {
        addressService.deleteAddress(email, addressId);
        List<Address> addresses = addressService.getAddresses(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Address deleted successfully");
        response.put("addresses", addresses);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-address")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @RequestParam String email,
            @RequestParam String addressId,
            @RequestParam String newAddress) {
        addressService.updateAddress(email, addressId, newAddress);
        List<Address> addresses = addressService.getAddresses(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Address updated successfully");
        response.put("addresses", addresses);
        return ResponseEntity.ok(response);
    }
} 