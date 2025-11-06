package com.onepiece.user_service.controller;


import com.onepiece.user_service.dto.*;
import com.onepiece.user_service.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/userservice")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        
        try {
            logger.info("Registration attempt for username: {}", registerRequest.getUsername());
            
            RegisterResponseDTO response = authService.register(registerRequest);
            
            logger.info("User registered successfully: {}", registerRequest.getUsername());
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "User registered successfully");
            successResponse.put("data", response);
            successResponse.put("timestamp", LocalDateTime.now());
            
            return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
            
        } catch (Exception e) {
            logger.error("Registration failed for username: {} - Error: {}", 
                        registerRequest.getUsername(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        
        try {
            logger.info("Login attempt for username: {}", loginRequest.getUsername());
            
            LoginResponseDTO response = authService.login(loginRequest);
            
            logger.info("User logged in successfully: {}", loginRequest.getUsername());
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Login successful");
            successResponse.put("data", response);
            successResponse.put("timestamp", LocalDateTime.now());
            
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Login failed for username: {} - Error: {}", 
                        loginRequest.getUsername(), e.getMessage());
            throw e;
        }
    }


    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        
        try {
            logger.info("Password reset attempt for username: {}", request.getUsername());
            
            boolean isReset = authService.resetPassword(
                request.getUsername(), 
                request.getOldPassword(), 
                request.getNewPassword()
            );
            
            if (isReset) {
                logger.info("Password reset successful for username: {}", request.getUsername());
                
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", "Password reset successfully");
                successResponse.put("timestamp", LocalDateTime.now());
                
                return new ResponseEntity<>(successResponse, HttpStatus.OK);
            } else {
                logger.warn("Password reset failed for username: {} - Invalid credentials", request.getUsername());
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid username or current password");
                errorResponse.put("timestamp", LocalDateTime.now());
                
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
        } catch (Exception e) {
            logger.error("Password reset error for username: {} - Error: {}", 
                        request.getUsername(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            logger.info("User logout requested");
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Logged out successfully");
            successResponse.put("timestamp", LocalDateTime.now());
            
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Logout error: {}", e.getMessage());
            throw e;
        }
    }


    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid authorization header");
                errorResponse.put("timestamp", LocalDateTime.now());
                
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }
            
            String token = authHeader.substring(7);
            
            if (token.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Token is empty");
                errorResponse.put("timestamp", LocalDateTime.now());
                
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Token format is valid");
            successResponse.put("token", token.substring(0, Math.min(token.length(), 20)) + "...");
            successResponse.put("timestamp", LocalDateTime.now());
            
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> healthResponse = new HashMap<>();
        healthResponse.put("status", "UP");
        healthResponse.put("service", "Authentication Service");
        healthResponse.put("timestamp", LocalDateTime.now());
        
        return new ResponseEntity<>(healthResponse, HttpStatus.OK);
    }
}
