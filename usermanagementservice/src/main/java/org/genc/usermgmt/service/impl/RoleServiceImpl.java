package org.genc.usermgmt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.RoleResponseDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.exception.RecordAlreadyExistsException;
import org.genc.usermgmt.exception.ResourceNotFoundException;
import org.genc.usermgmt.repo.RoleRepository;
import org.genc.usermgmt.service.api.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponseDTO createRole(RoleRequestDTO request) {
        Optional<Role> roleEntity = roleRepository.findByName(request.getName());
        if(roleEntity.isEmpty()) {
            Role role = Role.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .build();

            Role savedRole = roleRepository.save(role);
            return mapToDTO(savedRole);
        }
        log.warn(" Role already exists {}",roleEntity.get().getName().toString());
        return mapToDTO(roleEntity.get());
    }


    public Role seedRoleData(RoleRequestDTO request) {
        Optional<Role> roleEntity = roleRepository.findByName(request.getName());
        if(roleEntity.isEmpty()) {
            Role role = Role.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .build();

            return roleRepository.save(role);
        }
        return roleEntity.get();
    }

    @Override
    public RoleResponseDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return mapToDTO(role);
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    @Override
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        role.setName(request.getName());  // Now accepts RoleType directly
        role.setDescription(request.getDescription());

        Role updatedRole = roleRepository.save(role);
        return mapToDTO(updatedRole);
    }


    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    public Role getRoleByName(RoleType roleType) {
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with : " + roleType.toString()));;
        return role;
    }

    private RoleResponseDTO mapToDTO(Role role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName().toString())
                .description(role.getDescription())
                .build();
    }
}
