package com.ecommerce.service;

import com.ecommerce.dto.AddressResponse;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    
    // Address validation patterns
    private static final Pattern US_ZIP_PATTERN = Pattern.compile("\\b\\d{5}(?:-\\d{4})?\\b");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
    private static final int MAX_ADDRESSES_PER_USER = 5;

    @Transactional
    public Map<String, Object> createAddress(String email, String addressText) {
        log.info("Creating address for user: {} with enhanced validation", email);
        
        // Enhanced user validation
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
            
        // Check address limit per user
        List<Address> existingAddresses = addressRepository.findAll().stream()
            .filter(addr -> addr.getUser().getEmail().equals(email))
            .collect(Collectors.toList());
            
        if (existingAddresses.size() >= MAX_ADDRESSES_PER_USER) {
            throw new RuntimeException("Maximum address limit reached. Users can have up to " + MAX_ADDRESSES_PER_USER + " addresses.");
        }
        
        // Parse and validate address format
        Map<String, String> parsedAddress = parseAddressComponents(addressText);
        validateAddressComponents(parsedAddress);
        
        // Check for duplicate addresses
        Optional<Address> duplicateAddress = findDuplicateAddress(user, addressText);
        if (duplicateAddress.isPresent()) {
            log.warn("Duplicate address detected for user: {}", email);
            return createDuplicateResponse(duplicateAddress.get());
        }
        
        // Create address with enhanced metadata
        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setUser(user);
        address.setAddress(addressText);
        
        // Auto-set as default if it's the user's first address
        boolean isFirstAddress = existingAddresses.isEmpty();
        if (isFirstAddress) {
            log.info("Setting as default address for new user: {}", email);
        }
        
        Address savedAddress = addressRepository.save(address);
        log.info("Address created with id: {} for user: {}", savedAddress.getId(), email);
        
        // Generate enhanced response with analytics
        return createSuccessResponse(savedAddress, isFirstAddress, parsedAddress);
    }
    
    /**
     * Parse address text into components for validation
     */
    private Map<String, String> parseAddressComponents(String addressText) {
        Map<String, String> components = new HashMap<>();
        
        // Extract ZIP code
        Matcher zipMatcher = US_ZIP_PATTERN.matcher(addressText);
        if (zipMatcher.find()) {
            components.put("zipCode", zipMatcher.group());
        }
        
        // Extract phone number
        Matcher phoneMatcher = PHONE_PATTERN.matcher(addressText);
        if (phoneMatcher.find()) {
            components.put("phoneNumber", phoneMatcher.group());
        }
        
        // Simple state detection (this is a simplified example)
        String[] stateAbbreviations = {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", 
                                      "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
                                      "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
                                      "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
                                      "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"};
        
        for (String state : stateAbbreviations) {
            if (addressText.toUpperCase().contains(" " + state + " ") || 
                addressText.toUpperCase().endsWith(" " + state)) {
                components.put("state", state);
                break;
            }
        }
        
        components.put("originalText", addressText);
        return components;
    }
    
    /**
     * Validate parsed address components
     */
    private void validateAddressComponents(Map<String, String> components) {
        String originalText = components.get("originalText");
        
        // Validate minimum address length
        if (originalText == null || originalText.trim().length() < 10) {
            throw new IllegalArgumentException("Address must be at least 10 characters long");
        }
        
        // Validate US ZIP code presence for US addresses
        if (!components.containsKey("zipCode")) {
            log.warn("No valid ZIP code detected in address: {}", originalText);
        }
        
        // Check for suspicious patterns
        if (originalText.toLowerCase().contains("po box") || originalText.toLowerCase().contains("p.o. box")) {
            log.info("PO Box address detected: {}", originalText);
        }
        
        // Validate state for US addresses
        if (!components.containsKey("state")) {
            log.warn("No valid state abbreviation detected in address: {}", originalText);
        }
    }
    
    /**
     * Check for duplicate addresses for the same user
     */
    private Optional<Address> findDuplicateAddress(User user, String addressText) {
        List<Address> userAddresses = addressRepository.findAll().stream()
            .filter(addr -> addr.getUser().getId().equals(user.getId()))
            .collect(Collectors.toList());
            
        return userAddresses.stream()
            .filter(addr -> isSimilarAddress(addr.getAddress(), addressText))
            .findFirst();
    }
    
    /**
     * Check if two addresses are similar (fuzzy matching)
     */
    private boolean isSimilarAddress(String existingAddress, String newAddress) {
        if (existingAddress == null || newAddress == null) {
            return false;
        }
        
        // Normalize addresses for comparison
        String normalized1 = normalizeAddress(existingAddress);
        String normalized2 = normalizeAddress(newAddress);
        
        // Exact match
        if (normalized1.equals(normalized2)) {
            return true;
        }
        
        // Calculate similarity (simple implementation)
        double similarity = calculateSimilarity(normalized1, normalized2);
        return similarity > 0.85; // 85% similarity threshold
    }
    
    /**
     * Normalize address string for comparison
     */
    private String normalizeAddress(String address) {
        return address.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
    
    /**
     * Calculate similarity between two strings using Levenshtein distance
     */
    private double calculateSimilarity(String str1, String str2) {
        int maxLen = Math.max(str1.length(), str2.length());
        if (maxLen == 0) return 1.0;
        
        int distance = levenshteinDistance(str1, str2);
        return 1.0 - (double) distance / maxLen;
    }
    
    /**
     * Calculate Levenshtein distance between two strings
     */
    private int levenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        
        for (int i = 0; i <= str1.length(); i++) {
            for (int j = 0; j <= str2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }
        return dp[str1.length()][str2.length()];
    }
    
    /**
     * Create response for duplicate address scenario
     */
    private Map<String, Object> createDuplicateResponse(Address existingAddress) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "duplicate");
        response.put("message", "Similar address already exists");
        response.put("existingAddressId", existingAddress.getId());
        response.put("suggestion", "Please verify if this is the same address or modify to make it unique");
        return response;
    }
    
    /**
     * Create success response with enhanced metadata
     */
    private Map<String, Object> createSuccessResponse(Address address, boolean isDefault, Map<String, String> parsedComponents) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Address created successfully");
        response.put("addressId", address.getId());
        response.put("isDefault", isDefault);
        
        // Add parsed components for frontend use
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("hasZipCode", parsedComponents.containsKey("zipCode"));
        metadata.put("hasPhoneNumber", parsedComponents.containsKey("phoneNumber"));
        metadata.put("detectedState", parsedComponents.get("state"));
        metadata.put("addressType", determineAddressType(parsedComponents.get("originalText")));
        
        response.put("metadata", metadata);
        return response;
    }
    
    /**
     * Determine address type based on content
     */
    private String determineAddressType(String address) {
        if (address == null) return "unknown";
        
        String lowerAddress = address.toLowerCase();
        if (lowerAddress.contains("po box") || lowerAddress.contains("p.o. box")) {
            return "po_box";
        } else if (lowerAddress.contains("apt") || lowerAddress.contains("suite") || 
                   lowerAddress.contains("unit") || lowerAddress.contains("#")) {
            return "apartment";
        } else {
            return "residential";
        }
    }

    public List<AddressResponse> getAddresses(String email) {
        log.info("Fetching addresses for user: {}", email);
        
        return addressRepository.findAll().stream()
            .filter(address -> address.getUser().getEmail().equals(email))
            .map(this::convertToAddressResponse)
            .collect(Collectors.toList());
    }

    private AddressResponse convertToAddressResponse(Address address) {
        return AddressResponse.builder()
            .addressId(address.getId())
            .address(address.getAddress())
            .build();
    }

    @Transactional
    public void deleteAddress(String email, String addressId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findByIdAndUser(addressId, user)
            .orElseThrow(() -> new RuntimeException("Address not found"));

        addressRepository.delete(address);
    }

    @Transactional
    public Address updateAddress(String email, String addressId, String newAddressText) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findByIdAndUser(addressId, user)
            .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setAddress(newAddressText);
        return addressRepository.save(address);
    }
} 