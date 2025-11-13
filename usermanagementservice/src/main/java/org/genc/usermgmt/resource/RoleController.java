package org.genc.usermgmt.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.ErrorResponse;
import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.RoleResponseDTO;
import org.genc.usermgmt.exception.ResourceNotFoundException;
import org.genc.usermgmt.service.api.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/userservice/roles")
@Tag(name = "Role Management", description = "Role management endpoints")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create new role")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createRole(
            @Valid @RequestBody RoleRequestDTO request,
            HttpServletRequest servletRequest) {
        try {
            log.info("Creating new role: {}", request.getRoleName());
            RoleResponseDTO response = roleService.createRole(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Role validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            log.error("Error creating role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating role", servletRequest.getRequestURI()));
        }
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Get role by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getRole(
            @PathVariable Integer roleId,
            HttpServletRequest servletRequest) {
        try {
            log.info("Fetching role with id: {}", roleId);
            RoleResponseDTO response = roleService.getRoleById(roleId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.error("Role not found: {}", roleId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of(HttpStatus.NOT_FOUND, e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            log.error("Error fetching role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching role", servletRequest.getRequestURI()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all roles")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        log.info("Fetching all roles");
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "Update role")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateRole(
            @PathVariable Integer roleId,
            @Valid @RequestBody RoleRequestDTO request,
            HttpServletRequest servletRequest) {
        try {
            log.info("Updating role with id: {}", roleId);
            RoleResponseDTO response = roleService.updateRole(roleId, request);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.error("Role not found for update: {}", roleId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of(HttpStatus.NOT_FOUND, e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            log.error("Error updating role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating role", servletRequest.getRequestURI()));
        }
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "Delete role")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteRole(
            @PathVariable Integer roleId,
            HttpServletRequest servletRequest) {
        try {
            log.info("Deleting role with id: {}", roleId);
            roleService.deleteRole(roleId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            log.error("Role not found for deletion: {}", roleId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of(HttpStatus.NOT_FOUND, e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            log.error("Error deleting role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting role", servletRequest.getRequestURI()));
        }
    }
}