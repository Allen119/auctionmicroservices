package com.onepiece.user_service.mapper;


import com.onepiece.user_service.dto.RegisterRequestDTO;
import com.onepiece.user_service.dto.RegisterResponseDTO;
import com.onepiece.user_service.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class UserMapper {

    public User toEntity(RegisterRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setContact(Long.parseLong(dto.getContact()));
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());

        return user;
    }

    public RegisterResponseDTO toRegisterResponseDTO(User user, List<String> roles) {
        return RegisterResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .roles(roles)
                .message("User registered successfully")
                .build();
    }
}

