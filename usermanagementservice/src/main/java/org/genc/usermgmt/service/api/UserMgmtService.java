package org.genc.usermgmt.service.api;

import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.entity.User;

public interface UserMgmtService {

    /**
     * Register a new user with BUYER role (default role)
     * This is called during user registration
     */
    User registerNewUser(UserRegistrationRequestDTO request);

    /**
     * Check if a user exists by username
     */
    boolean isUserExists(String username);

    /**
     * Get user by username
     */
    User getUserByUsername(String username);
}