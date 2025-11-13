package org.genc.usermgmt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.AuthRequestDTO;
import org.genc.usermgmt.dto.AuthResponseDTO;
import org.genc.usermgmt.dto.CustomUserDetails;
import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.service.api.AuthService;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.genc.usermgmt.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMgmtService userMgmtService;

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {
        try {
            log.info("Login attempt for user: {}", request.getUsername());

            // Authenticate user with Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Get authenticated user details
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Generate JWT token
            String jwtToken = jwtUtil.generateToken(userDetails);

            log.info("User '{}' logged in successfully", request.getUsername());

            // Return response
            return AuthResponseDTO.builder()
                    .jwt(jwtToken)
                    .userId(userDetails.getUserId())
                    .firstName(userDetails.getFirstName())
                    .lastName(userDetails.getLastName())
                    .email(userDetails.getEmail())
                    .contact(userDetails.getContact())
                    .appInstance("onepiece-auction")
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", request.getUsername());
            throw new IllegalArgumentException("Invalid username or password");
        } catch (Exception e) {
            log.error("Authentication error for user: {} - {}", request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Authentication error occurred");
        }
    }

    @Override
    public AuthResponseDTO register(UserRegistrationRequestDTO request) {
        try {
            log.info("Registration attempt for username: {}", request.getUsername());

            // Register new user (automatically assigned BUYER role)
            User savedUser = userMgmtService.registerNewUser(request);

            log.info("User '{}' registered successfully with BUYER role", request.getUsername());

            return AuthResponseDTO.builder()
                    .userId(savedUser.getUserId())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .email(savedUser.getEmail())
                    .contact(savedUser.getContact())
                    .appInstance("onepiece-auction")
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Registration validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage(), e);
            throw new RuntimeException("Registration error occurred");
        }
    }
}