package com.onepiece.user_service.repo;

import com.onepiece.user_service.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, Integer> {
    List<UserRole> findByUserId(int userId);
    List<UserRole> findByRoleId(int roleId);
    boolean existsByUserIdAndRoleId(int userId, int roleId);
    void deleteByUserIdAndRoleId(int userId, int roleId);
}
