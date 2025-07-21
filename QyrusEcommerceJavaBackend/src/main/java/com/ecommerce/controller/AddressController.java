package com.ecommerce.controller;

import com.ecommerce.dto.AddressRequest;
import com.ecommerce.dto.AddressResponse;
import com.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/create-address/")
    public ResponseEntity<Map<String, Object>> createAddress(@Valid @RequestBody AddressRequest request) {
        log.info("Processing address creation request for user: {} at {}", 
                request.getEmail(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        try {
            // Enhanced input validation
            validateAddressRequest(request);
            
            // Call service with enhanced logic
            Map<String, Object> serviceResponse = addressService.createAddress(request.getEmail(), request.getAddress());
            
            // Handle different response scenarios
            if ("duplicate".equals(serviceResponse.get("status"))) {
                return handleDuplicateAddressResponse(request, serviceResponse);
            } else if ("success".equals(serviceResponse.get("status"))) {
                return handleSuccessfulAddressCreation(request, serviceResponse);
            } else {
                return handleUnexpectedResponse(serviceResponse);
            }
            
        } catch (IllegalArgumentException e) {
            log.error("Validation error for user {}: {}", request.getEmail(), e.getMessage());
            return createValidationErrorResponse(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Business logic error for user {}: {}", request.getEmail(), e.getMessage());
            return createBusinessErrorResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error creating address for user {}: {}", request.getEmail(), e.getMessage(), e);
            return createSystemErrorResponse();
        }
    }
    
    /**
     * Enhanced validation for address creation requests
     */
    private void validateAddressRequest(AddressRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required and cannot be empty");
        }
        
        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required and cannot be empty");
        }
        
        // Additional business validation
        if (request.getAddress().length() > 500) {
            throw new IllegalArgumentException("Address cannot exceed 500 characters");
        }
        
        // Check for potentially harmful content
        String address = request.getAddress().toLowerCase();
        if (address.contains("<script") || address.contains("javascript:") || address.contains("sql")) {
            throw new IllegalArgumentException("Invalid characters detected in address");
        }
        
        log.debug("Address request validation passed for user: {}", request.getEmail());
    }
    
    /**
     * Handle duplicate address detection response
     */
    private ResponseEntity<Map<String, Object>> handleDuplicateAddressResponse(
            AddressRequest request, Map<String, Object> serviceResponse) {
        
        log.info("Duplicate address detected for user: {}", request.getEmail());
        
        // Get current addresses for context
        List<AddressResponse> currentAddresses = addressService.getAddresses(request.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error_code", "DUPLICATE_ADDRESS");
        response.put("message", serviceResponse.get("message"));
        response.put("details", serviceResponse);
        response.put("existing_address_id", serviceResponse.get("existingAddressId"));
        response.put("suggestion", serviceResponse.get("suggestion"));
        response.put("current_addresses", currentAddresses);
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Analytics for duplicate detection
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("user_email", request.getEmail());
        analytics.put("attempted_address", request.getAddress());
        analytics.put("duplicate_detection", true);
        analytics.put("current_address_count", currentAddresses.size());
        response.put("analytics", analytics);
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    /**
     * Handle successful address creation
     */
    private ResponseEntity<Map<String, Object>> handleSuccessfulAddressCreation(
            AddressRequest request, Map<String, Object> serviceResponse) {
        
        log.info("Address successfully created for user: {}", request.getEmail());
        
        // Get updated address list
        List<AddressResponse> allAddresses = addressService.getAddresses(request.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", serviceResponse.get("message"));
        response.put("address_id", serviceResponse.get("addressId"));
        response.put("is_default", serviceResponse.get("isDefault"));
        response.put("addresses", allAddresses);
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Include metadata from service
        if (serviceResponse.containsKey("metadata")) {
            response.put("address_analysis", serviceResponse.get("metadata"));
        }
        
        // Enhanced analytics for successful creation
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("user_email", request.getEmail());
        analytics.put("address_created", true);
        analytics.put("total_addresses", allAddresses.size());
        analytics.put("is_first_address", serviceResponse.get("isDefault"));
        analytics.put("creation_timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Add metadata analysis to analytics
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) serviceResponse.get("metadata");
        if (metadata != null) {
            analytics.put("address_has_zipcode", metadata.get("hasZipCode"));
            analytics.put("address_has_phone", metadata.get("hasPhoneNumber"));
            analytics.put("detected_state", metadata.get("detectedState"));
            analytics.put("address_type", metadata.get("addressType"));
        }
        
        response.put("analytics", analytics);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Handle unexpected service response
     */
    private ResponseEntity<Map<String, Object>> handleUnexpectedResponse(Map<String, Object> serviceResponse) {
        log.error("Unexpected response from address service: {}", serviceResponse);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error_code", "UNEXPECTED_RESPONSE");
        response.put("message", "An unexpected error occurred while processing your address");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("service_response", serviceResponse);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * Create validation error response
     */
    private ResponseEntity<Map<String, Object>> createValidationErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error_code", "VALIDATION_ERROR");
        response.put("message", "Request validation failed");
        response.put("details", errorMessage);
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Create business logic error response
     */
    private ResponseEntity<Map<String, Object>> createBusinessErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error_code", "BUSINESS_LOGIC_ERROR");
        response.put("message", errorMessage);
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
    
    /**
     * Create system error response
     */
    private ResponseEntity<Map<String, Object>> createSystemErrorResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error_code", "SYSTEM_ERROR");
        response.put("message", "An internal system error occurred. Please try again later.");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @GetMapping("/get-addresses")
    public ResponseEntity<Map<String, Object>> getAddresses(@RequestParam @NotBlank @Email String email) {
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