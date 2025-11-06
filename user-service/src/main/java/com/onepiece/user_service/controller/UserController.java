package com.onepiece.user_service.controller;


import com.onepiece.user_service.dto.AssignRoleRequest;
import com.onepiece.user_service.dto.UserProfileDTO;
import com.onepiece.user_service.dto.UserProfileUpdateDTO;
import com.onepiece.user_service.model.User;
import com.onepiece.user_service.model.UserRole;
import com.onepiece.user_service.service.UserRoleService;
import com.onepiece.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/userservice")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    
    @DeleteMapping("/delete/{userId}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                userService.deleteUserById(userId);
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete user: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   
    @PostMapping("/assign-role")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assignRole(@Valid @RequestBody AssignRoleRequest request) {
        try {
            userRoleService.assignRoleToUser(request.getUserId(), request.getRoleId());
            return new ResponseEntity<>("Role assigned successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to assign role: " + e.getMessage(), 
                                      HttpStatus.BAD_REQUEST);
        }
    }

   
    @PostMapping("/assign-seller-role/{userId}")
    // @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#userId).username == authentication.name")
    public ResponseEntity<String> assignSellerRole(@PathVariable int userId) {
        try {
            userRoleService.assignSellerRole(userId);
            return ResponseEntity.ok("Seller role assigned successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                                .body("Failed to assign seller role: " + e.getMessage());
        }
    }

    
    @GetMapping("/roles/{userId}")
    // @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#userId).username == authentication.name")
    public ResponseEntity<List<UserRole>> getUserRoles(@PathVariable int userId) {
        try {
            List<UserRole> userRoles = userRoleService.getUserRoles(userId);
            return ResponseEntity.ok(userRoles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/remove-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeRole(@Valid @RequestBody AssignRoleRequest request) {
        try {
            userRoleService.removeRoleFromUser(request.getUserId(), request.getRoleId());
            return ResponseEntity.ok("Role removed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                                .body("Failed to remove role: " + e.getMessage());
        }
    }

    @GetMapping("/check-role/{userId}/{roleId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#userId).username == authentication.name")
    public ResponseEntity<Boolean> checkUserRole(@PathVariable int userId, @PathVariable int roleId) {
        try {
            boolean hasRole = userRoleService.hasRole(userId, roleId);
            return ResponseEntity.ok(hasRole);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }


    @GetMapping("/profile/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#userId).username == authentication.name")
    public ResponseEntity<?> getUserProfile(@PathVariable int userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                UserProfileDTO profile = UserProfileDTO.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .contact(String.valueOf(user.getContact()))
                        .build();
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                                .body("Failed to get user profile: " + e.getMessage());
        }
    }

    @GetMapping("/check-seller-role/{sellerId}")
    public ResponseEntity<Boolean> checkSellerRole(@PathVariable int sellerId){
        try {
            boolean isSeller = userRoleService.isSeller(sellerId);
            return ResponseEntity.ok(isSeller);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @PutMapping("/profile/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#userId).username == authentication.name")
    public ResponseEntity<String> updateUserProfile(@PathVariable int userId, 
                                                   @Valid @RequestBody UserProfileUpdateDTO updateRequest) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            if (updateRequest.getFirstName() != null) {
                user.setFirstName(updateRequest.getFirstName());
            }
            if (updateRequest.getLastName() != null) {
                user.setLastName(updateRequest.getLastName());
            }
            if (updateRequest.getContact() != null) {
                user.setContact(Long.valueOf(updateRequest.getContact()));
            }

            userService.saveUser(user);
            return ResponseEntity.ok("Profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                                .body("Failed to update profile: " + e.getMessage());
        }
    }


    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
