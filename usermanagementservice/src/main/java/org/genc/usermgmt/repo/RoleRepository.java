package org.genc.usermgmt.repo;

import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
