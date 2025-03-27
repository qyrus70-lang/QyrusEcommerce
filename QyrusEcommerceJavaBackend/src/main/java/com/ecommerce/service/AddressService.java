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

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public String createAddress(String email, String addressText) {
        log.info("Creating address for user: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
            
        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setUser(user);
        address.setAddress(addressText);
        
        addressRepository.save(address);
        log.info("Address created with id: {}", address.getId());
        
        return address.getId();
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