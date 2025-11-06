package com.onepiece.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    
    private int userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String contact;
}
