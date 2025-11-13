package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String jwt;
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private Long contact;
    private String appInstance;
}