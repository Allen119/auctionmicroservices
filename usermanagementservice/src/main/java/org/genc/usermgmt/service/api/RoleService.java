package org.genc.usermgmt.service.api;

import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.RoleResponseDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;

import java.util.List;

public interface RoleService {
    RoleResponseDTO createRole(RoleRequestDTO request);
    RoleResponseDTO getRoleById(Long id);
    List<RoleResponseDTO> getAllRoles();
    RoleResponseDTO updateRole(Long id, RoleRequestDTO request);
    void deleteRole(Long id);
    Role getRoleByName(RoleType roleType);
    public Role seedRoleData(RoleRequestDTO request);
}
