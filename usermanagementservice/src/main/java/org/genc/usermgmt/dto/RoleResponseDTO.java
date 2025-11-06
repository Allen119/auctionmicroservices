package org.genc.usermgmt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponseDTO {
    private Long id;
    private String name;
    private String description;
}
