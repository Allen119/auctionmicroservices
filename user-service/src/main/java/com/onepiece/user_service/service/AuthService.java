package com.onepiece.user_service.service;


import com.onepiece.user_service.dto.LoginRequestDTO;
import com.onepiece.user_service.dto.LoginResponseDTO;
import com.onepiece.user_service.dto.RegisterRequestDTO;
import com.onepiece.user_service.dto.RegisterResponseDTO;
import com.onepiece.user_service.exception.InvalidCredentialsException;
import com.onepiece.user_service.exception.PasswordValidationException;
import com.onepiece.user_service.exception.UserAlreadyExistsException;
import com.onepiece.user_service.exception.UserNotFoundException;
import com.onepiece.user_service.mapper.UserMapper;
import com.onepiece.user_service.model.User;
import com.onepiece.user_service.model.UserRole;
import com.onepiece.user_service.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO registerRequest) {
        validateRegistrationRequest(registerRequest);
        
        User existingUserByUsername = userRepo.findByUsername(registerRequest.getUsername());
        if (existingUserByUsername != null) {
            throw new UserAlreadyExistsException("Username already exists: " + registerRequest.getUsername());
        }

        User existingUserByEmail = userRepo.findByEmail(registerRequest.getEmail());
        if (existingUserByEmail != null) {
            throw new UserAlreadyExistsException("Email already exists: " + registerRequest.getEmail());
        }

        try {
            User user = userMapper.toEntity(registerRequest);

            validatePassword(user.getPassword());
            user.setPassword(encoder.encode(user.getPassword()));

            User savedUser = userRepo.save(user);

            userRoleService.assignDefaultBuyerRole(savedUser.getUserId());

            List<UserRole> userRoles = userRoleService.getUserRoles(savedUser.getUserId());
            List<String> roles = userRoles.stream()
                    .map(ur -> getRoleName(ur.getRoleId()))
                    .collect(Collectors.toList());

            return userMapper.toRegisterResponseDTO(savedUser, roles);
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        validateLoginRequest(loginRequest);
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername().trim(),
                            loginRequest.getPassword()
                    )
            );

            if (!authentication.isAuthenticated()) {
                throw new InvalidCredentialsException("Authentication failed");
            }

            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername().trim());

            User user = userRepo.findByUsername(loginRequest.getUsername().trim());
            if (user == null) {
                throw new UserNotFoundException("User not found: " + loginRequest.getUsername());
            }

            String token = jwtService.generateToken(userDetails);

            List<UserRole> userRoles = userRoleService.getUserRoles(user.getUserId());
            List<String> roles = userRoles.stream()
                    .map(ur -> getRoleName(ur.getRoleId()))
                    .collect(Collectors.toList());

            return LoginResponseDTO.builder()
                    .token(token)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .userId(user.getUserId())
                    .roles(roles)
                    .message("Login successful")
                    .build();

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        } catch (InvalidCredentialsException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    private String getRoleName(int roleId) {
        switch(roleId) {
            case 1: return "BUYER";
            case 2: return "SELLER";
            case 3: return "ADMIN";
            default: return "USER";
        }
    }

    public boolean resetPassword(String username, String oldPassword, String newPassword) {
        try {
            User user = userRepo.findByUsername(username);
            if (user == null) {
                return false;
            }

            if (!encoder.matches(oldPassword, user.getPassword())) {
                return false;
            }

            if (newPassword == null || newPassword.length() < 6) {
                throw new IllegalArgumentException("New password must be at least 6 characters long");
            }

            user.setPassword(encoder.encode(newPassword));
            userRepo.save(user);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset password: " + e.getMessage());
        }
    }

    private void validateRegistrationRequest(RegisterRequestDTO registerRequest) {
        if (registerRequest == null) {
            throw new IllegalArgumentException("Registration request cannot be null");
        }
        
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        // if (registerRequest.getFirstName() == null || registerRequest.getFirstName().trim().isEmpty()) {
        //     throw new IllegalArgumentException("First name is required");
        // }
        
        // if (registerRequest.getLastName() == null || registerRequest.getLastName().trim().isEmpty()) {
        //     throw new IllegalArgumentException("Last name is required");
        // }
        
        if (!isValidEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (!isValidUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username must be 3-50 characters and contain only letters, numbers, and underscores");
        }
    }
    
    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new PasswordValidationException("Password must be at least 6 characters long");
        }
        
        if (password.length() > 100) {
            throw new PasswordValidationException("Password must not exceed 100 characters");
        }
        
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        
        if (!hasLetter || !hasDigit) {
            throw new PasswordValidationException("Password must contain at least one letter and one number");
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    private boolean isValidUsername(String username) {
        return username.length() >= 3 && username.length() <= 50 && username.matches("^[a-zA-Z0-9_]+$");
    }

    private void validateLoginRequest(LoginRequestDTO loginRequest) {
        if (loginRequest == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }
        
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        if (loginRequest.getUsername().trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
    }
}