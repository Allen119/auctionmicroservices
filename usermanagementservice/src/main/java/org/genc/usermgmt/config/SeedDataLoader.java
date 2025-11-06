package org.genc.usermgmt.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.service.api.RoleService;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataLoader implements CommandLineRunner {

    private final UserMgmtService userMgmtService;
    private final RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        // Create or fetch roles
        Role adminRole = roleService.seedRoleData(RoleRequestDTO.builder()
                .name(RoleType.ROLE_ADMIN)
                .description("Admin User Role")
                .build());

        Role userRole = roleService.seedRoleData(RoleRequestDTO.builder()
                .name(RoleType.ROLE_USER)
                .description("Standard User Role")
                .build());

        // Create admin user
        if (userMgmtService.isNewUser("admin")) {
            UserRegistrationRequestDTO userReqDTO = new UserRegistrationRequestDTO("admin", "admin123",
                    "gencadmin@cognizant.com", "ADMIN", "GENC",
                    "9657932761", RoleType.ROLE_ADMIN);
            userMgmtService.registerNewUser(userReqDTO);
        }

        // Create regular user
        if (userMgmtService.isNewUser("user")) {
            UserRegistrationRequestDTO userReqDTO = new UserRegistrationRequestDTO("user", "user123",
                    "gencuser@cognizant.com", "user1", "genc",
                    "6657932766", RoleType.ROLE_USER);
            userMgmtService.registerNewUser(userReqDTO);
        }
    }
}

