package com.ecommerce.service;

import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public Address createAddress(String email, String addressText) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = new Address();
        address.setUser(user);
        address.setAddress(addressText);
        return addressRepository.save(address);
    }

    public List<Address> getAddresses(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return addressRepository.findByUser(user);
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