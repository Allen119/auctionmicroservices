package org.genc.usermgmt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.exception.UserAlreadyExistsException;
import org.genc.usermgmt.repo.RoleRepository;
import org.genc.usermgmt.repo.UserRepository;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMgmtServiceImpl implements UserMgmtService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user with BUYER role (default role)
     * This is called during user registration via AuthService
     */
    @Override
    public User registerNewUser(UserRegistrationRequestDTO request) {
        log.info("Registering new user with username: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.error("Registration failed - Username already exists: {}", request.getUsername());
            throw new UserAlreadyExistsException(request.getUsername() + " already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("Registration failed - Email already registered: {}", request.getEmail());
            throw new UserAlreadyExistsException("Email " + request.getEmail() + " already registered");
        }

        // Fetch the BUYER role (default role for all new users)
        Role buyerRole = roleRepository.findByRoleName(RoleType.BUYER.name())
                .orElseThrow(() -> {
                    log.error("BUYER role not found in database");
                    return new RuntimeException("BUYER role not found");
                });

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        try {
            user.setContact(Long.parseLong(request.getContact()));
        } catch (NumberFormatException e) {
            log.error("Invalid contact number format: {}", request.getContact());
            throw new IllegalArgumentException("Invalid contact number format");
        }

        // Assign BUYER role
        user.getRoles().add(buyerRole);

        User savedUser = userRepository.save(user);
        log.info("New user registered successfully - Username: {}, Email: {}, Role: BUYER",
                savedUser.getUsername(), savedUser.getEmail());

        return savedUser;
    }

    /**
     * Check if a user exists by username
     */
    @Override
    public boolean isUserExists(String username) {
        boolean exists = userRepository.findByUsername(username).isPresent();
        log.debug("User exists check - Username: {}, Exists: {}", username, exists);
        return exists;
    }

    /**
     * Get user by username
     */
    @Override
    public User getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new RuntimeException("User not found: " + username);
                });
    }
}