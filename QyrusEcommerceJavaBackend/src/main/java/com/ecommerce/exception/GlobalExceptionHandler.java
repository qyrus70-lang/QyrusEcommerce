package com.ecommerce.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception e) {
        log.error("Unexpected error occurred: ", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());
        response.put("stackTrace", e.getStackTrace());
        
        return ResponseEntity.internalServerError().body(response);
    }
} 