package org.genc.usermgmt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.RoleResponseDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.exception.ResourceNotFoundException;
import org.genc.usermgmt.repo.RoleRepository;
import org.genc.usermgmt.service.api.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponseDTO createRole(RoleRequestDTO request) {
        // Check if role already exists
        if (roleRepository.findByRoleName(request.getRoleName().name()).isPresent()) {
            log.warn("Role already exists: {}", request.getRoleName().name());
            throw new IllegalArgumentException("Role already exists: " + request.getRoleName().name());
        }

        // Create new role
        Role role = new Role();
        role.setRoleName(request.getRoleName().name()); // "BUYER", "SELLER", "ADMIN"

        Role savedRole = roleRepository.save(role);
        log.info("Role created: {}", savedRole.getRoleName());

        return mapToDTO(savedRole);
    }

    @Override
    public RoleResponseDTO getRoleById(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        return mapToDTO(role);
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO updateRole(Integer roleId, RoleRequestDTO request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        role.setRoleName(request.getRoleName().name());
        Role updatedRole = roleRepository.save(role);

        log.info("Role updated: {}", updatedRole.getRoleName());
        return mapToDTO(updatedRole);
    }

    @Override
    public void deleteRole(Integer roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }
        roleRepository.deleteById(roleId);
        log.info("Role deleted with id: {}", roleId);
    }

    @Override
    public Role getRoleByName(RoleType roleType) {
        Role role = roleRepository.findByRoleName(roleType.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleType.name()));
        return role;
    }

    private RoleResponseDTO mapToDTO(Role role) {
        return RoleResponseDTO.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .build();
    }
}