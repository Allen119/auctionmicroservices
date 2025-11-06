package com.onepiece.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponseDTO {

    private int userId;
    private String username;
    private String email;
    private LocalDate createdAt;
    private List<String> roles;
    private String message;
}

