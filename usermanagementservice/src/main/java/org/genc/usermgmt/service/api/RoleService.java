package org.genc.usermgmt.service.api;

import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.RoleResponseDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;

import java.util.List;

public interface RoleService {

    RoleResponseDTO createRole(RoleRequestDTO request);

    RoleResponseDTO getRoleById(Integer roleId);  // Changed from Long to Integer

    List<RoleResponseDTO> getAllRoles();

    RoleResponseDTO updateRole(Integer roleId, RoleRequestDTO request);  // Changed from Long to Integer

    void deleteRole(Integer roleId);  // Changed from Long to Integer

    Role getRoleByName(RoleType roleType);
}