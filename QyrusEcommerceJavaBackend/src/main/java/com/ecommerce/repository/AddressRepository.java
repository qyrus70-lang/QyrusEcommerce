package com.ecommerce.repository;

import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String> {
    List<Address> findByUser(User user);
    Optional<Address> findByIdAndUser(String id, User user);
} 