package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationResponseDTO {

    private Integer userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Long contact;
    private String message;
}