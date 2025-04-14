package com.ecommerce.service;

import com.ecommerce.entity.Contact;
import com.ecommerce.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {
    
    private final ContactRepository contactRepository;
    private final JavaMailSender emailSender;
    
    public void recordContact(String email, String comments) {
        Contact contact = Contact.builder()
                .email(email)
                .comments(comments)
                .build();
        
        contactRepository.save(contact);
        sendThankYouEmail(email);
        log.info("Contact recorded for email: {}", email);
    }
    
    private void sendThankYouEmail(String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Thank You for Contacting Us");
            message.setText("Dear Customer,\n\nThank you for reaching out to us. We have received your message and will get back to you as soon as possible.\n\nBest regards,\nThe Qyrus Team");
            
            emailSender.send(message);
            log.info("Thank you email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send thank you email to: {}", email, e);
            // Continue execution even if email sending fails
        }
    }
} 