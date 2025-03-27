package com.ecommerce.controller;

import com.ecommerce.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/record-contact")
    public ResponseEntity<Map<String, String>> recordContact(@RequestBody Map<String, String> requestMap) {
        String email = requestMap.get("email");
        String comments = requestMap.get("comments");
        
        log.info("Recording contact from user: {}", email);
        
        contactService.recordContact(email, comments);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contact recorded successfully");
        response.put("email", email);
        
        return ResponseEntity.ok(response);
    }
} 