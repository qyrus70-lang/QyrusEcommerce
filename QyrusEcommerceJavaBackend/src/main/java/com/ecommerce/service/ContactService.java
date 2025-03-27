package com.ecommerce.service;

import com.ecommerce.entity.Contact;
import com.ecommerce.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {
    
    private final ContactRepository contactRepository;
    
    public void recordContact(String email, String comments) {
        Contact contact = Contact.builder()
                .email(email)
                .comments(comments)
                .build();
        
        contactRepository.save(contact);
        log.info("Contact recorded for email: {}", email);
    }
} 