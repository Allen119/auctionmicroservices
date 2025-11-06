package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserRegistrationRequestDTO {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private RoleType roleType;
}
