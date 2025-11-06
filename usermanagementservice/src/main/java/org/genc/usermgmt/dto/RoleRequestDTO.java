package org.genc.usermgmt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.genc.usermgmt.enums.RoleType;

@Data
@Builder
public class RoleRequestDTO {
    @NotNull(message = "Role name is required")
    private RoleType name;
    private String description;
}