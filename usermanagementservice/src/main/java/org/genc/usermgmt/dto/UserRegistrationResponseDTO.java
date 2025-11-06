package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;

import java.util.Set;

@Data
@Builder
public class UserRegistrationResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String userMessage;
    private Set<RoleType> roles;
}
