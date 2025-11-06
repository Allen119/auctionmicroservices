package com.onepiece.user_service.service;


import com.onepiece.user_service.model.UserRole;
import com.onepiece.user_service.repo.UserRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepo userRoleRepo;

    private static final int BUYER_ROLE_ID = 1;
    private static final int SELLER_ROLE_ID = 2;
    private static final int ADMIN_ROLE_ID = 3;

    public UserRole assignRoleToUser(int userId, int roleId){
        if(userRoleRepo.existsByUserIdAndRoleId(userId,roleId)){
            throw new RuntimeException("User already has this role");
        }

        UserRole userRole = new UserRole();

        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setCreatedAt(LocalDate.now());
        userRole.setUpdatedAt(LocalDate.now());
        userRole.setVersion(1);

        return userRoleRepo.save(userRole);
    }

    public UserRole assignBuyerRole(int userId){
        return assignRoleToUser(userId, BUYER_ROLE_ID);
    }

    public UserRole assignSellerRole(int userId){
        return assignRoleToUser(userId, SELLER_ROLE_ID);
    }

    public UserRole assignAdminRole(int userId){
        return assignRoleToUser(userId,ADMIN_ROLE_ID);
    }

    public void assignDefaultBuyerRole(int userId){
        try {
            assignBuyerRole(userId);
        } catch (RuntimeException e){

        }
    }

    public List<UserRole> getUserRoles(int userId){
        return userRoleRepo.findByUserId(userId);
    }

    public void removeRoleFromUser(int userId, int roleId) {
        userRoleRepo.deleteByUserIdAndRoleId(userId,roleId);
    }

    public boolean hasRole(int userId, int roleId) {
        return userRoleRepo.existsByUserIdAndRoleId(userId, roleId);
    }

    public boolean isBuyer(int userId) {
        return hasRole(userId, BUYER_ROLE_ID);
    }

    public boolean isSeller(int userId) {
        return hasRole(userId, SELLER_ROLE_ID);
    }

    public boolean isAdmin(int userId) {
        return hasRole(userId, ADMIN_ROLE_ID);
    }
}
