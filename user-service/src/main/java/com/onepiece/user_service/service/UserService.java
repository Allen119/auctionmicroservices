package com.onepiece.user_service.service;


import com.onepiece.user_service.model.User;
import com.onepiece.user_service.model.UserPrincipal;
import com.onepiece.user_service.model.UserRole;
import com.onepiece.user_service.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username);

        if(user == null){
            System.out.println("user 404 error");
            throw new UsernameNotFoundException("user 404");
        }

        List<UserRole> userRoles = userRoleService.getUserRoles(user.getUserId());
        List<String> roles = userRoles.stream()
                .map(ur -> getRoleName(ur.getRoleId()))
                .collect(Collectors.toList());

        return new UserPrincipal(user, roles);
    }

    private String getRoleName(int roleId) {
        switch(roleId) {
            case 1: return "BUYER";
            case 2: return "SELLER";
            case 3: return "ADMIN";
            default: return "USER";
        }
    }

    public List<UserRole> getUserRoles(int userId){
        return userRoleService.getUserRoles(userId);
    }

    public UserRole assignRoleToUser(int userId,int roleId){
        return userRoleService.assignRoleToUser(userId,roleId);
    }

    public User getUserById(int userId) {
        return userRepo.findById(userId).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public void deleteUserById(int userId) {
        userRepo.deleteById(userId);
    }

    /**
     * Save or update a user
     */
    public User saveUser(User user) {
        return userRepo.save(user);
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    /**
     * Check if user exists by username
     */
    public boolean existsByUsername(String username) {
        return userRepo.findByUsername(username) != null;
    }

    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepo.findByEmail(email) != null;
    }
}
